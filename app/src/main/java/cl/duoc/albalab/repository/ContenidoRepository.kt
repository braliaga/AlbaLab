package cl.duoc.albalab.repository

import cl.duoc.albalab.data.local.ContenidoFicticio
import cl.duoc.albalab.data.model.CasoExito
import cl.duoc.albalab.data.model.IndicadorImpacto
import cl.duoc.albalab.data.model.ProductoCatalogo
import cl.duoc.albalab.data.model.ProgramaInfo

class ContenidoRepository {

    // Punto único de acceso al contenido (listo para conectar Retrofit -> ms-contenidos)
    fun obtenerProgramas(): List<ProgramaInfo> = ContenidoFicticio.programas

    fun obtenerCasosExito(): List<CasoExito> = ContenidoFicticio.casosExito

    fun obtenerCatalogo(): List<ProductoCatalogo> = ContenidoFicticio.catalogoProductos

    fun obtenerIndicadores(): List<IndicadorImpacto> = ContenidoFicticio.indicadores

    fun obtenerBannerInicioUrl(): String = ContenidoFicticio.bannerInicioUrl

    fun obtenerPropositoInstitucional(): String = ContenidoFicticio.propositoInstitucional

    fun obtenerDestinoAportes() = ContenidoFicticio.destinoAportes

}