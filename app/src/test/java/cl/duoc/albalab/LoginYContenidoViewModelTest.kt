package cl.duoc.albalab

import cl.duoc.albalab.data.local.ContenidoFicticio
import cl.duoc.albalab.data.remote.ApiAlbaLab
import cl.duoc.albalab.data.remote.ApiFeriados
import cl.duoc.albalab.data.remote.ApiMindicador
import cl.duoc.albalab.data.remote.Sesion
import cl.duoc.albalab.data.remote.dto.FeriadoDto
import cl.duoc.albalab.data.remote.dto.LoginDto
import cl.duoc.albalab.data.remote.dto.MindicadorDto
import cl.duoc.albalab.data.remote.dto.TokenDto
import cl.duoc.albalab.data.remote.dto.ValorIndicadorDto
import cl.duoc.albalab.repository.AuthRepository
import cl.duoc.albalab.repository.ContenidoRepository
import cl.duoc.albalab.repository.FeriadosRepository
import cl.duoc.albalab.repository.IndicadoresRepository
import cl.duoc.albalab.viewmodel.ContenidoViewModel
import cl.duoc.albalab.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginYContenidoViewModelTest {
    private val api: ApiAlbaLab = mockk()

    @Before
    fun preparar() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    // ---------- Login ----------
    @Test
    fun loginConCamposVaciosMarcaErroresYNoLlamaAlServidor() {
        val vm = LoginViewModel(AuthRepository(api))

        vm.ingresar()

        assertNotNull(vm.estado.value.errorEmail)
        assertNotNull(vm.estado.value.errorPassword)
        coVerify(exactly = 0) { api.login(any()) }
    }

    @Test
    fun loginCorrectoGuardaElTokenEnLaSesion() {
        coEvery { api.login(LoginDto("gestor@albalab.cl", "Alba2026!")) } returns
            TokenDto("jwt-123", "gestor@albalab.cl", "Equipo de Vinculación", 3600)
        val vm = LoginViewModel(AuthRepository(api))

        vm.onEmail(" gestor@albalab.cl ")
        vm.onPassword("Alba2026!")
        vm.ingresar()

        assertTrue(vm.estado.value.ingresoOk)
        assertEquals("", vm.estado.value.password)
        assertEquals("jwt-123", Sesion.token)
        assertEquals("Equipo de Vinculación", Sesion.nombre)
    }

    @Test
    fun loginConClaveIncorrectaMuestraElMensajeDelServidor() {
        coEvery { api.login(any()) } throws
            Fixtures.httpError(401, """{"message":"Correo o contraseña incorrectos"}""")
        val vm = LoginViewModel(AuthRepository(api))

        vm.onEmail("gestor@albalab.cl")
        vm.onPassword("mala")
        vm.ingresar()

        assertEquals("Correo o contraseña incorrectos", vm.estado.value.error)
        assertFalse(vm.estado.value.cargando)
        assertFalse(Sesion.activa)
    }

    @Test
    fun cerrarSesionBorraElToken() {
        Sesion.iniciar("t", "Gestor")

        AuthRepository(api).cerrarSesion()

        assertNull(Sesion.token)
    }

    // ---------- Contenido + APIs externas ----------
    private val apiMindicador: ApiMindicador = mockk()
    private val apiFeriados: ApiFeriados = mockk()

    private fun contenidoVm() = ContenidoViewModel(
        ContenidoRepository(api), IndicadoresRepository(apiMindicador), FeriadosRepository(apiFeriados)
    )

    @Test
    fun sinBackendNiApisMuestraContenidoLocalYAvisos() {
        coEvery { api.programas() } throws IOException()
        coEvery { apiMindicador.indicadores() } throws IOException()
        coEvery { apiFeriados.feriados(any()) } throws IOException()

        val s = contenidoVm().uiState.value

        assertFalse(s.desdeServidor)
        assertEquals(ContenidoFicticio.programas, s.programas)
        assertEquals(ContenidoFicticio.propositoInstitucional, s.propositoInstitucional)
        assertTrue(s.errorEconomia)
        assertTrue(s.errorFeriado)
    }

    @Test
    fun conApisExternasMuestraUfDolarYProximoFeriado() {
        coEvery { api.programas() } throws IOException()
        coEvery { apiMindicador.indicadores() } returns
            MindicadorDto(ValorIndicadorDto(40000.0, null), ValorIndicadorDto(1000.0, null))
        coEvery { apiFeriados.feriados(any()) } answers {
            val anio = firstArg<Int>()
            listOf(FeriadoDto("$anio-12-31", "Feriado de prueba"))
        }

        val s = contenidoVm().uiState.value

        assertEquals(40000.0, s.economia!!.uf, 0.001)
        assertEquals("Feriado de prueba", s.proximoFeriado?.nombre)
        assertFalse(s.errorEconomia)
        assertFalse(s.errorFeriado)
    }
}
