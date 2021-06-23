package com.example.cryptorxjavaroomretrofit

import com.example.cryptorxjavaroomretrofit.model.database.CryptoEntity
import com.jakewharton.rxbinding2.InitialValueObservable
import io.reactivex.Observable

interface Contract {

    interface ViewInterface {
        fun showData(list: List<CryptoEntity>)
        fun databaseDataCompletion(message: Int)
        fun error(error: Throwable)
        fun observeTextChangedListener() : InitialValueObservable<CharSequence>
    }

    interface PresenterInterface {
        val view : ViewInterface
        fun init()
        fun destroy()
    }

}