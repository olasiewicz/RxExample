package com.example.cryptorxjavaroomretrofit.application

import android.app.Application
import com.example.cryptorxjavaroomretrofit.di_module.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(appModule))
        }

    }
}