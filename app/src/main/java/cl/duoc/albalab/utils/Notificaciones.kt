package cl.duoc.albalab.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.toArgb
import cl.duoc.albalab.R
import cl.duoc.albalab.ui.theme.RojoAlba
import cl.duoc.albalab.viewmodel.Notificador

class Notificaciones(context: Context) : Notificador {
    private val context = context.applicationContext

    override fun registroGuardado(nombre: String) {
        // En Android 13+ sin permiso no se muestra nada; el registro ya quedó guardado igual.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        crearCanal()
        val aviso = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setColor(RojoAlba.toArgb())
            .setContentTitle("Registro enviado")
            .setContentText("Gracias, $nombre. El equipo de vinculación te contactará pronto.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), aviso)
    }

    private fun crearCanal() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(CANAL, "Registros de interés", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Confirmación al enviar un registro"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    private companion object {
        const val CANAL = "registros"
    }
}
