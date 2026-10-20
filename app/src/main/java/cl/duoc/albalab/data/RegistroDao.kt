package cl.duoc.albalab.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.albalab.model.RegistroInteres
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {
    @Insert
    suspend fun insertar(r: RegistroInteres): Long

    @Update
    suspend fun actualizar(r: RegistroInteres)

    @Query("SELECT * FROM registros ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<RegistroInteres>>

    @Query("SELECT * FROM registros WHERE id = :id")
    suspend fun obtenerPorId(id: Int): RegistroInteres?
}
