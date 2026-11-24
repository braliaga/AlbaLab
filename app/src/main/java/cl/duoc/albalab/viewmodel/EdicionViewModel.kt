package cl.duoc.albalab.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.EstadoSolicitud
import cl.duoc.albalab.repository.RegistroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EdicionState(
    val id: Int = 0,
    val nombre: String = "",
    val telefono: String = "",
    val contacto: ContactoPreferido? = null,
    val recibeNovedades: Boolean = false,
    // Estado guardado en el servidor y el que el gestor eligió en los chips (se envía solo si cambió).
    val estadoActual: EstadoSolicitud = EstadoSolicitud.RECIBIDA,
    val estadoElegido: EstadoSolicitud = EstadoSolicitud.RECIBIDA,
    val errores: Map<Campo, String> = emptyMap(),
    val procesando: Boolean = false,
    val error: String? = null,
    val sesionExpirada: Boolean = false,
    val guardadoOk: Boolean = false,
    val eliminado: Boolean = false
)

// El id llega por el argumento "id" de la ruta de navegación, a través de SavedStateHandle.
class EdicionViewModel(
    private val repo: RegistroRepository,
    estadoGuardado: SavedStateHandle
) : ViewModel() {
    private val id: Int = checkNotNull(estadoGuardado["id"])

    private val _estado = MutableStateFlow(EdicionState(id = id))
    val estado: StateFlow<EdicionState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val r = repo.obtenerPorId(id) ?: return@launch
            _estado.update {
                it.copy(
                    nombre = r.nombre,
                    telefono = r.telefono,
                    contacto = r.contactoPreferido,
                    recibeNovedades = r.recibeNovedades,
                    estadoActual = r.estado,
                    estadoElegido = r.estado
                )
            }
        }
    }

    fun onTelefono(v: String) = _estado.update {
        it.copy(telefono = v, errores = it.errores.conError(Campo.TELEFONO, Validaciones.telefono(v)))
    }

    fun onContacto(c: ContactoPreferido) = _estado.update {
        it.copy(contacto = c, errores = it.errores - Campo.CONTACTO)
    }

    fun onNovedades(v: Boolean) = _estado.update { it.copy(recibeNovedades = v) }

    fun onEstado(e: EstadoSolicitud) = _estado.update {
        if (it.estadoActual.esFinal) it else it.copy(estadoElegido = e)
    }

    // PUT con los datos editables y, si el gestor eligió otro estado, PATCH /estado (queda en el historial).
    fun guardar() {
        val e = _estado.value
        val errores = soloErrores(
            Campo.TELEFONO to Validaciones.telefono(e.telefono),
            Campo.CONTACTO to Validaciones.contacto(e.contacto)
        )

        if (errores.isNotEmpty()) {
            _estado.update { it.copy(errores = errores) }
            return
        }

        _estado.update { it.copy(errores = emptyMap(), procesando = true, error = null) }
        viewModelScope.launch {
            val resultado = repo.actualizar(e.id, e.telefono, e.contacto!!, e.recibeNovedades).mapCatching {
                if (e.estadoElegido != e.estadoActual) {
                    repo.cambiarEstado(e.id, e.estadoElegido, "Cambio desde la app").getOrThrow()
                }
            }
            resultado
                .onSuccess { _estado.update { it.copy(procesando = false, guardadoOk = true) } }
                .onFailure { err -> fallo(err) }
        }
    }

    // DELETE /solicitudes/{id}. La pantalla pide confirmación antes de llamar.
    fun eliminar() {
        _estado.update { it.copy(procesando = true, error = null) }
        viewModelScope.launch {
            repo.eliminar(id)
                .onSuccess { _estado.update { it.copy(procesando = false, eliminado = true) } }
                .onFailure { err -> fallo(err) }
        }
    }

    private fun fallo(err: Throwable) = _estado.update {
        it.copy(procesando = false, error = err.mensaje(), sesionExpirada = err.esSesionExpirada)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { EdicionViewModel(repositorio(), createSavedStateHandle()) }
        }
    }
}
