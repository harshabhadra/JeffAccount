package com.example.jeffaccount.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class TimeSheet(
    @SerializedName("posts")
    @Expose
    var posts: List<TimeSheetPost>
)

data class TimeSheetPost (
    @SerializedName("tid")
    @Expose
    var tid: String?,

    @SerializedName("job_no")
    @Expose
    var jobNo: String?,

    @SerializedName("quotation_no")
    @Expose
    var quotationNo: String?,

    @SerializedName("name")
    @Expose
    var name: String?,

    @SerializedName("item_description")
    @Expose
    var itemDescription: String?,

    @SerializedName("hrs")
    @Expose
    var hrs: String?,

    @SerializedName("total_amount")
    @Expose
    var totalAmount: String?,

    @SerializedName("amount")
    @Expose
    var amount: String?,

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
    var discountAmount: String?,
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
    var telephone:String?
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
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tid)
        parcel.writeString(jobNo)
        parcel.writeString(quotationNo)
        parcel.writeString(name)
        parcel.writeString(itemDescription)
        parcel.writeString(hrs)
        parcel.writeString(totalAmount)
        parcel.writeString(amount)
        parcel.writeString(advanceAmount)
        parcel.writeString(vat)
        parcel.writeString(specialInstruction)
        parcel.writeString(date)
        parcel.writeString(paymentMethod)
        parcel.writeString(discountAmount)
        parcel.writeString(street)
        parcel.writeString(country)
        parcel.writeString(postCode)
        parcel.writeString(telephone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimeSheetPost> {
        override fun createFromParcel(parcel: Parcel): TimeSheetPost {
            return TimeSheetPost(parcel)
        }

        override fun newArray(size: Int): Array<TimeSheetPost?> {
            return arrayOfNulls(size)
        }
    }
}