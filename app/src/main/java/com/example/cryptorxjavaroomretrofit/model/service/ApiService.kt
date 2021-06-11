package com.example.cryptorxjavaroomretrofit.model.service

import com.example.cryptorxjavaroomretrofit.model.Crypto
import io.reactivex.Single
import retrofit2.http.GET

interface ApiService {

    @GET("prices?key=ad1c827dda8c777fbc36e7268dfa123b674fb862")
    fun getData() : Single<List<Crypto>>

}