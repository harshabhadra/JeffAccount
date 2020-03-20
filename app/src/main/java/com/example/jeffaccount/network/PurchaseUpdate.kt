package com.example.jeffaccount.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PurchaseUpdate(
    @SerializedName("pid")
    @Expose
    var pid:String?,
    @SerializedName("apikey")
    @Expose
    var apikey: String?,

    @SerializedName("job_no")
    @Expose
    var jobNo: String?,

    @SerializedName("quotation_no")
    @Expose
    var quotationNo: String?,

    @SerializedName("sup_name")
    @Expose
    var customerName: String?,

    @SerializedName("date")
    @Expose
    var date: String?,

    @SerializedName("street_address")
    @Expose
    var streetAddress: String?,

    @SerializedName("country")
    @Expose
    var country: String?,

    @SerializedName("post_code")
    @Expose
    var postCode: String?,

    @SerializedName("telephone")
    @Expose
    var telephone: String?,
    @SerializedName("payment_method")
    @Expose
    var paymentMethod:String?,
    @SerializedName("comment")
    @Expose
    var comment:String?,
    @SerializedName("items")
    @Expose
    var items: List<Item>?
)