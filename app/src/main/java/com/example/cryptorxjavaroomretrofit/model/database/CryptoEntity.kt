package com.example.cryptorxjavaroomretrofit.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cryptoTable", indices = [Index(value = ["name"], unique = true)])
data class CryptoEntity(
    @PrimaryKey(autoGenerate = true) var uid: Long = 0,
    @ColumnInfo(name = "name") val cryptoName: String,
    @ColumnInfo(name = "price") val cryptoPrice: String
)