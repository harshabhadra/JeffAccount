package com.example.jeffaccount.dataBase

import android.os.Parcel
import android.os.Parcelable
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
    var userName: String?,
    @ColumnInfo(name = "password")
    @NonNull
    var password:String?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(userName)
        parcel.writeString(password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LogInCred> {
        override fun createFromParcel(parcel: Parcel): LogInCred {
            return LogInCred(parcel)
        }

        override fun newArray(size: Int): Array<LogInCred?> {
            return arrayOfNulls(size)
        }
    }
}