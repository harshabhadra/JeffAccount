package com.example.jeffaccount.network

import androidx.lifecycle.LiveData
import com.example.jeffaccount.model.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
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
        @Field("custid") customerId: String,
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
    fun deleteCustomer(@Field("custid") customerId: String): Call<String>

    //Get List of Customer
    @POST("customerlist.php")
    fun getCustomerList(): Call<Customer>

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
        @Field("supid") customerId: String,
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
    fun deleteSupplier(@Field("supid") customerId: String): Call<String>

    //Get Supplier list
    @POST("supplierlist.php")
    fun getSupplierList(): Call<Supplier>

    //Add Quotation
    @POST("quatationadd.php")
    fun addQuotation(@Body body:QuotationAdd): Call<String>

    //Update Quotation
    @POST("quatationupdate.php")
    fun updateQuotation(@Body quotation:QuotationUpdate): Call<String>

    //Get quotation list
    @POST("quatationlist.php")
    @FormUrlEncoded
    fun getQuotationList(
        @Field("apikey")apiKey: String
    ): Call<Quotation>

    //Delete Quotation
    @POST("quatationdel.php")
    @FormUrlEncoded
    fun deleteQuotation(@Field("qid") quotationId: Int): Call<String>

    //Add Company
    @POST("companyadd.php")
    @FormUrlEncoded
    fun addCompany(
        @Field("comname") comName: String,
        @Field("street") street: String,
        @Field("country") country: String,
        @Field("postcode") postCode: String,
        @Field("telephone") telephoneNo: String,
        @Field("comemail") customerEmail: String,
        @Field("web") webAdd: String
    ): Call<String>

    //Update Company
    @POST("companyupdate.php")
    @FormUrlEncoded
    fun updateCompany(
        @Field("comid") id: Int,
        @Field("comname") comName: String,
        @Field("street") street: String,
        @Field("country") country: String,
        @Field("postcode") postCode: String,
        @Field("telephone") telephoneNo: String,
        @Field("comemail") customerEmail: String,
        @Field("web") webAdd: String
    ): Call<String>

    //Get list of company
    @POST("companylist.php")
    fun getCompanyList():Call<Company>

    //Delete company
    @POST("companydel.php")
    @FormUrlEncoded
    fun deleteCompany(@Field("comid")id:Int):Call<String>

    //Add Purchase
    @POST("purchaseadd.php")
    @FormUrlEncoded
    fun addPurchase(
        @Field("apikey")apiKey: String,
        @Field("job_no") jobNo: String,
        @Field("quotation_no") quotationNo: String,
        @Field("vat") vat: Double,
        @Field("date") date: String,
        @Field("customer_name") supplierName: String,
        @Field("street_address")streetAddress:String,
        @Field("country")country: String,
        @Field("post_code")postCode: String,
        @Field("telephone")telephoneNo: String,
        @Field("special_instruction") specialIns: String,
        @Field("item_description") itemDes: String,
        @Field("payment_method") paymentMethod: String,
        @Field("quantity") quantity: Int,
        @Field("unit_amount") unitAmount: Double,
        @Field("advance_amount") advanceAmount: Double,
        @Field("discount_amount") discountAmount: Double,
        @Field("total_amount") totoalAmount: Double
    ):Call<String>

    //Update Purchase
    @POST("purchaseupdate.php")
    @FormUrlEncoded
    fun updatePurchase(
        @Field("apikey")apiKey: String,
        @Field("pid")purchaseId:Int,
        @Field("job_no") jobNo: String,
        @Field("quotation_no") quotationNo: String,
        @Field("vat") vat: Double,
        @Field("date") date: String,
        @Field("customer_name") supplierName: String,
        @Field("street_address")streetAddress:String,
        @Field("country")country: String,
        @Field("post_code")postCode: String,
        @Field("telephone")telephoneNo: String,
        @Field("special_instruction") specialIns: String,
        @Field("item_description") itemDes: String,
        @Field("payment_method") paymentMethod: String,
        @Field("quantity") quantity: Int,
        @Field("unit_amount") unitAmount: Double,
        @Field("advance_amount") advanceAmount: Double,
        @Field("discount_amount") discountAmount: Double,
        @Field("total_amount") totoalAmount: Double
    ):Call<String>

    //Get purchase list
    @POST("purchaselist.php")
    @FormUrlEncoded
    fun getPurchaseList(
        @Field("apikey")apiKey: String
    ):Call<Purchase>

    //Delete Purchase
    @POST("purchasedel.php")
    @FormUrlEncoded
    fun deletePurchase(@Field("pid")purchaseId:Int):Call<String>

    //Add TimeSheet
    @POST("timesheetadd.php")
    @FormUrlEncoded
    fun addTimeSheet(
        @Field("apikey")apiKey: String,
        @Field("job_no") jobNo: String,
        @Field("quotation_no") quotationNo: String,
        @Field("vat") vat: Double,
        @Field("date") date: String,
        @Field("name") supplierName: String,
        @Field("street_address")streetAddress:String,
        @Field("country")country: String,
        @Field("post_code")postCode: String,
        @Field("telephone")telephoneNo: String,
        @Field("special_instruction") specialIns: String,
        @Field("item_description") itemDes: String,
        @Field("payment_method") paymentMethod: String,
        @Field("hrs") quantity: Int,
        @Field("amount") unitAmount: Double,
        @Field("advance_amount") advanceAmount: Double,
        @Field("discount_amount") discountAmount: Double,
        @Field("total_amount") totoalAmount: Double
    ):Call<String>

    //Update time sheet
    @POST("timesheetupdate.php")
    @FormUrlEncoded
    fun updateTimeSheet(
        @Field("apikey")apiKey: String,
        @Field("tid")timesheetId:Int,
        @Field("job_no") jobNo: String,
        @Field("quotation_no") quotationNo: String,
        @Field("vat") vat: Double,
        @Field("date") date: String,
        @Field("name") supplierName: String,
        @Field("street_address")streetAddress:String,
        @Field("country")country: String,
        @Field("post_code")postCode: String,
        @Field("telephone")telephoneNo: String,
        @Field("special_instruction") specialIns: String,
        @Field("item_description") itemDes: String,
        @Field("payment_method") paymentMethod: String,
        @Field("hrs") quantity: Int,
        @Field("amount") unitAmount: Double,
        @Field("advance_amount") advanceAmount: Double,
        @Field("discount_amount") discountAmount: Double,
        @Field("total_amount") totoalAmount: Double
    ):Call<String>

    //Delete time sheet
    @POST("timesheetdel.php")
    @FormUrlEncoded
    fun deleteTimeSheet(@Field("tid")timeSheetId:Int):Call<String>

    //Get list of time sheet
    @POST("timesheetlist.php")
    @FormUrlEncoded
    fun getTimeSheetList(@Field("apikey")apiKey: String):Call<TimeSheet>
}

object JeffApi {

    val retrofitService: AppApiService by lazy { retrofit.create(AppApiService::class.java) }
}