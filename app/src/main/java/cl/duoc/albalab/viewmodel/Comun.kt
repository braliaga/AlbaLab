package cl.duoc.albalab.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import cl.duoc.albalab.data.local.AppDatabase
import cl.duoc.albalab.data.remote.ErrorApi
import cl.duoc.albalab.data.remote.RedAlbaLab
import cl.duoc.albalab.repository.RegistroRepository

enum class Campo { NOMBRE, TELEFONO, CORREO, DIRECCION, PERSONAS, FECHA, CONTACTO, TIPO_AYUDA, ACEPTA }

internal fun Map<Campo, String>.conError(campo: Campo, error: String?): Map<Campo, String> =
    if (error == null) this - campo else this + (campo to error)

// Deja solo los campos que tienen error, descartando los válidos (null).
internal fun soloErrores(vararg pares: Pair<Campo, String?>): Map<Campo, String> =
    pares.mapNotNull { (campo, error) -> error?.let { campo to it } }.toMap()

// Usado por las factories de los tres ViewModels de registros: caché Room + ms-vinculacion por el gateway.
internal fun CreationExtras.repositorio() =
    RegistroRepository(AppDatabase.obtener(this[APPLICATION_KEY]!!).registroDao(), RedAlbaLab.api)

// Mensaje para mostrar en pantalla a partir del error de una llamada remota.
internal fun Throwable.mensaje(): String = message ?: "Ocurrió un error inesperado. Inténtalo de nuevo."

internal val Throwable.esSesionExpirada: Boolean get() = this is ErrorApi.NoAutorizado

// Lo implementa utils/Notificaciones. Es una interfaz para que el ViewModel no dependa de Android y se pueda testear.
fun interface Notificador {
    fun registroGuardado(nombre: String)
}
