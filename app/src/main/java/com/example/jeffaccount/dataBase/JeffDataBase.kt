package com.example.jeffaccount.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LogInCred::class], version = 2, exportSchema = false)
abstract class JeffDataBase : RoomDatabase() {

    abstract val loginDao: LogInDao

    companion object {

        private var INSTANCE: JeffDataBase? = null

        fun getInstance(context: Context): JeffDataBase {
            synchronized(this) {

                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context,
                            JeffDataBase::class.java,
                            "Jeff_Database"
                        )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}