package com.example.jeffaccount.network

import androidx.lifecycle.LiveData
import com.example.jeffaccount.model.Customer
import com.example.jeffaccount.model.Quotation
import com.example.jeffaccount.model.Supplier
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private const val BASE_URL = "https://alphabusinessdesigns.com/wordpress/appproject/jtapp/"

val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface AppApiService {

    //Login
    @POST("login.php")
    @FormUrlEncoded
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<String>

    //Add Customer
    @POST("customeradd.php")
    @FormUrlEncoded
    fun addCustomer(
        @Field("custname") customerName: String,
        @Field("street") streetAdd: String,
        @Field("country") country: String,
        @Field("postcode") postCode: String,
        @Field("telephone") telephoneNo: String,
        @Field("customeremail") customerEmail: String,
        @Field("web") webAdd: String
    ): Call<String>

    //Edit Customer
    @POST("customerupdate.php")
    @FormUrlEncoded
    fun updateCustomer(
        @Field("custid")customerId: String,
        @Field("custname") customerName: String,
        @Field("street") streetAdd: String,
        @Field("country") country: String,
        @Field("postcode") postCode: String,
        @Field("telephone") telephoneNo: String,
        @Field("customeremail") customerEmail: String,
        @Field("web") webAdd: String
    ): Call<String>

    //Delete customer
    @POST("customerdel.php")
    @FormUrlEncoded
    fun deleteCustomer(@Field("custid")customerId:String):Call<String>

    //Get List of Customer
    @POST("customerlist.php")
    fun getCustomerList():Call<Customer>

    //Add Supplier
    @POST("supplieradd.php")
    @FormUrlEncoded
    fun addSupplier(
        @Field("supname") customerName: String,
        @Field("street") streetAdd: String,
        @Field("country") country: String,
        @Field("postcode") postCode: String,
        @Field("telephone") telephoneNo: String,
        @Field("supemail") customerEmail: String,
        @Field("web") webAdd: String
    ): Call<String>

    //Update Supplier
    @POST("supplierupdate.php")
    @FormUrlEncoded
    fun updateSupplier(
        @Field("supid")customerId: String,
        @Field("supname") customerName: String,
        @Field("street") streetAdd: String,
        @Field("country") country: String,
        @Field("postcode") postCode: String,
        @Field("telephone") telephoneNo: String,
        @Field("supemail") customerEmail: String,
        @Field("web") webAdd: String
    ): Call<String>

    //Delete supplier
    @POST("supplierdel.php")
    @FormUrlEncoded
    fun deleteSupplier(@Field("supid")customerId:String):Call<String>

    //Get Supplier list
    @POST("supplierlist.php")
    fun getSupplierList():Call<Supplier>

    //Add Quotation
    @POST("quatationadd.php")
    @FormUrlEncoded
    fun addQuotation(
        @Field("job_no")jobNo:String,
        @Field("quotation_no")quotationNo:String,
        @Field("vat")vat:Double,
        @Field("date")date:String,
        @Field("customer_name")customerName:String,
        @Field("special_instruction")specialIns:String,
        @Field("item_description")itemDes:String,
        @Field("payment_method")paymentMethod:String,
        @Field("quantity")quantity:Int,
        @Field("unit_amount")unitAmount:Double,
        @Field("advance_amount")advanceAmount:Double,
        @Field("discount_amount")discountAmount:Double,
        @Field("total_amount")totoalAmount:Double
    ):Call<String>

    //Update Quotation
    @POST("quatationupdate.php")
    @FormUrlEncoded
    fun updateQuotation(
        @Field("qid")quotationId:Int,
        @Field("job_no")jobNo:String,
        @Field("quotation_no")quotationNo:String,
        @Field("vat")vat:Double,
        @Field("date")date:String,
        @Field("customer_name")customerName:String,
        @Field("special_instruction")specialIns:String,
        @Field("item_description")itemDes:String,
        @Field("payment_method")paymentMethod:String,
        @Field("quantity")quantity:Int,
        @Field("unit_amount")unitAmount:Double,
        @Field("advance_amount")advanceAmount:Double,
        @Field("discount_amount")discountAmount:Double,
        @Field("total_amount")totoalAmount:Double
    ):Call<String>

    //Get quotation list
    @POST("quatationlist.php")
    fun getQuotationList():Call<Quotation>

    //Delete Quotation
    @POST("quatationdel.php")
    @FormUrlEncoded
    fun deleteQuotation(@Field("qid")quotationId: Int):Call<String>
}

object JeffApi {

    val retrofitService: AppApiService by lazy { retrofit.create(AppApiService::class.java) }
}