package jp.pois.crowpay.repos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import jp.pois.crowpay.repos.dao.BalanceDao
import jp.pois.crowpay.repos.dao.UserDao
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.repos.entities.Repayment
import jp.pois.crowpay.repos.entities.User
import jp.pois.crowpay.utils.Direction
import jp.pois.crowpay.utils.LocalDateConverters
import jp.pois.crowpay.utils.UUIDConverters

@Database(entities = [User::class, Balance::class, Repayment::class], version = 1)
@TypeConverters(Direction.Converters::class, LocalDateConverters::class, UUIDConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun balanceDao(): BalanceDao

    companion object {
        private const val DATABASE_NAME = "crowpay_db"

        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context)
            = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }
}
