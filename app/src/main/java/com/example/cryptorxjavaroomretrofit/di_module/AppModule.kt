package com.example.cryptorxjavaroomretrofit.di_module

import android.content.Context
import com.example.cryptorxjavaroomretrofit.BuildConfig
import com.example.cryptorxjavaroomretrofit.model.database.CryptoDB
import com.example.cryptorxjavaroomretrofit.model.database.CryptoDao
import com.example.cryptorxjavaroomretrofit.model.service.ApiService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {
    single { provideRetrofit(BuildConfig.BASE_URL) }
    single { provideApiService(get()) }
    single { provideDatabase(androidContext()) }
}

private fun provideRetrofit(
    BASE_URL: String
): Retrofit =
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

private fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

private fun provideDatabase(context: Context) : CryptoDao = CryptoDB.getInstance(context).cryptoDao()
