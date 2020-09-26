package com.example.jeffaccount.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Customer (
    @SerializedName("posts")
    @Expose
    var posts: List<Post>
)

@Parcelize
data class Post(
    @SerializedName("custid")
    @Expose
    var custid: String?,
    @SerializedName("custname")
    @Expose
    var custname: String? = "",
    @SerializedName("street")
    @Expose
    var street: String? = "",
    @SerializedName("postcode")
    @Expose
    var postcode: String? = "",
    @SerializedName("telephone")
    @Expose
    var telephone: String? = "",
    @SerializedName("customeremail")
    @Expose
    var customeremail: String? = "",
    @SerializedName("web")
    @Expose
    var web: String? = "",
    @SerializedName("country")
    @Expose
    var country:String? = "",
    @SerializedName("county")
    @Expose
    var county:String? = ""
):Parcelable