package com.example.jeffaccount.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Logos(
    @SerializedName("posts")
    @Expose
    val logoList:List<Logo>?
)
data class Logo (
    @SerializedName("imgid")
    @Expose
    var imgid: String,

    @SerializedName("file_name")
    @Expose
    var fileName: String
)