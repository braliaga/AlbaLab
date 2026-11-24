package cl.duoc.albalab

import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.EstadoSolicitud
import cl.duoc.albalab.data.model.RegistroInteres
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.data.remote.dto.SolicitudDto
import java.util.Calendar
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

// Datos de prueba ficticios compartidos por los tests.
object Fixtures {
    // Año siguiente al actual: las fechas de prueba nunca quedan "en el pasado" para Validaciones.fecha.
    val anioSiguiente: Int = Calendar.getInstance().get(Calendar.YEAR) + 1

    fun registro(
        id: Int = 1,
        nombre: String = "Ana Pérez",
        tipo: TipoAyuda = TipoAyuda.DONACION,
        estado: EstadoSolicitud = EstadoSolicitud.RECIBIDA,
        fotoPath: String? = null
    ) = RegistroInteres(
        id = id,
        nombre = nombre,
        telefono = "912345678",
        correo = "ana@mail.cl",
        direccion = "Calle 123",
        personas = 3,
        fechaContacto = "15/12/$anioSiguiente",
        contactoPreferido = ContactoPreferido.CORREO,
        tipoAyuda = tipo,
        aceptaContacto = true,
        recibeNovedades = true,
        fotoPath = fotoPath,
        estado = estado
    )

    fun dto(r: RegistroInteres) = SolicitudDto(
        id = r.id,
        nombre = r.nombre,
        telefono = r.telefono,
        correo = r.correo,
        direccion = r.direccion,
        personas = r.personas,
        fechaContacto = r.fechaContacto,
        contactoPreferido = r.contactoPreferido.name,
        tipoAyuda = r.tipoAyuda.name,
        aceptaContacto = r.aceptaContacto,
        recibeNovedades = r.recibeNovedades,
        fotoPath = r.fotoPath,
        estado = r.estado.name
    )

    fun httpError(codigo: Int, cuerpo: String = "") =
        HttpException(Response.error<Any>(codigo, cuerpo.toResponseBody()))
}
