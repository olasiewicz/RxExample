package com.example.cryptorxjavaroomretrofit

import android.app.Application
import com.example.cryptorxjavaroomretrofit.di_module.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(appModule))
        }

    }
}