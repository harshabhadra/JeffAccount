package com.example.jeffaccount.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Company (
    @SerializedName("posts")
    @Expose
    var posts: List<Post>?
)

data class ComPost (
    @SerializedName("comid")
    @Expose
    var comid: String?,

    @SerializedName("comname")
    @Expose
    var comname: String?,

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
    var comemail: String?,

    @SerializedName("web")
    @Expose
    var web: String?,

    @SerializedName("country")
    @Expose
    var country: String?
)