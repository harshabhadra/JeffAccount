package com.example.jeffaccount.model

import android.os.Parcel
import android.os.Parcelable
import com.example.jeffaccount.network.SearchSupplierPost
import com.example.jeffaccount.network.asSupplierPost
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Supplier (
    @SerializedName("posts")
    @Expose
    var posts: List<SupPost>
)

@Parcelize
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
    var country: String?,
    @SerializedName("county")
    @Expose
    var county:String
):Parcelable

fun SupPost.asSearchSupplierPost():SearchSupplierPost{
    return SearchSupplierPost(
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