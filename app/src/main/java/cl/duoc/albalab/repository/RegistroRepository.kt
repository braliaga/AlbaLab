package cl.duoc.albalab.repository

import cl.duoc.albalab.data.local.RegistroDao
import cl.duoc.albalab.data.model.RegistroInteres
import kotlinx.coroutines.flow.Flow

// Aquí se reemplazaría el DAO por llamadas a la API REST de CORPALIV
class RegistroRepository(private val dao: RegistroDao) {
    suspend fun insertar(r: RegistroInteres): Long = dao.insertar(r)

    suspend fun actualizar(r: RegistroInteres) = dao.actualizar(r)

    fun obtenerTodos(): Flow<List<RegistroInteres>> = dao.obtenerTodos()

    suspend fun obtenerPorId(id: Int): RegistroInteres? = dao.obtenerPorId(id)
}
