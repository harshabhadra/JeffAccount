package com.example.jeffaccount.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class CompanyLogo (
    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("caomimge")
    @Expose
    var caomimge: String
)