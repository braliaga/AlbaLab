package cl.duoc.albalab.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.duoc.albalab.MainActivity
import cl.duoc.albalab.R

private const val CANAL = "registros"
private const val ID = 1

interface Notificador {
    fun registroGuardado(nombre: String)
}

class NotificadorAndroid(context: Context) : Notificador {
    // applicationContext y no el de una Activity: el notificador vive tanto como el ViewModel.
    private val context: Context = context.applicationContext

    init {
        // NotificationChannelCompat funciona también en versiones menores a la 26, sin revisar la versión.
        NotificationManagerCompat.from(this.context).createNotificationChannel(
            NotificationChannelCompat.Builder(CANAL, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName("Registros")
                .build()
        )
    }

    override fun registroGuardado(nombre: String) {
        // Desde Android 13 hay que tener el permiso concedido; si no, el registro se guarda igual, sin aviso.
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        // Al tocar el aviso se trae la app al frente: si ya está abierta se reutiliza, sin crear otra pantalla encima.
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val abrirApp = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val aviso = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle("Registro recibido")
            .setContentText("Gracias, $nombre. El equipo de Alba Lab te contactará pronto.")
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID, aviso)
    }
}
