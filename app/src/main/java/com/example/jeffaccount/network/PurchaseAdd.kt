package com.example.jeffaccount.network

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PurchaseAdd(
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
):Parcelable

@Parcelize
data class PItem (
    @SerializedName("no_of_item")
    @Expose
    var noOfItem: Int?,

    @SerializedName("item_des")
    @Expose
    var itemDes: String?,

    @SerializedName("qty")
    @Expose
    var qty: Int?,

    @SerializedName("unit_amount")
    @Expose
    var unitAmount: Double?,

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: Double?,

    @SerializedName("total_amount")
    @Expose
    var totalAmount: Double?,

    @SerializedName("vat")
    @Expose
    var vat: Double?
):Parcelable