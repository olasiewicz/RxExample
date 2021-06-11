package com.example.cryptorxjavaroomretrofit.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptorxjavaroomretrofit.R
import com.example.cryptorxjavaroomretrofit.databinding.ActivityMainBinding

import com.example.cryptorxjavaroomretrofit.model.Crypto
import com.example.cryptorxjavaroomretrofit.model.database.CryptoDB
import com.example.cryptorxjavaroomretrofit.model.database.CryptoDao
import com.example.cryptorxjavaroomretrofit.model.database.CryptoEntity
import com.example.cryptorxjavaroomretrofit.model.service.RetrofitInstance
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private val TAG = MainActivity::class.simpleName
    lateinit var database: CryptoDao
    var cryptoAdapter = CryptoAdapter()
    private lateinit var subscriber: DisposableSubscriber<List<CryptoEntity>>
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initRecyclerView()
        fetchDataFromRestApi()
    }

    @SuppressLint("CheckResult")
    private fun fetchDataFromRestApi() {
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

        val lista = loadDataFromApiToDB(applicationContext, cryptoListResponse)
        val initialLoadDisposable = lista.subscribe()
        compositeDisposable.add(initialLoadDisposable)
    }

    @SuppressLint("CheckResult")
    private fun loadDataFromApiToDB(
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

                RxTextView.textChanges(binding.editText)
                    .observeOn(Schedulers.io())
                    .map { if (it.length == 0) database.findAll() else database.findCrypto("%$it%")  }
                    .toFlowable(BackpressureStrategy.BUFFER)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(getSubscriber())


            }
            .doOnError {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_inserting_toDB),
                    Toast.LENGTH_LONG
                ).show()
            }

        return lista.toFlowable()
    }



    private fun initRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = cryptoAdapter
    }

    fun getSubscriber(): DisposableSubscriber<List<CryptoEntity>> {
        subscriber = object : DisposableSubscriber<List<CryptoEntity>>() {
            override fun onNext(listOfCrypto: List<CryptoEntity>) {
                if (listOfCrypto.isEmpty()) {
                    Toast.makeText(applicationContext, R.string.nothing_found, Toast.LENGTH_SHORT)
                        .show()
                }
                cryptoAdapter.cryptoList = listOfCrypto
            }

            override fun onError(p0: Throwable?) = Unit
            override fun onComplete() = Unit

        }
        compositeDisposable.add(subscriber)
        return subscriber
    }

    override fun onDestroy() {
        super.onDestroy()
        CryptoDB.destroyInstance()
        compositeDisposable.clear()
    }

}