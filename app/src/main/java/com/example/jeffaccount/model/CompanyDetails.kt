package com.example.jeffaccount.model

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CompanyDetails (
    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("comid")
    @Expose
    var comid: String,

    @SerializedName("caomimge")
    @Expose
    var caomimge: String?,
    @SerializedName("comname")
    @Expose
    var comname: String,

    @SerializedName("companydes")
    @Expose
    var comDesription:String?,

    @SerializedName("street")
    @Expose
    var street: String,

    @SerializedName("postcode")
    @Expose
    var postcode: String,

    @SerializedName("telephone")
    @Expose
    var telephone: String,

    @SerializedName("comemail")
    @Expose
    var comemail: String?,

    @SerializedName("web")
    @Expose
    var web: String,

    @SerializedName("country")
    @Expose
    var country: String,

    @SerializedName("county")
    @Expose
    var county: String?
):Parcelable