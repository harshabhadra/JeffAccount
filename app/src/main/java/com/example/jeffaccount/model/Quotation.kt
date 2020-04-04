package com.example.jeffaccount.model

import android.os.Parcelable
import com.example.jeffaccount.network.Item
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Quotation (
    @SerializedName("posts")
    @Expose
    var posts: List<QuotationPost>?
)

@Parcelize
data class QuotationPost (
    @SerializedName("qid")
    @Expose
    var qid: String?,

    @SerializedName("job_no")
    @Expose
    var jobNo: String?,

    @SerializedName("quotation_no")
    @Expose
    var quotationNo: String?,

    @SerializedName("customer_name")
    @Expose
    var customerName: String?,

    @SerializedName("date")
    @Expose
    var date: String?,
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
    @SerializedName("payment_method")
    @Expose
    var paymentMethod:String?,
    @SerializedName("comment")
    @Expose
    var comment:String?,
    @SerializedName("vat")
    @Expose
    var vat:String?,
    @SerializedName("item_description")
    @Expose
    var itemDescription: MutableList<Item>
):Parcelable

@Parcelize
data class ItemDescription (
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
    var unitAmount: Int?,

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: Int?,

    @SerializedName("total_amount")
    @Expose
    var totalAmount: Int?,

    @SerializedName("vat")
    @Expose
    var vat: Int?):Parcelable