package cl.duoc.albalab

import cl.duoc.albalab.data.model.EstadoSolicitud
import cl.duoc.albalab.data.remote.ApiAlbaLab
import cl.duoc.albalab.data.remote.ErrorApi
import cl.duoc.albalab.data.remote.Sesion
import cl.duoc.albalab.repository.RegistroRepository
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RegistroRepositoryTest {
    private lateinit var dao: FakeRegistroDao
    private lateinit var api: ApiAlbaLab
    private lateinit var repo: RegistroRepository

    @Before
    fun preparar() {
        dao = FakeRegistroDao()
        api = mockk()
        repo = RegistroRepository(dao, api)
        Sesion.iniciar("token", "Gestor")
    }

    @After
    fun limpiar() = Sesion.cerrar()

    @Test
    fun crearGuardaEnLaCacheConElIdDelServidor() = runTest {
        coEvery { api.crearSolicitud(any()) } returns Fixtures.dto(Fixtures.registro(id = 42))

        val r = repo.crear(Fixtures.registro(id = 0))

        assertEquals(42, r.getOrThrow().id)
        assertEquals(42, dao.lista.value.single().id)
    }

    @Test
    fun crearSinConexionDevuelveSinConexionYNoGuarda() = runTest {
        coEvery { api.crearSolicitud(any()) } throws IOException()

        val r = repo.crear(Fixtures.registro())

        assertTrue(r.exceptionOrNull() is ErrorApi.SinConexion)
        assertTrue(dao.lista.value.isEmpty())
    }

    @Test
    fun errorDeValidacionDelServidorMuestraElMensajeDelCampo() = runTest {
        coEvery { api.crearSolicitud(any()) } throws Fixtures.httpError(
            400, """{"message":"Hay datos inválidos","campos":{"telefono":"El teléfono debe tener 9 dígitos"}}"""
        )

        val r = repo.crear(Fixtures.registro())

        assertTrue(r.exceptionOrNull() is ErrorApi.Servidor)
        assertEquals("El teléfono debe tener 9 dígitos", r.exceptionOrNull()?.message)
    }

    @Test
    fun errorDelServidorSinCuerpoUsaMensajeGenerico() = runTest {
        coEvery { api.crearSolicitud(any()) } throws Fixtures.httpError(500, "no es json")

        val r = repo.crear(Fixtures.registro())

        assertEquals("El servidor respondió con un error (500).", r.exceptionOrNull()?.message)
    }

    @Test
    fun errorInesperadoNoEscapaComoExcepcion() = runTest {
        coEvery { api.crearSolicitud(any()) } throws IllegalStateException("boom")

        val r = repo.crear(Fixtures.registro())

        assertTrue(r.exceptionOrNull() is ErrorApi.Servidor)
    }

    @Test
    fun refrescarReemplazaLaCacheYConservaLaFotoLocal() = runTest {
        dao.lista.value = listOf(
            Fixtures.registro(id = 1, fotoPath = "content://foto1"),
            Fixtures.registro(id = 99, nombre = "Eliminada en otro teléfono")
        )
        coEvery { api.listarSolicitudes() } returns listOf(
            Fixtures.dto(Fixtures.registro(id = 1, estado = EstadoSolicitud.EN_REVISION)),
            Fixtures.dto(Fixtures.registro(id = 2, nombre = "Bruno"))
        )

        repo.refrescar().getOrThrow()

        val cache = dao.lista.value.sortedBy { it.id }
        assertEquals(listOf(1, 2), cache.map { it.id })
        assertEquals("content://foto1", cache[0].fotoPath)
        assertEquals(EstadoSolicitud.EN_REVISION, cache[0].estado)
    }

    @Test
    fun refrescarConTokenVencidoCierraLaSesion() = runTest {
        coEvery { api.listarSolicitudes() } throws Fixtures.httpError(401)

        val r = repo.refrescar()

        assertTrue(r.exceptionOrNull() is ErrorApi.NoAutorizado)
        assertFalse(Sesion.activa)
    }

    @Test
    fun cambiarEstadoActualizaLaCache() = runTest {
        dao.lista.value = listOf(Fixtures.registro(id = 1))
        coEvery { api.cambiarEstado(1, any()) } returns
            Fixtures.dto(Fixtures.registro(id = 1, estado = EstadoSolicitud.CONCRETADO))

        repo.cambiarEstado(1, EstadoSolicitud.CONCRETADO, "ok").getOrThrow()

        assertEquals(EstadoSolicitud.CONCRETADO, repo.obtenerPorId(1)?.estado)
    }

    @Test
    fun eliminarConErrorDelServidorNoBorraLaCache() = runTest {
        dao.lista.value = listOf(Fixtures.registro(id = 1))
        coEvery { api.eliminarSolicitud(1) } returns Response.error(404, """{"message":"La solicitud 1 no existe"}""".toResponseBody())

        val r = repo.eliminar(1)

        assertEquals("La solicitud 1 no existe", r.exceptionOrNull()?.message)
        assertEquals(1, dao.lista.value.size)
    }
}
