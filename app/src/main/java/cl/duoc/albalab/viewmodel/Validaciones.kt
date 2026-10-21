package cl.duoc.albalab.viewmodel

import cl.duoc.albalab.model.ContactoPreferido
import cl.duoc.albalab.model.TipoAyuda
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Cada función devuelve el mensaje de error, o null si el valor es válido.
object Validaciones {
    private val regexCorreo = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")
    private val regexFecha = Regex("""^\d{2}/\d{2}/\d{4}$""")

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "El campo no puede estar vacío"
        valor.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
        else -> null
    }

    fun telefono(valor: String): String? =
        if (valor.length == 9 && valor.all { it.isDigit() }) null
        else "Solo se permiten 9 dígitos, sin espacios vacíos ni símbolos ni carácteres"

    fun correo(valor: String): String? =
        if (regexCorreo.matches(valor)) null
        else "Ingresa un correo válido, por ejemplo nombre@correo.cl"

    fun direccion(valor: String): String? = when {
        valor.isBlank() -> "La dirección no puede estar vacía"
        valor.trim().length < 5 -> "La dirección debe tener al menos 5 caracteres"
        else -> null
    }

    fun personas(valor: String): String? {
        val n = valor.toIntOrNull()
        return if (n != null && n in 1..500) null
        else "Ingresa un número de personas entre 1 y 500"
    }

    fun fecha(valor: String, hoy: Date): String? {
        // El formato se valida antes de parsear porque parse() acepta texto sobrante al final.
        if (!regexFecha.matches(valor)) return "Usa el formato dd/MM/aaaa, por ejemplo 15/11/2026"
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { isLenient = false }
        val fecha = try {
            formato.parse(valor)
        } catch (e: java.text.ParseException) {
            null
        } ?: return "La fecha no existe en el calendario"
        return if (fecha.before(inicioDelDia(hoy))) "La fecha no puede ser anterior a hoy" else null
    }

    fun contacto(valor: ContactoPreferido?): String? =
        if (valor == null) "Elige cómo prefieres que te contactemos" else null

    fun tipoAyuda(valor: TipoAyuda?): String? =
        if (valor == null) "Elige un tipo de ayuda" else null

    fun aceptaContacto(valor: Boolean): String? =
        if (valor) null else "Debes aceptar que te contactemos para enviar el registro"

    private fun inicioDelDia(fecha: Date): Date = Calendar.getInstance().apply {
        time = fecha
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}
