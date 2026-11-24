package cl.duoc.albalab

import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.data.remote.ApiAlbaLab
import cl.duoc.albalab.data.remote.ApiFeriados
import cl.duoc.albalab.data.remote.dto.FeriadoDto
import cl.duoc.albalab.data.remote.dto.SolicitudRequestDto
import cl.duoc.albalab.repository.FeriadosRepository
import cl.duoc.albalab.repository.RegistroRepository
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.RegistroViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
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
class RegistroViewModelTest {
    private lateinit var dao: FakeRegistroDao
    private lateinit var api: ApiAlbaLab
    private lateinit var apiFeriados: ApiFeriados
    private lateinit var notificador: FakeNotificador
    private lateinit var vm: RegistroViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = FakeRegistroDao()
        api = mockk()
        apiFeriados = mockk()
        notificador = FakeNotificador()
        coEvery { apiFeriados.feriados(any()) } returns emptyList()
        vm = RegistroViewModel(RegistroRepository(dao, api), notificador, FeriadosRepository(apiFeriados))
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun llenarFormularioValido() {
        vm.onCambio(Campo.NOMBRE, "Ana Pérez")
        vm.onCambio(Campo.TELEFONO, "912345678")
        vm.onCambio(Campo.CORREO, "ana@mail.cl")
        vm.onCambio(Campo.DIRECCION, "Calle 123")
        vm.onCambio(Campo.PERSONAS, "5")
        vm.onCambio(Campo.FECHA, "15/12/${Fixtures.anioSiguiente}")
        vm.onContacto(ContactoPreferido.CORREO)
        vm.onTipoAyuda(TipoAyuda.DONACION)
        vm.onAceptaContacto(true)
    }

    @Test
    fun guardarFormularioVacioPublicaErroresYNoLlamaAlServidor() {
        vm.guardar()

        assertEquals(Campo.entries.toSet(), vm.formulario.value.errores.keys)
        assertEquals(1, vm.formulario.value.intentosFallidos)
        assertTrue(notificador.avisos.isEmpty())
        coVerify(exactly = 0) { api.crearSolicitud(any()) }
    }

    @Test
    fun guardarFormularioValidoEnviaAlServidorNotificaYLimpia() {
        val enviado = slot<SolicitudRequestDto>()
        coEvery { api.crearSolicitud(capture(enviado)) } returns Fixtures.dto(Fixtures.registro(id = 10))
        llenarFormularioValido()

        vm.guardar()

        val s = vm.formulario.value
        assertEquals("Ana Pérez", enviado.captured.nombre)
        assertEquals("DONACION", enviado.captured.tipoAyuda)
        assertEquals(listOf("Ana Pérez"), notificador.avisos)
        assertEquals(10, dao.lista.value.single().id)
        assertTrue(s.guardadoOk)
        assertFalse(s.guardando)
        assertEquals("", s.nombre)
    }

    @Test
    fun guardarSinConexionNoNotificaYConservaLosDatos() {
        coEvery { api.crearSolicitud(any()) } throws IOException("sin red")
        llenarFormularioValido()

        vm.guardar()

        val s = vm.formulario.value
        assertNotNull(s.errorEnvio)
        assertFalse(s.guardando)
        assertFalse(s.guardadoOk)
        assertEquals("Ana Pérez", s.nombre)
        assertTrue(notificador.avisos.isEmpty())
        assertTrue(dao.lista.value.isEmpty())
    }

    @Test
    fun cambiarTelefonoInvalidoMarcaSoloTelefono() {
        vm.onCambio(Campo.TELEFONO, "12")

        assertEquals(setOf(Campo.TELEFONO), vm.formulario.value.errores.keys)
    }

    @Test
    fun fechaQueEsFeriadoMuestraAvisoSinMarcarError() {
        val anio = Fixtures.anioSiguiente
        coEvery { apiFeriados.feriados(anio) } returns listOf(FeriadoDto("$anio-12-25", "Navidad"))

        vm.onCambio(Campo.FECHA, "25/12/$anio")

        val s = vm.formulario.value
        assertTrue(s.avisoFecha!!.contains("Navidad"))
        assertFalse(Campo.FECHA in s.errores)
    }

    @Test
    fun fechaNormalNoMuestraAvisoYCambiarLaFechaLoBorra() {
        val anio = Fixtures.anioSiguiente
        coEvery { apiFeriados.feriados(anio) } returns listOf(FeriadoDto("$anio-12-25", "Navidad"))
        vm.onCambio(Campo.FECHA, "25/12/$anio")

        vm.onCambio(Campo.FECHA, "26/12/$anio")

        assertNull(vm.formulario.value.avisoFecha)
    }

    @Test
    fun fallaDeLaApiDeFeriadosNoAfectaElFormulario() {
        coEvery { apiFeriados.feriados(any()) } throws IOException("sin red")

        vm.onCambio(Campo.FECHA, "25/12/${Fixtures.anioSiguiente}")

        assertNull(vm.formulario.value.avisoFecha)
        assertTrue(vm.formulario.value.errores.isEmpty())
    }

    @Test
    fun opcionesDelFormularioActualizanElEstado() {
        vm.onRecibeNovedades(true)
        vm.onFoto("content://foto")
        vm.onAceptaContacto(false)

        val s = vm.formulario.value
        assertTrue(s.recibeNovedades)
        assertEquals("content://foto", s.fotoPath)
        assertTrue(Campo.ACEPTA in s.errores)
    }
}
