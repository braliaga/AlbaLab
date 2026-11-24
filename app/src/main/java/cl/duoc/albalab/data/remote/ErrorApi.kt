package cl.duoc.albalab.data.remote

import cl.duoc.albalab.data.remote.dto.ErrorApiDto
import com.google.gson.Gson
import java.io.IOException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

// Errores que la interfaz sabe mostrar. Ninguna llamada remota deja escapar una excepción: si la app
// se cae en la defensa, no se evalúa nada de lo que sigue.
sealed class ErrorApi(mensaje: String) : Exception(mensaje) {
    class SinConexion : ErrorApi("No pudimos conectar con el servidor. Revisa tu conexión e inténtalo de nuevo.")
    class NoAutorizado(mensaje: String) : ErrorApi(mensaje)
    class Servidor(mensaje: String) : ErrorApi(mensaje)
}

private val gson = Gson()

// Envuelve una llamada Retrofit en Result y traduce cualquier falla a un ErrorApi con mensaje en español.
suspend fun <T> llamarApi(bloque: suspend () -> T): Result<T> = try {
    Result.success(bloque())
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    // El cuerpo de error solo se puede leer una vez: se lee aquí y se reutiliza.
    val delServidor = mensajeDelServidor(e)
    Result.failure(
        if (e.code() == 401) ErrorApi.NoAutorizado(delServidor ?: "Tu sesión expiró. Ingresa nuevamente.")
        else ErrorApi.Servidor(delServidor ?: "El servidor respondió con un error (${e.code()}).")
    )
} catch (e: IOException) {
    Result.failure(ErrorApi.SinConexion())
} catch (e: Exception) {
    Result.failure(ErrorApi.Servidor("Ocurrió un error inesperado. Inténtalo de nuevo."))
}

// Usa el "message" del ApiErrorResponse del backend; si hay errores por campo, muestra el primero.
internal fun mensajeDelServidor(e: HttpException): String? = try {
    val cuerpo = e.response()?.errorBody()?.string()
    val error = gson.fromJson(cuerpo, ErrorApiDto::class.java)
    error?.campos?.values?.firstOrNull() ?: error?.message
} catch (ex: Exception) {
    null
}
