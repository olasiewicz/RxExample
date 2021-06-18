package com.example.cryptorxjavaroomretrofit

import com.example.cryptorxjavaroomretrofit.model.database.CryptoEntity
import com.jakewharton.rxbinding2.InitialValueObservable

interface Contract {

    interface ViewInterface {
        fun showData(list: List<CryptoEntity>)
        fun initRecyclerView()
        fun showToast(message: Int)
        fun error(error: Throwable)
        fun addTextChangedListener()
    }

    interface PresenterInterface {
        val view : ViewInterface
        fun fetchData()
        fun onTextChanged(text: InitialValueObservable<CharSequence>)
        fun destroy()
    }

}