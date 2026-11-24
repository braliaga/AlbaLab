package cl.duoc.albalab

import cl.duoc.albalab.data.local.ContenidoFicticio
import cl.duoc.albalab.data.remote.ApiAlbaLab
import cl.duoc.albalab.data.remote.dto.CasoExitoDto
import cl.duoc.albalab.data.remote.dto.DestinoAporteDto
import cl.duoc.albalab.data.remote.dto.IndicadorDto
import cl.duoc.albalab.data.remote.dto.ProductoDto
import cl.duoc.albalab.data.remote.dto.ProgramaDto
import cl.duoc.albalab.repository.ContenidoRepository
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContenidoRepositoryTest {
    private val api: ApiAlbaLab = mockk()
    private val repo = ContenidoRepository(api)

    private fun servidorResponde() {
        coEvery { api.programas() } returns listOf(ProgramaDto(1, "Escuela", "Resumen", "Detalle"))
        coEvery { api.casos() } returns listOf(CasoExitoDto(1, "Participante A", "Historia", "Logro"))
        coEvery { api.productos() } returns listOf(ProductoDto(1, "Kit", "Desc", 18500, null))
        coEvery { api.indicadores() } returns listOf(IndicadorDto(1, "+85", "Estudiantes"))
        coEvery { api.destinos() } returns listOf(DestinoAporteDto(1, "Materiales", "45%", "Insumos"))
    }

    @Test
    fun conBackendDevuelveElContenidoDelServidor() = runTest {
        servidorResponde()

        val c = repo.obtener()

        assertTrue(c.desdeServidor)
        assertEquals("Escuela", c.programas.single().titulo)
        assertEquals("Detalle", c.programas.single().detalleExpandido)
        assertEquals("Participante A", c.casosExito.single().aliasParticipante)
        assertEquals("", c.catalogo.single().imagenUrl)
        assertEquals("+85", c.indicadores.single().cifra)
        assertEquals("45%", c.destinoAportes.single().porcentaje)
    }

    @Test
    fun sinBackendUsaLaCopiaLocal() = runTest {
        servidorResponde()
        coEvery { api.productos() } throws IOException()

        val c = repo.obtener()

        assertFalse(c.desdeServidor)
        assertEquals(ContenidoFicticio.programas, c.programas)
        assertEquals(ContenidoFicticio.catalogoProductos, c.catalogo)
    }

    @Test
    fun identidadInstitucionalEsLocal() {
        assertEquals(ContenidoFicticio.bannerInicioUrl, repo.obtenerBannerInicioUrl())
        assertEquals(ContenidoFicticio.propositoInstitucional, repo.obtenerPropositoInstitucional())
    }
}
