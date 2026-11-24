package cl.duoc.albalab

import cl.duoc.albalab.data.model.EstadoSolicitud
import cl.duoc.albalab.utils.aPesosChilenos
import cl.duoc.albalab.utils.aPesosConDecimales
import cl.duoc.albalab.utils.equivalenteUfUsd
import cl.duoc.albalab.viewmodel.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatoTest {
    @Test
    fun pesosChilenosSinDecimales() {
        assertEquals("$18.500", 18500.aPesosChilenos())
    }

    @Test
    fun pesosConDosDecimales() {
        assertEquals("$41.391,69", 41391.69.aPesosConDecimales())
    }

    @Test
    fun equivalenciaEnUfYDolares() {
        assertEquals("≈ 0,50 UF · US$ 20,00", equivalenteUfUsd(20000, 40000.0, 1000.0))
    }

    @Test
    fun estadosFinalesDeUnaSolicitud() {
        assertTrue(EstadoSolicitud.CONCRETADO.esFinal)
        assertTrue(EstadoSolicitud.DESCARTADO.esFinal)
        assertFalse(EstadoSolicitud.EN_REVISION.esFinal)
    }

    @Test
    fun claveObligatoriaEnElLogin() {
        assertNotNull(Validaciones.clave("  "))
        assertNull(Validaciones.clave("Alba2026!"))
    }
}
