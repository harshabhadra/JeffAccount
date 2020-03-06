package com.example.jeffaccount.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Customer (
    @SerializedName("posts")
    @Expose
    var posts: List<Post>
)

data class Post(
    @SerializedName("custid")
    @Expose
    var custid: String?,
    @SerializedName("custname")
    @Expose
    var custname: String?,
    @SerializedName("street")
    @Expose
    var street: String?,
    @SerializedName("postcode")
    @Expose
    var postcode: String?,
    @SerializedName("telephone")
    @Expose
    var telephone: String?,
    @SerializedName("customeremail")
    @Expose
    var customeremail: String?,
    @SerializedName("web")
    @Expose
    var web: String?,
    @SerializedName("country")
    @Expose
    var country:String?
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
        parcel.writeString(custid)
        parcel.writeString(custname)
        parcel.writeString(street)
        parcel.writeString(postcode)
        parcel.writeString(telephone)
        parcel.writeString(customeremail)
        parcel.writeString(web)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}