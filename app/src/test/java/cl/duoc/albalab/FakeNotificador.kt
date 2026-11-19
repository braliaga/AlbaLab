package cl.duoc.albalab

import cl.duoc.albalab.utils.Notificador

class FakeNotificador : Notificador {
    val nombres = mutableListOf<String>()

    override fun registroGuardado(nombre: String) {
        nombres += nombre
    }
}
