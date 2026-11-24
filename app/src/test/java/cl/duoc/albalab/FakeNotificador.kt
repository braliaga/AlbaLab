package cl.duoc.albalab

import cl.duoc.albalab.viewmodel.Notificador

class FakeNotificador : Notificador {
    val avisos = mutableListOf<String>()

    override fun registroGuardado(nombre: String) {
        avisos += nombre
    }
}
