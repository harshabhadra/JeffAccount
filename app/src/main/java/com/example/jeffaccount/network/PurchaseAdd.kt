package com.example.jeffaccount.network

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class PurchaseAdd (
    @SerializedName("comid")
    @Expose
    var comid: String,

    @SerializedName("apikey")
    @Expose
    var apikey: String,

    @SerializedName("job_no")
    @Expose
    var jobNo: String,

    @SerializedName("quotation_no")
    @Expose
    var quotationNo: String,

    @SerializedName("sup_list")
    @Expose
    var supList: List<SupList>?,
    @SerializedName("custname")
    @Expose
    var custname: String? = "",
    @SerializedName("street")
    @Expose
    var street: String? ="",
    @SerializedName("postCode")
    @Expose
    var postcode: String? = "",
    @SerializedName("telephone")
    @Expose
    var telephone: String? ="",
    @SerializedName("customerEmail")
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
    var county:String? = "",
    @SerializedName("date")
    @Expose
    var date: String? = "",
    @SerializedName("comment")
    @Expose
    var comment: String? = ""
)

@Parcelize
data class SupList(
    @SerializedName("sup_name")
    @Expose
    var supName: String?="",

    @SerializedName("date")
    @Expose
    var supDate:String? = "",

    @SerializedName("county")
    @Expose
    var county:String? = "",

    @SerializedName("payment_method")
    @Expose
    var paymentMethod: String? = "",

    @SerializedName("vat")
    @Expose
    var vat: Double?=0.0,

    @SerializedName("item_list")
    @Expose
    var itemList: List<Item>?
):Parcelable