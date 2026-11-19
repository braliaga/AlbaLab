package cl.duoc.albalab

import androidx.lifecycle.SavedStateHandle
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.RegistroInteres
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.repository.RegistroRepository
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.EdicionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EdicionViewModelTest {
    private lateinit var dao: FakeRegistroDao
    private lateinit var vm: EdicionViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = FakeRegistroDao()
        runBlocking {
            dao.insertar(
                RegistroInteres(
                    nombre = "Ana Pérez",
                    telefono = "912345678",
                    correo = "ana@mail.cl",
                    direccion = "Calle 123",
                    personas = 5,
                    fechaContacto = "01/01/2027",
                    contactoPreferido = ContactoPreferido.CORREO,
                    tipoAyuda = TipoAyuda.DONACION,
                    aceptaContacto = true,
                    recibeNovedades = true
                )
            )
        }
        vm = EdicionViewModel(RegistroRepository(dao), SavedStateHandle(mapOf("id" to 1)))
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun alCrearseCargaLosDatosDelRegistro() {
        val e = vm.estado.value

        assertEquals(1, e.id)
        assertEquals("912345678", e.telefono)
        assertEquals(ContactoPreferido.CORREO, e.contacto)
        assertTrue(e.recibeNovedades)
        assertFalse(e.guardadoOk)
    }

    @Test
    fun guardarConTelefonoInvalidoMarcaErrorYNoActualiza() {
        vm.onTelefono("12")

        vm.guardar()

        assertEquals(setOf(Campo.TELEFONO), vm.estado.value.errores.keys)
        assertFalse(vm.estado.value.guardadoOk)
        assertEquals("912345678", dao.lista.value.first().telefono)
    }

    @Test
    fun guardarActualizaElRegistroYMarcaGuardadoOk() {
        vm.onTelefono("987654321")
        vm.onContacto(ContactoPreferido.TELEFONO)
        vm.onNovedades(false)

        vm.guardar()

        val guardado = dao.lista.value.first()
        assertEquals("987654321", guardado.telefono)
        assertEquals(ContactoPreferido.TELEFONO, guardado.contactoPreferido)
        assertFalse(guardado.recibeNovedades)
        assertTrue(vm.estado.value.guardadoOk)
    }
}
