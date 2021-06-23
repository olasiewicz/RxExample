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
import org.koin.core.KoinComponent
import org.koin.core.inject


@SuppressLint("CheckResult")
class MyPresenter (override val view: Contract.ViewInterface) : Contract.PresenterInterface,
    KoinComponent {

    private val apiService: ApiService by inject()
    private val database: CryptoDao by inject()
    private val compositeDisposable: CompositeDisposable by inject()

    @SuppressLint("CheckResult")
    override fun init() {
    fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        apiService.getData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponseSuccess(response) }, { error -> onFailure(error) })
    }

    override fun destroy() {
        CryptoDB.destroyInstance()
        compositeDisposable.clear()
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
                view.databaseDataCompletion(R.string.success)

                val observable: InitialValueObservable<CharSequence> = view.observeTextChangedListener()
                observable
                    .observeOn(Schedulers.io())
                    .map { if (it.length == 0) database.findAll() else database.findCrypto("%$it%") }
                    .toFlowable(BackpressureStrategy.BUFFER)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { response -> view.showData(response) },
                        { error -> onFailure(error) })
                compositeDisposable.add(observable.subscribe())
            }
            .doOnError {
                view.databaseDataCompletion(R.string.error_inserting_toDB)
            }
        compositeDisposable.add(lista.subscribe())

        return lista.toFlowable()
    }
}