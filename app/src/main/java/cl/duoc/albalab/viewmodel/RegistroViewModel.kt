package cl.duoc.albalab.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.albalab.data.AppDatabase
import cl.duoc.albalab.data.RegistroRepository
import cl.duoc.albalab.model.ContactoPreferido
import cl.duoc.albalab.model.RegistroInteres
import cl.duoc.albalab.model.TipoAyuda
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Campo { NOMBRE, TELEFONO, CORREO, DIRECCION, PERSONAS, FECHA, CONTACTO, TIPO_AYUDA, ACEPTA }

data class FormularioState(
    val nombre: String = "",
    val telefono: String = "",
    val correo: String = "",
    val direccion: String = "",
    val personas: String = "",
    val fecha: String = "",
    val contacto: ContactoPreferido? = null,
    val tipoAyuda: TipoAyuda? = null,
    val aceptaContacto: Boolean = false,
    val recibeNovedades: Boolean = false,
    val fotoPath: String? = null,
    val errores: Map<Campo, String> = emptyMap(),
    val guardando: Boolean = false,
    val guardadoOk: Boolean = false
)

data class EdicionState(
    val id: Int = 0,
    val telefono: String = "",
    val contacto: ContactoPreferido? = null,
    val recibeNovedades: Boolean = false,
    val errores: Map<Campo, String> = emptyMap(),
    val guardadoOk: Boolean = false
)

class RegistroViewModel(private val repo: RegistroRepository) : ViewModel() {
    private val _formulario = MutableStateFlow(FormularioState())
    val formulario: StateFlow<FormularioState> = _formulario.asStateFlow()

    private val _edicion = MutableStateFlow(EdicionState())
    val edicion: StateFlow<EdicionState> = _edicion.asStateFlow()

    val registros: StateFlow<List<RegistroInteres>> = repo.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onCambio(campo: Campo, valor: String) {
        _formulario.update { s ->
            val nuevo = when (campo) {
                Campo.NOMBRE -> s.copy(nombre = valor)
                Campo.TELEFONO -> s.copy(telefono = valor)
                Campo.CORREO -> s.copy(correo = valor)
                Campo.DIRECCION -> s.copy(direccion = valor)
                Campo.PERSONAS -> s.copy(personas = valor)
                Campo.FECHA -> s.copy(fecha = valor)
                else -> s
            }
            val error = when (campo) {
                Campo.NOMBRE -> Validaciones.nombre(valor)
                Campo.TELEFONO -> Validaciones.telefono(valor)
                Campo.CORREO -> Validaciones.correo(valor)
                Campo.DIRECCION -> Validaciones.direccion(valor)
                Campo.PERSONAS -> Validaciones.personas(valor)
                Campo.FECHA -> Validaciones.fecha(valor, Date())
                else -> null
            }
            nuevo.copy(errores = nuevo.errores.conError(campo, error), guardadoOk = false)
        }
    }

    fun onContacto(c: ContactoPreferido) = _formulario.update {
        it.copy(contacto = c, errores = it.errores - Campo.CONTACTO)
    }

    fun onTipoAyuda(t: TipoAyuda) = _formulario.update {
        it.copy(tipoAyuda = t, errores = it.errores - Campo.TIPO_AYUDA)
    }

    fun onAceptaContacto(v: Boolean) = _formulario.update {
        it.copy(aceptaContacto = v, errores = it.errores.conError(Campo.ACEPTA, Validaciones.aceptaContacto(v)))
    }

    fun onRecibeNovedades(v: Boolean) = _formulario.update { it.copy(recibeNovedades = v) }

    fun onFoto(path: String?) = _formulario.update { it.copy(fotoPath = path) }

    fun guardar() {
        val s = _formulario.value
        val errores = mapOf(
            Campo.NOMBRE to Validaciones.nombre(s.nombre),
            Campo.TELEFONO to Validaciones.telefono(s.telefono),
            Campo.CORREO to Validaciones.correo(s.correo),
            Campo.DIRECCION to Validaciones.direccion(s.direccion),
            Campo.PERSONAS to Validaciones.personas(s.personas),
            Campo.FECHA to Validaciones.fecha(s.fecha, Date()),
            Campo.CONTACTO to Validaciones.contacto(s.contacto),
            Campo.TIPO_AYUDA to Validaciones.tipoAyuda(s.tipoAyuda),
            Campo.ACEPTA to Validaciones.aceptaContacto(s.aceptaContacto)
        ).mapNotNull { (campo, error) -> error?.let { campo to it } }.toMap()

        if (errores.isNotEmpty()) {
            _formulario.update { it.copy(errores = errores, guardadoOk = false) }
            return
        }

        _formulario.update { it.copy(errores = emptyMap(), guardando = true) }
        viewModelScope.launch {
            repo.insertar(
                RegistroInteres(
                    nombre = s.nombre.trim(),
                    telefono = s.telefono,
                    correo = s.correo,
                    direccion = s.direccion.trim(),
                    personas = s.personas.toInt(),
                    fechaContacto = s.fecha,
                    contactoPreferido = s.contacto!!,
                    tipoAyuda = s.tipoAyuda!!,
                    aceptaContacto = s.aceptaContacto,
                    recibeNovedades = s.recibeNovedades,
                    fotoPath = s.fotoPath
                )
            )
            _formulario.value = FormularioState(guardadoOk = true)
        }
    }

    fun cargarEdicion(id: Int) {
        viewModelScope.launch {
            val r = repo.obtenerPorId(id) ?: return@launch
            _edicion.value = EdicionState(
                id = r.id,
                telefono = r.telefono,
                contacto = r.contactoPreferido,
                recibeNovedades = r.recibeNovedades
            )
        }
    }

    fun onEdicionTelefono(v: String) = _edicion.update {
        it.copy(telefono = v, errores = it.errores.conError(Campo.TELEFONO, Validaciones.telefono(v)))
    }

    fun onEdicionContacto(c: ContactoPreferido) = _edicion.update {
        it.copy(contacto = c, errores = it.errores - Campo.CONTACTO)
    }

    fun onEdicionNovedades(v: Boolean) = _edicion.update { it.copy(recibeNovedades = v) }

    fun guardarEdicion() {
        val e = _edicion.value
        val errores = mapOf(
            Campo.TELEFONO to Validaciones.telefono(e.telefono),
            Campo.CONTACTO to Validaciones.contacto(e.contacto)
        ).mapNotNull { (campo, error) -> error?.let { campo to it } }.toMap()

        if (errores.isNotEmpty()) {
            _edicion.update { it.copy(errores = errores) }
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
            _edicion.update { it.copy(errores = emptyMap(), guardadoOk = true) }
        }
    }

    private fun Map<Campo, String>.conError(campo: Campo, error: String?): Map<Campo, String> =
        if (error == null) this - campo else this + (campo to error)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                RegistroViewModel(RegistroRepository(AppDatabase.obtener(app).registroDao()))
            }
        }
    }
}
