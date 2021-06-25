package com.example.cryptorxjavaroomretrofit.model.service

import com.example.cryptorxjavaroomretrofit.BuildConfig
import com.example.cryptorxjavaroomretrofit.model.Crypto
import io.reactivex.Single
import retrofit2.http.GET

interface ApiService {

    @GET("prices?key=${BuildConfig.API_KEY}")
    fun getData() : Single<List<Crypto>>

}