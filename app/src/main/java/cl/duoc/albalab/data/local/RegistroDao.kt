package cl.duoc.albalab.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import cl.duoc.albalab.data.model.RegistroInteres
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(r: RegistroInteres): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(lista: List<RegistroInteres>)

    @Update
    suspend fun actualizar(r: RegistroInteres)

    @Query("SELECT * FROM registros ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<RegistroInteres>>

    @Query("SELECT * FROM registros")
    suspend fun listar(): List<RegistroInteres>

    @Query("SELECT * FROM registros WHERE id = :id")
    suspend fun obtenerPorId(id: Int): RegistroInteres?

    @Query("DELETE FROM registros WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Query("DELETE FROM registros")
    suspend fun borrarTodos()

    // La lista del servidor reemplaza la caché completa (así desaparecen las eliminadas desde otro dispositivo).
    @Transaction
    suspend fun reemplazarTodos(lista: List<RegistroInteres>) {
        borrarTodos()
        insertarTodos(lista)
    }
}
