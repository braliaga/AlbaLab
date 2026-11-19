package cl.duoc.albalab.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.repository.RegistroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EdicionState(
    val id: Int = 0,
    val telefono: String = "",
    val contacto: ContactoPreferido? = null,
    val recibeNovedades: Boolean = false,
    val errores: Map<Campo, String> = emptyMap(),
    val guardadoOk: Boolean = false
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
                    telefono = r.telefono,
                    contacto = r.contactoPreferido,
                    recibeNovedades = r.recibeNovedades
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

        viewModelScope.launch {
            val original = repo.obtenerPorId(e.id) ?: return@launch
            repo.actualizar(
                original.copy(
                    telefono = e.telefono,
                    contactoPreferido = e.contacto!!,
                    recibeNovedades = e.recibeNovedades
                )
            )
            _estado.update { it.copy(errores = emptyMap(), guardadoOk = true) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { EdicionViewModel(repositorio(), createSavedStateHandle()) }
        }
    }
}
