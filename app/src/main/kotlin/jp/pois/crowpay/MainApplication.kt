package jp.pois.crowpay

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val pref = getSharedPreferences("jp.pois.crowpay.IDENTIFIER", Context.MODE_PRIVATE) // TODO
        pref.edit().clear().apply()
    }
}
