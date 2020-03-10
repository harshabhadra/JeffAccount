package com.example.jeffaccount.dataBase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LogInDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(logInCred: LogInCred)

    @Query("SELECT * FROM log_in_user_table ORDER BY id ASC")
    fun getLogInCred():LiveData<List<LogInCred>>

    @Delete
    fun delete(logInCred: LogInCred)
}