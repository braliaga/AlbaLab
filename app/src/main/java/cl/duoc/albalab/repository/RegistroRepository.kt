package cl.duoc.albalab.repository

import cl.duoc.albalab.data.local.RegistroDao
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.EstadoSolicitud
import cl.duoc.albalab.data.model.RegistroInteres
import cl.duoc.albalab.data.remote.ApiAlbaLab
import cl.duoc.albalab.data.remote.ErrorApi
import cl.duoc.albalab.data.remote.Sesion
import cl.duoc.albalab.data.remote.dto.CambioEstadoDto
import cl.duoc.albalab.data.remote.dto.SolicitudRequestDto
import cl.duoc.albalab.data.remote.dto.SolicitudUpdateDto
import cl.duoc.albalab.data.remote.llamarApi
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

// ms-vinculacion es la fuente de verdad; Room guarda una copia para mostrar la lista al instante y sin conexión.
class RegistroRepository(private val dao: RegistroDao, private val api: ApiAlbaLab) {

    fun obtenerTodos(): Flow<List<RegistroInteres>> = dao.obtenerTodos()

    suspend fun obtenerPorId(id: Int): RegistroInteres? = dao.obtenerPorId(id)

    // POST /solicitudes: lo usa el visitante desde el formulario (no requiere sesión).
    suspend fun crear(r: RegistroInteres): Result<RegistroInteres> = llamarApi {
        val creado = api.crearSolicitud(SolicitudRequestDto.desde(r)).aModelo()
        dao.insertar(creado)
        creado
    }

    // GET /solicitudes: reemplaza la caché por la lista del servidor.
    suspend fun refrescar(): Result<Unit> = llamarGestor {
        val lista = api.listarSolicitudes().map { conFotoLocal(it.aModelo()) }
        dao.reemplazarTodos(lista)
    }

    suspend fun actualizar(
        id: Int,
        telefono: String,
        contacto: ContactoPreferido,
        recibeNovedades: Boolean
    ): Result<RegistroInteres> = llamarGestor {
        val dto = api.actualizarSolicitud(id, SolicitudUpdateDto(telefono, contacto.name, recibeNovedades))
        conFotoLocal(dto.aModelo()).also { dao.insertar(it) }
    }

    suspend fun cambiarEstado(id: Int, estado: EstadoSolicitud, observacion: String?): Result<RegistroInteres> =
        llamarGestor {
            val dto = api.cambiarEstado(id, CambioEstadoDto(estado.name, observacion))
            conFotoLocal(dto.aModelo()).also { dao.insertar(it) }
        }

    suspend fun eliminar(id: Int): Result<Unit> = llamarGestor {
        val respuesta = api.eliminarSolicitud(id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
        dao.eliminar(id)
    }

    // La foto solo existe en el teléfono que la tomó: si el servidor no la trae, se conserva la de la caché.
    private suspend fun conFotoLocal(r: RegistroInteres): RegistroInteres =
        if (r.fotoPath != null) r else r.copy(fotoPath = dao.obtenerPorId(r.id)?.fotoPath)

    // Las operaciones de gestión exigen JWT: si el gateway responde 401, la sesión se cierra para volver al login.
    private suspend fun <T> llamarGestor(bloque: suspend () -> T): Result<T> =
        llamarApi(bloque).onFailure { if (it is ErrorApi.NoAutorizado) Sesion.cerrar() }
}
