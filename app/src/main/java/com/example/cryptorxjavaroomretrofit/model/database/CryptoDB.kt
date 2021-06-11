package com.example.cryptorxjavaroomretrofit.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(CryptoEntity::class)], version = 1)
abstract class CryptoDB : RoomDatabase() {

    abstract fun cryptoDao(): CryptoDao

    companion object {

        private var INSTANCE: CryptoDB? = null

        fun getInstance(context: Context): CryptoDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        CryptoDB::class.java, "crypto.db"
                    )
                        .build()
                }
            }
            return INSTANCE as CryptoDB
        }

        fun destroyInstance() {
            INSTANCE = null
        }


    }
}