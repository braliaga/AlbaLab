package cl.duoc.albalab.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caché local de las solicitudes de ms-vinculacion: el id es el mismo del servidor.
@Entity(tableName = "registros")
data class RegistroInteres(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val direccion: String,
    val personas: Int,
    val fechaContacto: String, // dd/MM/aaaa
    val contactoPreferido: ContactoPreferido,
    val tipoAyuda: TipoAyuda,
    val aceptaContacto: Boolean,
    val recibeNovedades: Boolean,
    val fotoPath: String? = null,
    val estado: EstadoSolicitud = EstadoSolicitud.RECIBIDA
)
