package com.example.jeffaccount.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TimeSheetUpdate(
    @SerializedName("comid")
    @Expose
    var comid: String?,

    @SerializedName("tid")
    @Expose
    var tid: String,

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
    var custname: String = "",
    @SerializedName("street")
    @Expose
    var street: String = "",
    @SerializedName("postCode")
    @Expose
    var postcode: String = "",
    @SerializedName("telephone")
    @Expose
    var telephone: String = "",
    @SerializedName("customerEmail")
    @Expose
    var customeremail: String = "",
    @SerializedName("web")
    @Expose
    var web: String = "",
    @SerializedName("country")
    @Expose
    var country: String = "",
    @SerializedName("county")
    @Expose
    var county: String = ""
)
