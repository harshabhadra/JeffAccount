package com.example.jeffaccount.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Supplier (
    @SerializedName("posts")
    @Expose
    var posts: List<SupPost>
)


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
    var country: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
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
        parcel.writeString(supid)
        parcel.writeString(supname)
        parcel.writeString(street)
        parcel.writeString(postcode)
        parcel.writeString(telephone)
        parcel.writeString(supemail)
        parcel.writeString(web)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SupPost> {
        override fun createFromParcel(parcel: Parcel): SupPost {
            return SupPost(parcel)
        }

        override fun newArray(size: Int): Array<SupPost?> {
            return arrayOfNulls(size)
        }
    }
}