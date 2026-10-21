package cl.duoc.albalab

import cl.duoc.albalab.model.ContactoPreferido
import cl.duoc.albalab.model.TipoAyuda
import cl.duoc.albalab.viewmodel.Validaciones
import java.text.SimpleDateFormat
import java.util.Locale
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {
    private val hoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse("21/10/2026")!!

    @Test
    fun nombre() {
        assertNull(Validaciones.nombre("Ana Pérez"))
        assertNotNull(Validaciones.nombre(""))
        assertNotNull(Validaciones.nombre("Al"))
    }

    @Test
    fun telefono() {
        assertNull(Validaciones.telefono("912345678"))
        assertNotNull(Validaciones.telefono("12345678"))
        assertNotNull(Validaciones.telefono("12345678a"))
    }

    @Test
    fun correo() {
        assertNull(Validaciones.correo("ana@mail.cl"))
        assertNotNull(Validaciones.correo("a@b"))
        assertNotNull(Validaciones.correo(""))
    }

    @Test
    fun direccion() {
        assertNull(Validaciones.direccion("Calle 123"))
        assertNotNull(Validaciones.direccion(""))
        assertNotNull(Validaciones.direccion("abc"))
    }

    @Test
    fun personas() {
        assertNull(Validaciones.personas("20"))
        assertNotNull(Validaciones.personas("0"))
        assertNotNull(Validaciones.personas("abc"))
        assertNotNull(Validaciones.personas("501"))
    }

    @Test
    fun fecha() {
        assertNull(Validaciones.fecha("21/10/2026", hoy))
        assertNull(Validaciones.fecha("01/01/2027", hoy))
        assertNotNull(Validaciones.fecha("31/02/2027", hoy))
        assertNotNull(Validaciones.fecha("1/1/2027", hoy))
        assertNotNull(Validaciones.fecha("01/01/2027x", hoy))
        assertNotNull(Validaciones.fecha("20/10/2026", hoy))
    }

    @Test
    fun contacto() {
        assertNull(Validaciones.contacto(ContactoPreferido.CORREO))
        assertNotNull(Validaciones.contacto(null))
    }

    @Test
    fun tipoAyuda() {
        assertNull(Validaciones.tipoAyuda(TipoAyuda.DONACION))
        assertNotNull(Validaciones.tipoAyuda(null))
    }

    @Test
    fun aceptaContacto() {
        assertNull(Validaciones.aceptaContacto(true))
        assertNotNull(Validaciones.aceptaContacto(false))
    }
}
