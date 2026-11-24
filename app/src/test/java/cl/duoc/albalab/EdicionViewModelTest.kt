package cl.duoc.albalab

import androidx.lifecycle.SavedStateHandle
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.EstadoSolicitud
import cl.duoc.albalab.data.remote.ApiAlbaLab
import cl.duoc.albalab.data.remote.Sesion
import cl.duoc.albalab.data.remote.dto.CambioEstadoDto
import cl.duoc.albalab.data.remote.dto.SolicitudUpdateDto
import cl.duoc.albalab.repository.RegistroRepository
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.EdicionViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class EdicionViewModelTest {
    private lateinit var dao: FakeRegistroDao
    private lateinit var api: ApiAlbaLab
    private lateinit var vm: EdicionViewModel

    private fun crear(estado: EstadoSolicitud = EstadoSolicitud.RECIBIDA) {
        dao.lista.value = listOf(Fixtures.registro(id = 1, estado = estado, fotoPath = "content://foto"))
        vm = EdicionViewModel(RegistroRepository(dao, api), SavedStateHandle(mapOf("id" to 1)))
    }

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = FakeRegistroDao()
        api = mockk()
        Sesion.iniciar("token", "Gestor")
        crear()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    @Test
    fun alCrearseCargaLosDatosDelRegistro() {
        val e = vm.estado.value

        assertEquals(1, e.id)
        assertEquals("Ana Pérez", e.nombre)
        assertEquals("912345678", e.telefono)
        assertEquals(ContactoPreferido.CORREO, e.contacto)
        assertEquals(EstadoSolicitud.RECIBIDA, e.estadoActual)
        assertTrue(e.recibeNovedades)
    }

    @Test
    fun guardarConTelefonoInvalidoMarcaErrorYNoLlamaAlServidor() {
        vm.onTelefono("12")

        vm.guardar()

        assertEquals(setOf(Campo.TELEFONO), vm.estado.value.errores.keys)
        assertFalse(vm.estado.value.guardadoOk)
        coVerify(exactly = 0) { api.actualizarSolicitud(any(), any()) }
    }

    @Test
    fun guardarSinCambiarEstadoHacePutYNoPatch() {
        val actualizado = Fixtures.registro(id = 1).copy(telefono = "987654321", contactoPreferido = ContactoPreferido.TELEFONO)
        coEvery { api.actualizarSolicitud(1, SolicitudUpdateDto("987654321", "TELEFONO", false)) } returns
            Fixtures.dto(actualizado).copy(fotoPath = null)
        vm.onTelefono("987654321")
        vm.onContacto(ContactoPreferido.TELEFONO)
        vm.onNovedades(false)

        vm.guardar()

        val enCache = dao.lista.value.single()
        assertEquals("987654321", enCache.telefono)
        assertEquals("content://foto", enCache.fotoPath) // la foto local se conserva
        assertTrue(vm.estado.value.guardadoOk)
        coVerify(exactly = 0) { api.cambiarEstado(any(), any()) }
    }

    @Test
    fun guardarConOtroEstadoHacePatchConElEstadoElegido() {
        coEvery { api.actualizarSolicitud(1, any()) } returns Fixtures.dto(Fixtures.registro(id = 1))
        coEvery { api.cambiarEstado(1, any()) } returns
            Fixtures.dto(Fixtures.registro(id = 1, estado = EstadoSolicitud.CONTACTADO))
        vm.onEstado(EstadoSolicitud.CONTACTADO)

        vm.guardar()

        coVerify { api.cambiarEstado(1, CambioEstadoDto("CONTACTADO", "Cambio desde la app")) }
        assertEquals(EstadoSolicitud.CONTACTADO, dao.lista.value.single().estado)
        assertTrue(vm.estado.value.guardadoOk)
    }

    @Test
    fun unEstadoFinalNoSePuedeCambiar() {
        crear(EstadoSolicitud.CONCRETADO)

        vm.onEstado(EstadoSolicitud.EN_REVISION)

        assertEquals(EstadoSolicitud.CONCRETADO, vm.estado.value.estadoElegido)
    }

    @Test
    fun guardarSinConexionMuestraErrorYNoVuelve() {
        coEvery { api.actualizarSolicitud(any(), any()) } throws IOException("sin red")

        vm.guardar()

        assertNotNull(vm.estado.value.error)
        assertFalse(vm.estado.value.procesando)
        assertFalse(vm.estado.value.guardadoOk)
    }

    @Test
    fun eliminarBorraDelServidorYDeLaCache() {
        coEvery { api.eliminarSolicitud(1) } returns Response.success(Unit)

        vm.eliminar()

        assertTrue(vm.estado.value.eliminado)
        assertTrue(dao.lista.value.isEmpty())
    }

    @Test
    fun eliminarConTokenVencidoPideIngresarDeNuevo() {
        coEvery { api.eliminarSolicitud(1) } returns Response.error(401, "".toResponseBody())

        vm.eliminar()

        assertTrue(vm.estado.value.sesionExpirada)
        assertFalse(vm.estado.value.eliminado)
        assertEquals(1, dao.lista.value.size)
        assertFalse(Sesion.activa)
    }
}
