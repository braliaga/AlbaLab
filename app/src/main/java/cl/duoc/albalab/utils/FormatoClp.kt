package cl.duoc.albalab.utils

import java.text.NumberFormat
import java.util.Locale

fun Int.aPesosChilenos(): String {
    val formato = NumberFormat.getIntegerInstance(Locale("es", "CL"))
    return "$${formato.format(this)}"
}