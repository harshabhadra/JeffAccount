package com.example.jeffaccount.network

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class TimeSheetAdd(
    @SerializedName("comid")
    @Expose
    var comid: String?,

    @SerializedName("apikey")
    @Expose
    var apikey: String?,

    @SerializedName("job_no")
    @Expose
    var jobNo: String?,

    @SerializedName("quotation_no")
    @Expose
    var quotationNo: String?,

    @SerializedName("worker_list")
    @Expose
    var workerList: List<WorkerList>?,
    @SerializedName("custname")
    @Expose
    var custname: String= "",
    @SerializedName("street")
    @Expose
    var street: String ="",
    @SerializedName("postCode")
    @Expose
    var postcode: String= "",
    @SerializedName("telephone")
    @Expose
    var telephone: String ="",
    @SerializedName("customerEmail")
    @Expose
    var customeremail: String = "",
    @SerializedName("web")
    @Expose
    var web: String= "",
    @SerializedName("country")
    @Expose
    var country:String = "",
    @SerializedName("county")
    @Expose
    var county:String = ""
)

@Parcelize
data class ItemNameList (
    @SerializedName("item_name")
    @Expose
    var itemName: String?
):Parcelable

@Parcelize
data class WorkerList(
    @SerializedName("name")
    @Expose
    var name: String?,

    @SerializedName("hours")
    @Expose
    var hours: Int?,

    @SerializedName("amount")
    @Expose
    var amount: Double?,

    @SerializedName("advance_amount")
    @Expose
    var advanceAmount: Double?,

    @SerializedName("total_amount")
    @Expose
    var totalAmount:Double?,

    @SerializedName("date")
    @Expose
    var date: String?,

    @SerializedName("telephone")
    @Expose
    var telephone: String?,

    @SerializedName("payment_method")
    @Expose
    var paymentMethod: String?,

    @SerializedName("comment")
    @Expose
    var comment: String?,

    @SerializedName("vat")
    @Expose
    var vat: Double?,

    @SerializedName("item_list")
    @Expose
    var itemList: List<ItemNameList>?
):Parcelable