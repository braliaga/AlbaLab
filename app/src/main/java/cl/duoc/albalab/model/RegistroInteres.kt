package cl.duoc.albalab.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val fotoPath: String? = null
)
