package com.example.jeffaccount.model

import android.os.Parcel
import android.os.Parcelable
import com.example.jeffaccount.network.Item
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Purchase(
    @SerializedName("posts")
    @Expose
    var posts: List<PurchasePost>
)

@Parcelize
data class PurchasePost(
    @SerializedName("pid")
    @Expose
    var pid: String?,

    @SerializedName("job_no")
    @Expose
    var jobNo: String?,

    @SerializedName("quotation_no")
    @Expose
    var quotationNo: String?,

    @SerializedName("sup_name")
    @Expose
    var customerName: String?,

    @SerializedName("comment")
    @Expose
    var specialInstruction: String?,

    @SerializedName("date")
    @Expose
    var date: String?,
    @SerializedName("payment_method")
    @Expose
    var paymentMethod: String?,
    @SerializedName("street_address")
    @Expose
    var street:String?,
    @SerializedName("country")
    @Expose
    var country:String?,
    @SerializedName("post_code")
    @Expose
    var postCode:String?,
    @SerializedName("telephone")
    @Expose
    var telephone:String?,
    @SerializedName("item_description")
    @Expose
    var itemDescription: MutableList<Item>
):Parcelable