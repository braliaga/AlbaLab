package cl.duoc.albalab

import cl.duoc.albalab.data.local.RegistroDao
import cl.duoc.albalab.data.model.RegistroInteres
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// Room en memoria para tests JVM: se comporta como la caché real (reemplaza por id).
class FakeRegistroDao : RegistroDao {
    val lista = MutableStateFlow<List<RegistroInteres>>(emptyList())

    override suspend fun insertar(r: RegistroInteres): Long {
        lista.value = listOf(r) + lista.value.filterNot { it.id == r.id }
        return r.id.toLong()
    }

    override suspend fun insertarTodos(lista: List<RegistroInteres>) {
        lista.forEach { insertar(it) }
    }

    override suspend fun actualizar(r: RegistroInteres) {
        lista.value = lista.value.map { if (it.id == r.id) r else it }
    }

    override fun obtenerTodos(): Flow<List<RegistroInteres>> = lista

    override suspend fun listar(): List<RegistroInteres> = lista.value

    override suspend fun obtenerPorId(id: Int): RegistroInteres? = lista.value.firstOrNull { it.id == id }

    override suspend fun eliminar(id: Int) {
        lista.value = lista.value.filterNot { it.id == id }
    }

    override suspend fun borrarTodos() {
        lista.value = emptyList()
    }
}
