package com.example.jeffaccount.dataBase

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_in_user_table")
data class LogInCred(

    @PrimaryKey(autoGenerate = true)
    var id:Long = 0L,
    @ColumnInfo(name = "user_name")
    @NonNull
    var userName: String,
    @ColumnInfo(name = "password")
    @NonNull
    var password:String)