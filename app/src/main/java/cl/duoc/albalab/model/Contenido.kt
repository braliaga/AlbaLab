package cl.duoc.albalab.model

data class ProgramaInfo(
    val id: Int,
    val titulo: String,
    val resumen: String,
    val detalleExpandido: String
)

data class IndicadorImpacto(
    val cifra: String,
    val etiqueta: String
)

data class CasoExito(
    val id: Int,
    val aliasParticipante: String, // Anonimizado según rúbrica
    val historia: String,
    val logro: String
)

data class ProductoCatalogo(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precioClp: Int,
    val imagenUrl: String
)

data class DestinoAporte(
    val area: String,
    val porcentaje: String,
    val explicacion: String
)