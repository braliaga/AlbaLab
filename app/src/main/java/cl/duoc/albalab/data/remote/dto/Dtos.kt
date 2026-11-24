package cl.duoc.albalab.data.remote.dto

import cl.duoc.albalab.data.model.CasoExito
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.DestinoAporte
import cl.duoc.albalab.data.model.EstadoSolicitud
import cl.duoc.albalab.data.model.IndicadorImpacto
import cl.duoc.albalab.data.model.ProductoCatalogo
import cl.duoc.albalab.data.model.ProgramaInfo
import cl.duoc.albalab.data.model.RegistroInteres
import cl.duoc.albalab.data.model.TipoAyuda

// ---------- ms-vinculacion ----------
data class SolicitudDto(
    val id: Int,
    val nombre: String,
    val telefono: String?,
    val correo: String?,
    val direccion: String,
    val personas: Int,
    val fechaContacto: String,
    val contactoPreferido: String,
    val tipoAyuda: String,
    val aceptaContacto: Boolean,
    val recibeNovedades: Boolean,
    val fotoPath: String?,
    val estado: String
) {
    fun aModelo() = RegistroInteres(
        id = id,
        nombre = nombre,
        telefono = telefono.orEmpty(),
        correo = correo.orEmpty(),
        direccion = direccion,
        personas = personas,
        fechaContacto = fechaContacto,
        contactoPreferido = ContactoPreferido.valueOf(contactoPreferido),
        tipoAyuda = TipoAyuda.valueOf(tipoAyuda),
        aceptaContacto = aceptaContacto,
        recibeNovedades = recibeNovedades,
        fotoPath = fotoPath,
        estado = EstadoSolicitud.valueOf(estado)
    )
}

data class SolicitudRequestDto(
    val nombre: String,
    val telefono: String,
    val correo: String,
    val direccion: String,
    val personas: Int,
    val fechaContacto: String,
    val contactoPreferido: String,
    val tipoAyuda: String,
    val aceptaContacto: Boolean,
    val recibeNovedades: Boolean,
    val fotoPath: String?
) {
    companion object {
        fun desde(r: RegistroInteres) = SolicitudRequestDto(
            nombre = r.nombre,
            telefono = r.telefono,
            correo = r.correo,
            direccion = r.direccion,
            personas = r.personas,
            fechaContacto = r.fechaContacto,
            contactoPreferido = r.contactoPreferido.name,
            tipoAyuda = r.tipoAyuda.name,
            aceptaContacto = r.aceptaContacto,
            recibeNovedades = r.recibeNovedades,
            fotoPath = r.fotoPath
        )
    }
}

data class SolicitudUpdateDto(val telefono: String, val contactoPreferido: String, val recibeNovedades: Boolean)

data class CambioEstadoDto(val estado: String, val observacion: String?)

// ---------- ms-auth ----------
data class LoginDto(val email: String, val password: String)

data class TokenDto(val token: String, val email: String, val nombre: String, val expiraEnSegundos: Long)

// ---------- ms-contenidos ----------
data class ProgramaDto(val id: Int, val titulo: String, val resumen: String, val detalle: String) {
    fun aModelo() = ProgramaInfo(id, titulo, resumen, detalle)
}

data class CasoExitoDto(val id: Int, val alias: String, val historia: String, val logro: String) {
    fun aModelo() = CasoExito(id, alias, historia, logro)
}

data class ProductoDto(val id: Int, val nombre: String, val descripcion: String, val precioClp: Int, val imagenUrl: String?) {
    fun aModelo() = ProductoCatalogo(id, nombre, descripcion, precioClp, imagenUrl.orEmpty())
}

data class IndicadorDto(val id: Int, val cifra: String, val etiqueta: String) {
    fun aModelo() = IndicadorImpacto(cifra, etiqueta)
}

data class DestinoAporteDto(val id: Int, val area: String, val porcentaje: String, val explicacion: String) {
    fun aModelo() = DestinoAporte(area, porcentaje, explicacion)
}

// ---------- APIs externas ----------
// mindicador.cl: {"uf":{"valor":41391.69,"fecha":"..."},"dolar":{"valor":1017.17,...},...}
data class ValorIndicadorDto(val valor: Double, val fecha: String?)

data class MindicadorDto(val uf: ValorIndicadorDto, val dolar: ValorIndicadorDto)

// Nager.Date: [{"date":"2026-09-18","localName":"Fiestas Patrias",...}]
data class FeriadoDto(val date: String, val localName: String)

// Cuerpo de error común de los microservicios (ApiErrorResponse).
data class ErrorApiDto(val message: String?, val campos: Map<String, String>?)
