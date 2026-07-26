package com.demo.projectbase

import android.app.Application
import com.demo.projectbase.core.network.networkModule
import com.demo.projectbase.di.appModule
import com.demo.projectbase.feature.auth.di.authModule
import com.demo.projectbase.feature.home.di.homeModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule, networkModule, authModule, homeModule)
        }
    }
}
