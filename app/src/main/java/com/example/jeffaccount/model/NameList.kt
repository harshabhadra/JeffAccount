package com.example.jeffaccount.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NameList (val names:MutableList<String>
):Parcelable