package com.example.cryptorxjavaroomretrofit.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.cryptorxjavaroomretrofit.R
import com.example.cryptorxjavaroomretrofit.model.Crypto
import com.example.cryptorxjavaroomretrofit.model.database.CryptoDB
import com.example.cryptorxjavaroomretrofit.model.database.CryptoDao
import com.example.cryptorxjavaroomretrofit.model.database.CryptoEntity
import com.example.cryptorxjavaroomretrofit.model.service.RetrofitInstance
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private val TAG = MainActivity::class.simpleName
    lateinit var database: CryptoDao

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitInstance.apiService.getData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponseSuccess(response) }, { error -> onFailure(error) })
    }

    private fun onFailure(error: Throwable) {
        Log.e(TAG, error.message!!)
    }

    private fun onResponseSuccess(cryptoListResponse: List<Crypto>) {

        Log.d(TAG, "response success from Api")

        val lista = loadInitialData(applicationContext, cryptoListResponse)
        val initialLoadDisposable = lista.subscribe()
        compositeDisposable.add(initialLoadDisposable)
    }

    @SuppressLint("CheckResult")
    private fun loadInitialData(
        context: Context,
        cryptoListResponse: List<Crypto>
    ): Flowable<List<Long>> {
        val lista = Maybe.fromAction<List<Long>> {

            database = CryptoDB.getInstance(context).cryptoDao()

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
                Toast.makeText(context, getString(R.string.success), Toast.LENGTH_LONG).show()

            }
            .doOnError {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_inserting_cheeses),
                    Toast.LENGTH_LONG
                ).show()
            }

        return lista.toFlowable()
    }

    override fun onDestroy() {
        super.onDestroy()
        CryptoDB.destroyInstance()
        compositeDisposable.clear()
    }
}