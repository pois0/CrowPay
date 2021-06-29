package jp.pois.crowpay.repos.dao

import androidx.room.*
import jp.pois.crowpay.repos.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name")
    fun getUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: Long): Flow<User>

    @Insert
    suspend fun insertUser(user: User): Long

    @Query("UPDATE users SET customized_name = :newCustomizedName WHERE id = :id")
    suspend fun updateCustomizedName(id: Long, newCustomizedName: String)
}
