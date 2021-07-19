package jp.pois.crowpay.repos

import jp.pois.crowpay.repos.dao.BalanceDao
import jp.pois.crowpay.repos.dao.UserDao
import jp.pois.crowpay.repos.entities.User
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
) {
    fun getUsers() = userDao.getUsers()

    fun getUser(id: Long) = userDao.getUser(id)

    suspend fun putUser(uuid: UUID, name: String): User = userDao.putUser(uuid, name)

    suspend fun addUser(user: User) = userDao.insertUser(user)

    suspend fun updateCustomizedName(id: Long, newCustomizedName: String) = userDao.updateCustomizedName(id, newCustomizedName)
}
