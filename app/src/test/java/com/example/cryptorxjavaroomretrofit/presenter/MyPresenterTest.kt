package com.example.cryptorxjavaroomretrofit.presenter

import com.example.cryptorxjavaroomretrofit.Contract
import com.example.cryptorxjavaroomretrofit.di_module.appModule
import com.nhaarman.mockito_kotlin.mock
import org.junit.After

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito

class MyPresenterTest : KoinTest {

    private val view: Contract.ViewInterface = mock()
    private val presenter: Contract.PresenterInterface by inject { parametersOf(view) }

    @Before
    fun before() {
        startKoin {   modules(
            listOf(appModule)
        ) }
    }

    @After
    fun after() {
        stopKoin()
    }

//    @Test
//    fun `check if the value returned by the presenter is student`() {
//        val profileName = "student"
//        assert(profileName == presenter.)
//    }

    @Test
    fun `check if presenter invokes view showData method`() {
        // presenter.onComplete()
        Mockito.verify(view).showData()
    }

}