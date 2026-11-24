package cl.duoc.albalab

import cl.duoc.albalab.data.model.ContactoPreferido
import cl.duoc.albalab.data.model.RegistroInteres
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.repository.RegistroRepository
import cl.duoc.albalab.viewmodel.RegistrosViewModel
import cl.duoc.albalab.viewmodel.filtrar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrosViewModelTest {
    private val ana = registro(1, "Ana Pérez", TipoAyuda.DONACION)
    private val bruno = registro(2, "Bruno Díaz", TipoAyuda.VOLUNTARIADO)
    private val mariana = registro(3, "Mariana Rojas", TipoAyuda.VOLUNTARIADO)
    private val todos = listOf(ana, bruno, mariana)

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun filtrarPorTextoIgnoraMayusculasYEspacios() {
        assertEquals(listOf(ana, mariana), filtrar(todos, "  ANA ", null))
    }

    @Test
    fun filtrarPorTipo() {
        assertEquals(listOf(bruno, mariana), filtrar(todos, "", TipoAyuda.VOLUNTARIADO))
    }

    @Test
    fun filtrarCombinaTextoYTipo() {
        assertEquals(listOf(mariana), filtrar(todos, "ana", TipoAyuda.VOLUNTARIADO))
    }

    @Test
    fun filtrarSinCoincidenciasDevuelveVacio() {
        assertTrue(filtrar(todos, "zzz", null).isEmpty())
    }

    @Test
    fun estadoReaccionaABusquedaYTipoYLimpiar() = runTest {
        val dao = FakeRegistroDao().apply { lista.value = todos }
        val vm = RegistrosViewModel(RegistroRepository(dao))
        // WhileSubscribed solo emite con alguien escuchando.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.estado.collect {} }

        assertEquals(3, vm.estado.value.filtrados.size)
        assertTrue(vm.estado.value.cargado)

        vm.onBusqueda("bru")
        assertEquals(listOf(bruno), vm.estado.value.filtrados)
        assertEquals(3, vm.estado.value.total)

        vm.onBusqueda("")
        vm.onTipo(TipoAyuda.DONACION)
        assertEquals(listOf(ana), vm.estado.value.filtrados)

        vm.limpiarFiltros()
        assertEquals(3, vm.estado.value.filtrados.size)
    }

    private fun registro(id: Int, nombre: String, tipo: TipoAyuda) = RegistroInteres(
        id = id,
        nombre = nombre,
        telefono = "912345678",
        correo = "a@mail.cl",
        direccion = "Calle 123",
        personas = 1,
        fechaContacto = "01/01/2030",
        contactoPreferido = ContactoPreferido.CORREO,
        tipoAyuda = tipo,
        aceptaContacto = true,
        recibeNovedades = false
    )
}
