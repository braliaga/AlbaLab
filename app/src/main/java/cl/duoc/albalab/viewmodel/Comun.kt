package cl.duoc.albalab.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import cl.duoc.albalab.data.local.AppDatabase
import cl.duoc.albalab.repository.RegistroRepository

enum class Campo { NOMBRE, TELEFONO, CORREO, DIRECCION, PERSONAS, FECHA, CONTACTO, TIPO_AYUDA, ACEPTA }

internal fun Map<Campo, String>.conError(campo: Campo, error: String?): Map<Campo, String> =
    if (error == null) this - campo else this + (campo to error)

// Deja solo los campos que tienen error, descartando los válidos (null).
internal fun soloErrores(vararg pares: Pair<Campo, String?>): Map<Campo, String> =
    pares.mapNotNull { (campo, error) -> error?.let { campo to it } }.toMap()

// Usado por las factories de los tres ViewModels.
internal fun CreationExtras.repositorio() =
    RegistroRepository(AppDatabase.obtener(this[APPLICATION_KEY]!!).registroDao())
