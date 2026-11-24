package cl.duoc.albalab.repository

import cl.duoc.albalab.data.remote.ApiFeriados
import cl.duoc.albalab.data.remote.llamarApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class Feriado(val fechaIso: String, val nombre: String) {
    // "2026-09-18" -> "18/09"
    val fechaCorta: String get() = "${fechaIso.substring(8, 10)}/${fechaIso.substring(5, 7)}"
}

private val regexFecha = Regex("""(\d{1,2})/(\d{1,2})/(\d{4})""")

// "8/12/2026" -> "2026-12-08". Sin java.time porque la app soporta Android 7 (API 24).
fun aIso(fecha: String): String? = regexFecha.matchEntire(fecha.trim())?.destructured?.let { (d, m, a) ->
    "$a-${m.padStart(2, '0')}-${d.padStart(2, '0')}"
}

fun hoyIso(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

// Las fechas ISO se ordenan bien como texto, así que basta compararlas.
fun proximoDesde(hoy: String, feriados: List<Feriado>): Feriado? =
    feriados.filter { it.fechaIso >= hoy }.minByOrNull { it.fechaIso }

// API externa Nager.Date: feriados oficiales de Chile. Se piden una vez por año y quedan en memoria.
class FeriadosRepository(private val api: ApiFeriados) {
    private val cache = mutableMapOf<Int, List<Feriado>>()
    private val mutex = Mutex()

    suspend fun delAnio(anio: Int): Result<List<Feriado>> = mutex.withLock {
        cache[anio]?.let { return Result.success(it) }
        llamarApi { api.feriados(anio).map { Feriado(it.date, it.localName) } }
            .onSuccess { cache[anio] = it }
    }

    // Feriado que cae en la fecha escrita en el formulario (dd/MM/aaaa), o null.
    suspend fun feriadoEn(fecha: String): Feriado? {
        val iso = aIso(fecha) ?: return null
        return delAnio(iso.take(4).toInt()).getOrNull()?.firstOrNull { it.fechaIso == iso }
    }

    // Próximo feriado desde hoy; mira también el año siguiente por si quedan pocos días del actual.
    suspend fun proximo(hoy: String = hoyIso()): Result<Feriado?> {
        val anio = hoy.take(4).toInt()
        return delAnio(anio).map { esteAnio ->
            proximoDesde(hoy, esteAnio) ?: proximoDesde(hoy, delAnio(anio + 1).getOrDefault(emptyList()))
        }
    }
}
