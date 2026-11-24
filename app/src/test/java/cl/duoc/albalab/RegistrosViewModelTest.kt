package cl.duoc.albalab

import cl.duoc.albalab.Fixtures.dto
import cl.duoc.albalab.Fixtures.registro
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.data.remote.ApiAlbaLab
import cl.duoc.albalab.data.remote.Sesion
import cl.duoc.albalab.repository.RegistroRepository
import cl.duoc.albalab.viewmodel.RegistrosViewModel
import cl.duoc.albalab.viewmodel.filtrar
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
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
class RegistrosViewModelTest {
    private val ana = registro(1, "Ana Pérez", TipoAyuda.DONACION)
    private val bruno = registro(2, "Bruno Díaz", TipoAyuda.VOLUNTARIADO)
    private val mariana = registro(3, "Mariana Rojas", TipoAyuda.VOLUNTARIADO)
    private val todos = listOf(ana, bruno, mariana)

    private lateinit var dao: FakeRegistroDao
    private lateinit var api: ApiAlbaLab

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = FakeRegistroDao()
        api = mockk()
        Sesion.iniciar("token", "Gestor")
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    // WhileSubscribed solo emite con alguien escuchando.
    private fun TestScope.crearVm(): RegistrosViewModel {
        val vm = RegistrosViewModel(RegistroRepository(dao, api))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.estado.collect {} }
        return vm
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
    fun alCrearseTraeLaListaDelServidorYLaFiltra() = runTest {
        coEvery { api.listarSolicitudes() } returns todos.map(::dto)
        val vm = crearVm()

        assertEquals(3, vm.estado.value.total)
        assertTrue(vm.estado.value.cargado)
        assertFalse(vm.estado.value.actualizando)

        vm.onBusqueda("bru")
        assertEquals(listOf("Bruno Díaz"), vm.estado.value.filtrados.map { it.nombre })

        vm.onBusqueda("")
        vm.onTipo(TipoAyuda.DONACION)
        assertEquals(listOf(1), vm.estado.value.filtrados.map { it.id })
        assertEquals(TipoAyuda.DONACION, vm.tipo.value)

        vm.limpiarFiltros()
        assertEquals(3, vm.estado.value.filtrados.size)
        assertEquals("", vm.busqueda.value)
    }

    @Test
    fun sinConexionMuestraErrorYConservaLaCacheLocal() = runTest {
        dao.lista.value = listOf(ana)
        coEvery { api.listarSolicitudes() } throws IOException("sin red")
        val vm = crearVm()

        assertNotNull(vm.estado.value.error)
        assertEquals(1, vm.estado.value.total)
        assertFalse(vm.estado.value.sesionExpirada)
    }

    @Test
    fun tokenVencidoMarcaSesionExpiradaYCierraLaSesion() = runTest {
        coEvery { api.listarSolicitudes() } throws Fixtures.httpError(401)
        val vm = crearVm()

        assertTrue(vm.estado.value.sesionExpirada)
        assertFalse(Sesion.activa)
    }

    @Test
    fun reintentarDespuesDeUnErrorLimpiaElMensaje() = runTest {
        coEvery { api.listarSolicitudes() } throws IOException("sin red")
        val vm = crearVm()
        coEvery { api.listarSolicitudes() } returns listOf(dto(ana))

        vm.refrescar()

        assertNull(vm.estado.value.error)
        assertEquals(1, vm.estado.value.total)
    }
}
