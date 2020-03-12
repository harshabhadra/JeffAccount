package com.example.jeffaccount.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Company (
    @SerializedName("posts")
    @Expose
    var posts: List<ComPost>?
)

data class ComPost (
    @SerializedName("comid")
    @Expose
    var comid: String?,

    @SerializedName("comname")
    @Expose
    var comname: String?,

    @SerializedName("street")
    @Expose
    var street: String?,

    @SerializedName("postcode")
    @Expose
    var postcode: String?,

    @SerializedName("telephone")
    @Expose
    var telephone: String?,

    @SerializedName("comemail")
    @Expose
    var comemail: String?,

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
        parcel.writeString(comid)
        parcel.writeString(comname)
        parcel.writeString(street)
        parcel.writeString(postcode)
        parcel.writeString(telephone)
        parcel.writeString(comemail)
        parcel.writeString(web)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ComPost> {
        override fun createFromParcel(parcel: Parcel): ComPost {
            return ComPost(parcel)
        }

        override fun newArray(size: Int): Array<ComPost?> {
            return arrayOfNulls(size)
        }
    }
}