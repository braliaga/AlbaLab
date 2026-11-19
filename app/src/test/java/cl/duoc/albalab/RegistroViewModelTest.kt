package cl.duoc.albalab

import cl.duoc.albalab.repository.RegistroRepository
import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.RegistroViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class RegistroViewModelTest {
    private lateinit var dao: FakeRegistroDao
    private lateinit var notificador: FakeNotificador
    private lateinit var vm: RegistroViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = FakeRegistroDao()
        notificador = FakeNotificador()
        vm = RegistroViewModel(RegistroRepository(dao), notificador)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun llenarFormularioValido() {
        val manana = Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000)
        vm.onCambio(Campo.NOMBRE, "Ana Pérez")
        vm.onCambio(Campo.TELEFONO, "912345678")
        vm.onCambio(Campo.CORREO, "ana@mail.cl")
        vm.onCambio(Campo.DIRECCION, "Calle 123")
        vm.onCambio(Campo.PERSONAS, "5")
        vm.onCambio(Campo.FECHA, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(manana))
        vm.onContacto(ContactoPreferido.CORREO)
        vm.onTipoAyuda(TipoAyuda.DONACION)
        vm.onAceptaContacto(true)
    }

    @Test
    fun guardarFormularioVacioPublicaErroresYNoInserta() {
        vm.guardar()

        assertEquals(Campo.entries.toSet(), vm.formulario.value.errores.keys)
        assertEquals(0, dao.inserciones)
    }

    @Test
    fun guardarFormularioValidoInsertaYLimpiaCampos() {
        llenarFormularioValido()

        vm.guardar()

        val s = vm.formulario.value
        assertEquals(1, dao.inserciones)
        assertTrue(s.guardadoOk)
        assertFalse(s.guardando)
        assertEquals("", s.nombre)
        assertTrue(s.errores.isEmpty())
    }

    @Test
    fun guardarFormularioValidoNotificaConElNombre() {
        llenarFormularioValido()

        vm.guardar()

        assertEquals(listOf("Ana Pérez"), notificador.nombres)
    }

    @Test
    fun guardarFormularioVacioNoNotifica() {
        vm.guardar()

        assertTrue(notificador.nombres.isEmpty())
    }

    @Test
    fun cambiarTelefonoInvalidoMarcaSoloTelefono() {
        vm.onCambio(Campo.TELEFONO, "12")

        assertEquals(setOf(Campo.TELEFONO), vm.formulario.value.errores.keys)
    }
}
