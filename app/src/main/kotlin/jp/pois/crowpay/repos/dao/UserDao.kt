package jp.pois.crowpay.repos.dao

import androidx.room.*
import jp.pois.crowpay.repos.entities.User
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name")
    fun getUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: Long): Flow<User>

    @Query("SELECT * FROM users WHERE uuid = :uuid")
    fun getUser(uuid: UUID): User?

    @Transaction
    suspend fun putUser(uuid: UUID, name: String): User {
        return getUser(uuid) ?: User(name = name, uuid = uuid).let {
            val id = insertUser(it)
            it.copy(id = id)
        }
    }

    @Insert
    suspend fun insertUser(user: User): Long

    @Query("UPDATE users SET customized_name = :newCustomizedName WHERE id = :id")
    suspend fun updateCustomizedName(id: Long, newCustomizedName: String)
}
