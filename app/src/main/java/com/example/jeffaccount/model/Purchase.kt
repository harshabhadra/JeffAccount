package com.example.jeffaccount.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class Purchase(
    @SerializedName("posts")
    @Expose
    var posts: List<Post>?
)

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
)