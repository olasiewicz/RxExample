package com.example.cryptorxjavaroomretrofit.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptorxjavaroomretrofit.Contract
import com.example.cryptorxjavaroomretrofit.R
import com.example.cryptorxjavaroomretrofit.databinding.ActivityMainBinding
import com.example.cryptorxjavaroomretrofit.model.database.CryptoEntity
import com.jakewharton.rxbinding2.widget.RxTextView
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(), Contract.ViewInterface {

    private val presenter: Contract.PresenterInterface by inject { parametersOf(this)  }
    private val cryptoAdapter: CryptoAdapter by inject()
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initRecyclerView()
        presenter.init()
    }

    override fun showData(list: List<CryptoEntity>) {
        if (list.isEmpty()) {
            Toast.makeText(this, R.string.nothing_found, Toast.LENGTH_LONG).show()
        }
        cryptoAdapter.cryptoList = list
    }

    private fun initRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = cryptoAdapter
    }

    override fun databaseDataCompletion(message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun error(error: Throwable) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
    }

    @SuppressLint("CheckResult")
    override fun observeTextChangedListener()= (RxTextView.textChanges(binding.editText))

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

}