package cl.duoc.albalab.repository

import cl.duoc.albalab.data.local.ContenidoFicticio
import cl.duoc.albalab.data.model.CasoExito
import cl.duoc.albalab.data.model.DestinoAporte
import cl.duoc.albalab.data.model.IndicadorImpacto
import cl.duoc.albalab.data.model.ProductoCatalogo
import cl.duoc.albalab.data.model.ProgramaInfo
import cl.duoc.albalab.data.remote.ApiAlbaLab
import cl.duoc.albalab.data.remote.llamarApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class Contenidos(
    val programas: List<ProgramaInfo>,
    val casosExito: List<CasoExito>,
    val catalogo: List<ProductoCatalogo>,
    val indicadores: List<IndicadorImpacto>,
    val destinoAportes: List<DestinoAporte>,
    val desdeServidor: Boolean
)

// Punto único de acceso al contenido: lo pide a ms-contenidos y, si el backend no responde,
// usa la copia local (ContenidoFicticio) para que la app nunca quede vacía.
class ContenidoRepository(private val api: ApiAlbaLab) {

    suspend fun obtener(): Contenidos = llamarApi {
        coroutineScope {
            val programas = async { api.programas() }
            val casos = async { api.casos() }
            val productos = async { api.productos() }
            val indicadores = async { api.indicadores() }
            val destinos = async { api.destinos() }
            Contenidos(
                programas = programas.await().map { it.aModelo() },
                casosExito = casos.await().map { it.aModelo() },
                catalogo = productos.await().map { it.aModelo() },
                indicadores = indicadores.await().map { it.aModelo() },
                destinoAportes = destinos.await().map { it.aModelo() },
                desdeServidor = true
            )
        }
    }.getOrElse { local() }

    fun local() = Contenidos(
        programas = ContenidoFicticio.programas,
        casosExito = ContenidoFicticio.casosExito,
        catalogo = ContenidoFicticio.catalogoProductos,
        indicadores = ContenidoFicticio.indicadores,
        destinoAportes = ContenidoFicticio.destinoAportes,
        desdeServidor = false
    )

    // Identidad institucional fija de la app (no cambia por backend).
    fun obtenerBannerInicioUrl(): String = ContenidoFicticio.bannerInicioUrl

    fun obtenerPropositoInstitucional(): String = ContenidoFicticio.propositoInstitucional
}
