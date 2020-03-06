package com.example.jeffaccount.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Supplier (
    @SerializedName("posts")
    @Expose
    var posts: List<SupPost>
)


data class SupPost (
    @SerializedName("supid")
    @Expose
    var supid: String?,
    @SerializedName("supname")
    @Expose
    var supname: String?,
    @SerializedName("street")
    @Expose
    var street: String?,
    @SerializedName("postcode")
    @Expose
    var postcode: String?,
    @SerializedName("telephone")
    @Expose
    var telephone: String?,
    @SerializedName("supemail")
    @Expose
    var supemail: String?,
    @SerializedName("web")
    @Expose
    var web: String?,
    @SerializedName("country")
    @Expose
    var country: String?
)