package cl.duoc.albalab

import cl.duoc.albalab.data.RegistroDao
import cl.duoc.albalab.data.RegistroRepository
import cl.duoc.albalab.model.ContactoPreferido
import cl.duoc.albalab.model.RegistroInteres
import cl.duoc.albalab.model.TipoAyuda
import cl.duoc.albalab.viewmodel.Campo
import cl.duoc.albalab.viewmodel.RegistroViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeRegistroDao : RegistroDao {
    val lista = MutableStateFlow<List<RegistroInteres>>(emptyList())
    var inserciones = 0
        private set

    override suspend fun insertar(r: RegistroInteres): Long {
        inserciones++
        val nuevo = r.copy(id = inserciones)
        lista.value = listOf(nuevo) + lista.value
        return nuevo.id.toLong()
    }

    override suspend fun actualizar(r: RegistroInteres) {
        lista.value = lista.value.map { if (it.id == r.id) r else it }
    }

    override fun obtenerTodos(): Flow<List<RegistroInteres>> = lista

    override suspend fun obtenerPorId(id: Int): RegistroInteres? = lista.value.firstOrNull { it.id == id }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {
    private lateinit var dao: FakeRegistroDao
    private lateinit var vm: RegistroViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = FakeRegistroDao()
        vm = RegistroViewModel(RegistroRepository(dao))
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
    fun cambiarTelefonoInvalidoMarcaSoloTelefono() {
        vm.onCambio(Campo.TELEFONO, "12")

        assertEquals(setOf(Campo.TELEFONO), vm.formulario.value.errores.keys)
    }

    @Test
    fun editarTelefonoSeRefleja() {
        llenarFormularioValido()
        vm.guardar()

        vm.cargarEdicion(1)
        vm.onEdicionTelefono("987654321")
        vm.guardarEdicion()

        assertEquals("987654321", dao.lista.value.first().telefono)
        assertTrue(vm.edicion.value.guardadoOk)
    }

    @Test
    fun reabrirEdicionLimpiaGuardadoOk() {
        llenarFormularioValido()
        vm.guardar()
        vm.cargarEdicion(1)
        vm.guardarEdicion()
        assertTrue(vm.edicion.value.guardadoOk)

        vm.cargarEdicion(1)

        assertFalse(vm.edicion.value.guardadoOk)
    }
}
