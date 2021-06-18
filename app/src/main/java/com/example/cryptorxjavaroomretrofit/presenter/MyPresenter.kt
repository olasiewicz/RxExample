package com.example.cryptorxjavaroomretrofit.presenter

import android.annotation.SuppressLint
import com.example.cryptorxjavaroomretrofit.Contract
import com.example.cryptorxjavaroomretrofit.R
import com.example.cryptorxjavaroomretrofit.model.Crypto
import com.example.cryptorxjavaroomretrofit.model.database.CryptoDB
import com.example.cryptorxjavaroomretrofit.model.database.CryptoDao
import com.example.cryptorxjavaroomretrofit.model.database.CryptoEntity
import com.example.cryptorxjavaroomretrofit.model.service.ApiService
import com.jakewharton.rxbinding2.InitialValueObservable
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import org.koin.core.KoinComponent
import org.koin.core.inject


class MyPresenter(override val view: Contract.ViewInterface) : Contract.PresenterInterface,
    KoinComponent {

    private val apiService: ApiService by inject()
    private val database: CryptoDao by inject()
    private val compositeDisposable: CompositeDisposable by inject()

    private lateinit var subscriber: DisposableSubscriber<List<CryptoEntity>>

    @SuppressLint("CheckResult")
    override fun fetchData() {

        apiService.getData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponseSuccess(response) }, { error -> onFailure(error) })
    }

    @SuppressLint("CheckResult")
    override fun onTextChanged(text: InitialValueObservable<CharSequence>) {
        text.observeOn(Schedulers.io())
            .map { if (it.length == 0) database.findAll() else database.findCrypto("%$it%") }
            .toFlowable(BackpressureStrategy.BUFFER)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(getSubscriber())
    }

    override fun destroy() {
        CryptoDB.destroyInstance()
        compositeDisposable.clear()
    }

    fun getSubscriber(): DisposableSubscriber<List<CryptoEntity>> {
        subscriber = object : DisposableSubscriber<List<CryptoEntity>>() {
            override fun onNext(listOfCrypto: List<CryptoEntity>) {
                if (listOfCrypto.isEmpty()) {
                    view.showToast(R.string.nothing_found)
                }
                view.showData(listOfCrypto)
            }

            override fun onError(error: Throwable) = view.error(error)
            override fun onComplete() = Unit

        }
        compositeDisposable.add(subscriber)
        return subscriber
    }


    private fun onFailure(error: Throwable) {
        view.error(error)
    }

    private fun onResponseSuccess(cryptoListResponse: List<Crypto>) {
        val lista = loadDataFromApiToDB(cryptoListResponse)
        val initialLoadDisposable = lista.subscribe()
        compositeDisposable.add(initialLoadDisposable)
    }

    @SuppressLint("CheckResult")
    private fun loadDataFromApiToDB(
        cryptoListResponse: List<Crypto>
    ): Flowable<List<Long>> {
        val lista = Maybe.fromAction<List<Long>> {

            val cryptoEntityList = arrayListOf<CryptoEntity>()
            for (crypto in cryptoListResponse) {
                cryptoEntityList.add(
                    CryptoEntity(
                        cryptoName = crypto.currency,
                        cryptoPrice = crypto.price
                    )
                )
            }
            database.insertAll(cryptoEntityList)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                view.showToast(R.string.success)
                view.addTextChangedListener()
            }
            .doOnError {
                view.showToast(R.string.error_inserting_toDB)
            }

        return lista.toFlowable()
    }
}