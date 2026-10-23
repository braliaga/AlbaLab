package cl.duoc.albalab.data

import cl.duoc.albalab.model.*

object ContenidoFicticio {

    val propositoInstitucional =
        "CORPALIV y su Red Sociolaboral Alba Lab promueven la autonomía, educación e inclusión social y laboral de jóvenes y adultos con discapacidad múltiple."

    val indicadores = listOf(
        IndicadorImpacto("+85", "Estudiantes en Escuela Jan Van Dijk"),
        IndicadorImpacto("+40", "Jóvenes activos en talleres"),
        IndicadorImpacto("24", "Alianzas empresariales RSE")
    )

    val programas = listOf(
        ProgramaInfo(
            id = 1,
            titulo = "Escuela Especial Jan Van Dijk",
            resumen = "Educación especializada para niños con retos múltiples.",
            detalleExpandido = "Desarrolla habilidades comunicativas y de autonomía funcional con acompañamiento directo a las familias."
        ),
        ProgramaInfo(
            id = 2,
            titulo = "Talleres Sociolaborales Alba Lab",
            resumen = "Preparación para el trabajo y elaboración de productos con sentido.",
            detalleExpandido = "Espacio donde jóvenes crean papelería sustentable y regalos corporativos, fortaleciendo su inclusión."
        ),
        ProgramaInfo(
            id = 3,
            titulo = "Vinculación Empresarial y RSE",
            resumen = "Alianzas estratégicas, voluntariado corporativo y capacitación.",
            detalleExpandido = "Asesoramos a organizaciones en cultura inclusiva y jornadas de voluntariado junto a nuestra comunidad."
        )
    )

    val casosExito = listOf(
        CasoExito(
            id = 1,
            aliasParticipante = "Participante A (22 años)",
            historia = "Ingresó a los talleres de encuadernación adaptada tras egresar de la escuela.",
            logro = "Hoy lidera el control de calidad de sets para empresas aliadas."
        ),
        CasoExito(
            id = 2,
            aliasParticipante = "Participante B (25 años)",
            historia = "Se integró al programa de transición a la vida adulta.",
            logro = "Participa activamente presentando el catálogo Alba Lab."
        ),
        CasoExito(
            id = 3,
            aliasParticipante = "Empresa Colaboradora C",
            historia = "Reemplazó sus regalos anuales por kits elaborados en Alba Lab.",
            logro = "Financió insumos anuales para 15 participantes del taller."
        )
    )

    val catalogoProductos = listOf(
        ProductoCatalogo(1, "Kit Corporativo Bienvenida", "Libreta ecológica cosida a mano, lápiz plantable y bolsa.", 18500, "https://picsum.photos/id/20/300/300"),
        ProductoCatalogo(2, "Set Papelería Alba Lab", "Agenda perpetua en papel reciclado y separadores.", 12900, "https://picsum.photos/id/24/300/300"),
        ProductoCatalogo(3, "Caja Regalo Inclusión RSE", "Selección de productos artesanales con tarjeta de impacto.", 24900, "https://picsum.photos/id/30/300/300")
    )

    // Imágenes de ejemplo de un servicio público de fotos de stock: no corresponden a personas ni a la organización real.
    const val bannerInicioUrl = "https://picsum.photos/id/28/1200/600"

    val destinoAportes = listOf(
        DestinoAporte("Materiales y equipamiento adaptado", "45%", "Insumos para talleres productivos y ayudas técnicas sensoriales."),
        DestinoAporte("Becas y apoyo terapéutico", "35%", "Financiamiento directo para estudiantes y jóvenes con discapacidad múltiple."),
        DestinoAporte("Inserción y seguimiento laboral", "20%", "Acompañamiento por preparadores laborales en espacios de trabajo.")
    )
}