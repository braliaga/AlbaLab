package cl.duoc.albalab

import cl.duoc.albalab.data.local.RegistroDao
import cl.duoc.albalab.data.model.RegistroInteres
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
