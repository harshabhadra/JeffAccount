package com.example.jeffaccount.network

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class SearchCustomer (
    @SerializedName("comid")
    @Expose
    var comid: String?,

    @SerializedName("custname")
    @Expose
    var custname: String?,

    @SerializedName("street")
    @Expose
    var street: String?,

    @SerializedName("postcode")
    @Expose
    var postcode: String?,

    @SerializedName("telephone")
    @Expose
    var telephone: String?,

    @SerializedName("comemail")
    @Expose
    var comemail: Any?,

    @SerializedName("web")
    @Expose
    var web: String?,

    @SerializedName("country")
    @Expose
    var country: String?
)