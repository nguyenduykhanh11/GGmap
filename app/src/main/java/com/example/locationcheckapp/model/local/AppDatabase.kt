package com.example.locationcheckapp.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.locationcheckapp.model.Entity.room.Location

@Database(entities = [Location::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, AppDatabase::class.java, LOCATION_DATABASE)
                        .build()
                INSTANCE = instance

                instance
            }
        }
    }
}

const val LOCATION_TABLE = "LOCATION_TABLE"
const val LOCATION_DATABASE = "LOCATION_DATABASE"