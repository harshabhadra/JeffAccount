package com.example.jeffaccount.network

import com.example.jeffaccount.model.SupPost
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class SearchSupplierPost(
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
    var country: String?,
    @SerializedName("county")
    @Expose
    var county: String
)

data class SearchSupplier(
    @SerializedName("posts")
    @Expose
    var posts: List<SearchSupplierPost>?
)

fun SearchSupplierPost.asSupplierPost(): SupPost {
    return SupPost(
        this.supid,
        this.supname,
        this.street,
        this.postcode,
        this.telephone,
        this.supemail,
        this.web,
        this.country,
        this.county
    )
}