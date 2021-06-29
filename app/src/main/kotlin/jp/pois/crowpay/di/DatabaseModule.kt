package jp.pois.crowpay.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.pois.crowpay.repos.AppDatabase
import jp.pois.crowpay.repos.dao.BalanceDao
import jp.pois.crowpay.repos.dao.UserDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Provides
    fun provideBalanceDao(appDatabase: AppDatabase): BalanceDao = appDatabase.balanceDao()
}
