package cl.duoc.albalab

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.albalab.data.AppDatabase
import cl.duoc.albalab.data.RegistroDao
import cl.duoc.albalab.model.ContactoPreferido
import cl.duoc.albalab.model.RegistroInteres
import cl.duoc.albalab.model.TipoAyuda
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: RegistroDao

    @Before
    fun crearBase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.registroDao()
    }

    @After
    fun cerrarBase() {
        db.close()
    }

    private fun registroDePrueba() = RegistroInteres(
        nombre = "Ana Pérez",
        telefono = "912345678",
        correo = "ana@mail.cl",
        direccion = "Calle Falsa 123",
        personas = 2,
        fechaContacto = "01/01/2027",
        contactoPreferido = ContactoPreferido.TELEFONO,
        tipoAyuda = TipoAyuda.DONACION,
        aceptaContacto = true,
        recibeNovedades = false
    )

    @Test
    fun insertar_y_obtenerTodos_devuelveUnRegistro() = runBlocking {
        dao.insertar(registroDePrueba())

        val registros = dao.obtenerTodos().first()

        assertEquals(1, registros.size)
        assertEquals("Ana Pérez", registros[0].nombre)
    }

    @Test
    fun actualizar_cambiaElTelefono() = runBlocking {
        val id = dao.insertar(registroDePrueba()).toInt()
        val guardado = dao.obtenerPorId(id)!!

        dao.actualizar(guardado.copy(telefono = "987654321"))

        assertEquals("987654321", dao.obtenerPorId(id)?.telefono)
    }
}
