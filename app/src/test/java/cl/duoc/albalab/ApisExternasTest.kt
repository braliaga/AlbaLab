package cl.duoc.albalab

import cl.duoc.albalab.data.remote.ApiFeriados
import cl.duoc.albalab.data.remote.ApiMindicador
import cl.duoc.albalab.data.remote.ErrorApi
import cl.duoc.albalab.data.remote.dto.FeriadoDto
import cl.duoc.albalab.data.remote.dto.MindicadorDto
import cl.duoc.albalab.data.remote.dto.ValorIndicadorDto
import cl.duoc.albalab.repository.Feriado
import cl.duoc.albalab.repository.FeriadosRepository
import cl.duoc.albalab.repository.IndicadoresRepository
import cl.duoc.albalab.repository.aIso
import cl.duoc.albalab.repository.hoyIso
import cl.duoc.albalab.repository.proximoDesde
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApisExternasTest {
    private val apiFeriados: ApiFeriados = mockk()
    private val feriados = FeriadosRepository(apiFeriados)

    // ---------- mindicador.cl ----------
    @Test
    fun indicadoresTraeUfYDolar() = runTest {
        val api: ApiMindicador = mockk()
        coEvery { api.indicadores() } returns MindicadorDto(ValorIndicadorDto(41391.69, null), ValorIndicadorDto(1017.17, null))

        val r = IndicadoresRepository(api).obtener().getOrThrow()

        assertEquals(41391.69, r.uf, 0.001)
        assertEquals(1017.17, r.dolar, 0.001)
    }

    @Test
    fun indicadoresSinConexionDevuelveError() = runTest {
        val api: ApiMindicador = mockk()
        coEvery { api.indicadores() } throws IOException()

        assertTrue(IndicadoresRepository(api).obtener().exceptionOrNull() is ErrorApi.SinConexion)
    }

    // ---------- Nager.Date ----------
    @Test
    fun aIsoConvierteFechasDelFormulario() {
        assertEquals("2026-12-08", aIso("8/12/2026"))
        assertEquals("2026-09-18", aIso(" 18/09/2026 "))
        assertNull(aIso("2026-12-08"))
        assertNull(aIso("hola"))
    }

    @Test
    fun hoyIsoTieneFormatoAnioMesDia() {
        assertTrue(Regex("""\d{4}-\d{2}-\d{2}""").matches(hoyIso()))
    }

    @Test
    fun feriadoEnEncuentraElFeriadoDeEsaFecha() = runTest {
        coEvery { apiFeriados.feriados(2026) } returns listOf(FeriadoDto("2026-09-18", "Fiestas Patrias"))

        assertEquals("Fiestas Patrias", feriados.feriadoEn("18/09/2026")?.nombre)
        assertNull(feriados.feriadoEn("17/09/2026"))
        assertNull(feriados.feriadoEn("fecha inválida"))
    }

    @Test
    fun losFeriadosDeUnAnioSePidenUnaSolaVez() = runTest {
        coEvery { apiFeriados.feriados(2026) } returns listOf(FeriadoDto("2026-12-25", "Navidad"))

        feriados.feriadoEn("25/12/2026")
        feriados.feriadoEn("24/12/2026")

        coVerify(exactly = 1) { apiFeriados.feriados(2026) }
    }

    @Test
    fun siLaApiFallaNoHayFeriadoYSeVuelveAIntentar() = runTest {
        coEvery { apiFeriados.feriados(2026) } throws IOException()

        assertNull(feriados.feriadoEn("25/12/2026"))
        assertNull(feriados.feriadoEn("25/12/2026"))
        coVerify(exactly = 2) { apiFeriados.feriados(2026) }
    }

    @Test
    fun proximoBuscaEnElAnioSiguienteSiYaPasaronTodos() = runTest {
        coEvery { apiFeriados.feriados(2026) } returns listOf(FeriadoDto("2026-12-25", "Navidad"))
        coEvery { apiFeriados.feriados(2027) } returns listOf(FeriadoDto("2027-01-01", "Año Nuevo"))

        assertEquals("Navidad", feriados.proximo("2026-12-20").getOrThrow()?.nombre)
        assertEquals("Año Nuevo", feriados.proximo("2026-12-26").getOrThrow()?.nombre)
    }

    @Test
    fun proximoSinConexionDevuelveError() = runTest {
        coEvery { apiFeriados.feriados(any()) } throws IOException()

        assertTrue(feriados.proximo("2026-11-23").isFailure)
    }

    @Test
    fun proximoDesdeIncluyeElMismoDiaYFormateaCorto() {
        val lista = listOf(Feriado("2026-12-25", "Navidad"), Feriado("2026-12-08", "Inmaculada Concepción"))

        val f = proximoDesde("2026-12-08", lista)

        assertEquals("Inmaculada Concepción", f?.nombre)
        assertEquals("08/12", f?.fechaCorta)
        assertNull(proximoDesde("2026-12-26", lista))
    }
}
