package com.example.cryptorxjavaroomretrofit.model.database

import androidx.room.*

@Dao
interface CryptoDao {

    @Query("SELECT * FROM cryptoTable")
    fun findAll(): List<CryptoEntity>

    @Query("SELECT * FROM cryptoTable WHERE name LIKE :name")
    fun findCrypto(name: String): List<CryptoEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(crypto: List<CryptoEntity>): List<Long>

}