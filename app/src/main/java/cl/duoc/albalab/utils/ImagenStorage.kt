package cl.duoc.albalab.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagenStorage(private val context: Context) {
    // filesDir y no cacheDir: el sistema puede vaciar la caché y la foto quedaría guardada en el registro sin existir.
    private fun crearArchivo(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val directorio = File(context.filesDir, "imagenes").apply { mkdirs() }
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", directorio)
    }

    private fun aUri(archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.provider", archivo)

    fun crearArchivoImagen(): Uri = aUri(crearArchivo())

    // El Uri de la galería es un permiso temporal: se copia para que la foto siga abriendo tras reiniciar la app.
    fun copiarDesdeGaleria(origen: Uri): Uri? {
        val archivo = crearArchivo()
        val copiado = context.contentResolver.openInputStream(origen)?.use { entrada ->
            archivo.outputStream().use { salida -> entrada.copyTo(salida) }
            true
        } ?: false
        if (!copiado) {
            archivo.delete()
            return null
        }
        return aUri(archivo)
    }
}
