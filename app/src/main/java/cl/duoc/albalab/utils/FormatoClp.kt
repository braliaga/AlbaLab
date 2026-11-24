package cl.duoc.albalab.utils

import java.text.NumberFormat
import java.util.Locale

fun Int.aPesosChilenos(): String {
    val formato = NumberFormat.getIntegerInstance(Locale("es", "CL"))
    return "$${formato.format(this)}"
}

private val chile = Locale("es", "CL")

// 41391.69 -> "$41.391,69"
fun Double.aPesosConDecimales(): String {
    val formato = NumberFormat.getNumberInstance(chile).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "$${formato.format(this)}"
}

// Precio del catálogo expresado en UF y dólares con los valores de mindicador.cl: "≈ 0,45 UF · US$ 18,19".
fun equivalenteUfUsd(precioClp: Int, uf: Double, dolar: Double): String {
    val dosDecimales = NumberFormat.getNumberInstance(chile).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "≈ ${dosDecimales.format(precioClp / uf)} UF · US$ ${dosDecimales.format(precioClp / dolar)}"
}
