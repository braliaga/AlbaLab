package cl.duoc.albalab.data.model

// Mismos nombres que el enum de ms-vinculacion: viajan tal cual en el JSON.
enum class EstadoSolicitud(val etiqueta: String) {
    RECIBIDA("Recibida"),
    EN_REVISION("En revisión"),
    CONTACTADO("Contactado"),
    CONCRETADO("Concretado"),
    DESCARTADO("Descartado");

    // Una solicitud concretada o descartada ya no cambia de estado (misma regla que el backend).
    val esFinal: Boolean get() = this == CONCRETADO || this == DESCARTADO
}
