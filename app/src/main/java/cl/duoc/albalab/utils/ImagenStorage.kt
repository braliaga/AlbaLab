package cl.duoc.albalab.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagenStorage(private val context: Context) {
    fun crearArchivoImagen(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nombreArchivo = "JPEG_${timeStamp}_"
        val directorioAlmacenamiento: File = context.cacheDir
        val archivoImagen = File.createTempFile(nombreArchivo, ".jpg", directorioAlmacenamiento)

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            archivoImagen
        )
    }
}