package cl.duoc.albalab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.RegistroInteres
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.repository.RegistroRepository
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class RegistroViewModel(private val repo: RegistroRepository) : ViewModel() {
    private val _formulario = MutableStateFlow(FormularioState())
    val formulario: StateFlow<FormularioState> = _formulario.asStateFlow()

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
        val errores = soloErrores(
            Campo.NOMBRE to Validaciones.nombre(s.nombre),
            Campo.TELEFONO to Validaciones.telefono(s.telefono),
            Campo.CORREO to Validaciones.correo(s.correo),
            Campo.DIRECCION to Validaciones.direccion(s.direccion),
            Campo.PERSONAS to Validaciones.personas(s.personas),
            Campo.FECHA to Validaciones.fecha(s.fecha, Date()),
            Campo.CONTACTO to Validaciones.contacto(s.contacto),
            Campo.TIPO_AYUDA to Validaciones.tipoAyuda(s.tipoAyuda),
            Campo.ACEPTA to Validaciones.aceptaContacto(s.aceptaContacto)
        )

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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { RegistroViewModel(repositorio()) }
        }
    }
}
