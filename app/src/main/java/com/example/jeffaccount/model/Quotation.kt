package com.example.jeffaccount.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Quotation (
    @SerializedName("posts")
    @Expose
    var posts: List<QuotationPost>?
)

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

    @SerializedName("item_description")
    @Expose
    var itemDescription: String?,

    @SerializedName("quantity")
    @Expose
    var quantity: String?,

    @SerializedName("total_amount")
    @Expose
    var totalAmount: String?,

    @SerializedName("unit_amount")
    @Expose
    var unitAmount: String?,

    @SerializedName("advance_amount")
    @Expose
    var advanceAmount: String?,

    @SerializedName("vat")
    @Expose
    var vat: String?,

    @SerializedName("special_instruction")
    @Expose
    var specialInstruction: String?,

    @SerializedName("date")
    @Expose
    var date: String?,

    @SerializedName("payment_method")
    @Expose
    var paymentMethod: String?,

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(qid)
        parcel.writeString(jobNo)
        parcel.writeString(quotationNo)
        parcel.writeString(customerName)
        parcel.writeString(itemDescription)
        parcel.writeString(quantity)
        parcel.writeString(totalAmount)
        parcel.writeString(unitAmount)
        parcel.writeString(advanceAmount)
        parcel.writeString(vat)
        parcel.writeString(specialInstruction)
        parcel.writeString(date)
        parcel.writeString(paymentMethod)
        parcel.writeString(discountAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuotationPost> {
        override fun createFromParcel(parcel: Parcel): QuotationPost {
            return QuotationPost(parcel)
        }

        override fun newArray(size: Int): Array<QuotationPost?> {
            return arrayOfNulls(size)
        }
    }
}