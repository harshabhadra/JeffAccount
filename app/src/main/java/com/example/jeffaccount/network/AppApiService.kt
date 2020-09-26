package com.example.jeffaccount.network

import com.example.jeffaccount.model.*
import com.google.gson.GsonBuilder
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val BASE_URL = ""

val gson = GsonBuilder()
    .setLenient()
    .create()
val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(BASE_URL)
    .build()

interface AppApiService {

    //Login
    @POST("login.php")
    @FormUrlEncoded
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<CompanyDetails>

    //Add Customer
    @POST("customeradd.php")
    @FormUrlEncoded
    fun addCustomer(
        @Field("comid")comid:String? = "",
        @Field("custname") customerName: String? = "",
        @Field("street") streetAdd: String? = "",
        @Field("country") country: String? = "",
        @Field("county")county: String? = "",
        @Field("postcode") postCode: String? = "",
        @Field("telephone") telephoneNo: String? = "",
        @Field("customeremail") customerEmail: String? = "",
        @Field("web") webAdd: String? = ""
    ): Call<String>

    //Edit Customer
    @POST("customerupdate.php")
    @FormUrlEncoded
    fun updateCustomer(
        @Field("comid")comid: String? = "",
        @Field("custid") customerId: String? = "",
        @Field("custname") customerName: String?= "",
        @Field("street") streetAdd: String? = "",
        @Field("country") country: String? = "",
        @Field("county")county: String? = "",
        @Field("postcode") postCode: String? = "",
        @Field("telephone") telephoneNo: String? = "",
        @Field("customeremail") customerEmail: String? = "",
        @Field("web") webAdd: String? = ""
    ): Call<String>

    //Delete customer
    @POST("customerdel.php")
    @FormUrlEncoded
    fun deleteCustomer(@Field("custid") customerId: String): Call<String>

    //Get List of Customer
    @POST("customerlist.php")
    @FormUrlEncoded
    fun getCustomerList(@Field("comid")comid: String): Call<Customer>

    //Add Supplier
    @POST("supplieradd.php")
    @FormUrlEncoded
    fun addSupplier(
        @Field("comid")comid: String,
        @Field("supname") customerName: String,
        @Field("street") streetAdd: String,
        @Field("country") country: String,
        @Field("county")county: String,
        @Field("postcode") postCode: String,
        @Field("telephone") telephoneNo: String,
        @Field("supemail") customerEmail: String,
        @Field("web") webAdd: String
    ): Call<String>

    //Update Supplier
    @POST("supplierupdate.php")
    @FormUrlEncoded
    fun updateSupplier(
        @Field("comid")comid: String,
        @Field("supid") customerId: String,
        @Field("supname") customerName: String,
        @Field("street") streetAdd: String,
        @Field("country") country: String,
        @Field("county")county: String,
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
    @FormUrlEncoded
    fun getSupplierList(@Field("comid")comid: String): Call<Supplier>

    //Add Quotation
    @POST("quatationadd.php")
    fun addQuotation(@Body body: QuotationAdd): Call<String>

    //Update Quotation
    @POST("quatationupdate.php")
    fun updateQuotation(@Body quotation: QuotationUpdate): Call<String>

    //Get quotation list
    @POST("quatationlist.php")
    @FormUrlEncoded
    fun getQuotationList(
        @Field("comid")comid: String,
        @Field("apikey") apiKey: String
    ): Call<Quotation>

    //Delete Quotation
    @POST("quatationdel.php")
    @FormUrlEncoded
    fun deleteQuotation(@Field("qid") quotationId: Int): Call<String>

    //Add invoice
    @POST("invoiceadd.php")
    fun addInvoice(@Body invoice: InvoiceAdd): Call<String>

    //Update invoice
    @POST("invoiveupdate.php")
    fun updateInvoice(@Body invoice: InvoiceUpdate): Call<String>

    //Delete invoice
    @POST("invoicedel.php")
    @FormUrlEncoded
    fun deleteInvoice(
        @Field("apikey") apiKey: String,
        @Field("inid") inid: Int
    ): Call<String>

    //Get invoice list
    @POST("invoicelist.php")
    @FormUrlEncoded
    fun getInvoiceList(
        @Field("comid")comid: String,
        @Field("apikey") apiKey: String): Call<InvoiceList>

    //Search customer in quotation
    @POST("customer_search.php")
    @FormUrlEncoded
    fun searchCustomer(
        @Field("comid")comid:String,
        @Field("custname") customerName: String,
        @Field("apikey") apiKey: String
    ): Call<SearchCustomerList>

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
        @Field("apikey")apiKey: String,
        @Field("comid") id: String,
        @Field("street") street: String,
        @Field("companydes")companyDes:String,
        @Field("country") country: String,
        @Field("county")county:String,
        @Field("postcode") postCode: String,
        @Field("telephone") telephoneNo: String,
        @Field("comemail") customerEmail: String,
        @Field("web") webAdd: String
    ): Call<String>

    //Get list of company
    @POST("companylist.php")
    fun getCompanyList(): Call<Company>

    //Delete company
    @POST("companydel.php")
    @FormUrlEncoded
    fun deleteCompany(@Field("comid") id: Int): Call<String>

    //Add Purchase
    @POST("purchaseadd.php")
    fun addPurchase(
        @Body body: PurchaseAdd
    ): Call<String>

    //Update Purchase
    @POST("purchaseupdate.php")
    fun updatePurchase(@Body purchaseUpdate: PurchaseUpdate): Call<String>

    //Get purchase list
    @POST("purchaselist.php")
    @FormUrlEncoded
    fun getPurchaseList(
        @Field("comid")comid: String,
        @Field("apikey") apiKey: String
    ): Call<Purchase>

    //Delete Purchase
    @POST("purchasedel.php")
    @FormUrlEncoded
    fun deletePurchase(@Field("pid") purchaseId: Int): Call<String>

    //Search supplier for purchase
    @POST("sup_search.php")
    @FormUrlEncoded
    fun searchSupplier(
        @Field("comid")comid: String,
        @Field("sup_name") name: String,
        @Field("apikey") apiKey: String
    ): Call<SearchSupplier>

    //Add TimeSheet
    @POST("timesheetadd.php")
    fun addTimeSheet(
     @Body timeSheetAdd:TimeSheetAdd): Call<String>

    //Update time sheet
    @POST("timesheetupdate.php")
    fun updateTimeSheet(@Body timeSheetUpdate: TimeSheetUpdate): Call<String>

    //Delete time sheet
    @POST("timesheetdel.php")
    @FormUrlEncoded
    fun deleteTimeSheet(@Field("tid") timeSheetId: Int): Call<String>

    //Get list of time sheet
    @POST("timesheetlist.php")
    @FormUrlEncoded
    fun getTimeSheetList(
        @Field("comid")comid: String,
        @Field("apikey") apiKey: String): Call<TimeSheet>

    //Search purchase by job no
    @POST("purchase_search.php")
    @FormUrlEncoded
    fun searchPurchase(
        @Field("comid")comid: String,
        @Field("job_no") jobNo: String?,
        @Field("quotation_no")quotation_no:String?,
        @Field("customer_name")customer_name:String?,
        @Field("apikey") apiKey: String
    ):Call<Purchase>

    //Search Invoice
    @POST("invoice_search.php")
    @FormUrlEncoded
    fun searchInvoice(
        @Field("comid")comid: String,
        @Field("job_no") jobNo: String?,
        @Field("quotation_no")quotation_no:String?,
        @Field("customer_name")customer_name:String?,
        @Field("apikey") apiKey: String
    ):Call<InvoiceList>

    //Search TimeSheet
    @POST("timesheet_search.php")
    @FormUrlEncoded
    fun searchTimeSheet(
        @Field("comid")comid: String,
        @Field("job_no") jobNo: String?,
        @Field("quotation_no")quotation_no:String?,
        @Field("customer_name")customer_name:String?,
        @Field("apikey") apiKey: String
    ):Call<TimeSheet>

    //Search quotation
    @POST("quataion_search.php")
    @FormUrlEncoded
    fun searchQuotation(
        @Field("comid")comid: String,
        @Field("job_no")jobNo: String,
        @Field("apikey")apiKey: String
    ):Call<Quotation>

    @POST("quataion_search.php")
    @FormUrlEncoded
    fun searchQuotationByQuta(
        @Field("comid")comid: String,
        @Field("quotation_no")jobNo: String,
        @Field("apikey")apiKey: String
    ):Call<Quotation>

    @POST("quataion_search.php")
    @FormUrlEncoded
    fun searchQuotationByName(
        @Field("comid")comid: String,
        @Field("name")jobNo: String,
        @Field("apikey")apiKey: String
    ):Call<Quotation>



    @POST("changepssword.php")
    @FormUrlEncoded
    fun changePassword(
        @Field("comid")comid: String,
        @Field("password")password: String,
        @Field("newpassword")newPassWord:String
    ):Call<String>

    //Add Company Logo
    @POST("companylogo.php")
    @Multipart
    fun uploadCompanyLogo(
        @Part("comid")comid: String,
        @Part("comimage\";filename=\"comimage.png") logoimg:RequestBody):Call<CompanyLogo>

    //Add outher logos
    @POST("imageupload.php")
    @Multipart
    fun uploadLogo(
        @Part("comid")comid: String,
        @Part("files\";filename=\"files.png") logoimg:RequestBody):Call<String>

    //Get logo list
    @POST("imagelist.php")
    @FormUrlEncoded
    fun getLogoList(
        @Field("comid")comid: String
    ):Call<Logos>

    //Delete  a logo
    @POST("imagedel.php")
    @FormUrlEncoded
    fun deleteLogo(
        @Field("comid")comid: String,
        @Field("imgid")imgId:String
    ):Call<String>

    //Get abut, contact us and faq
    @POST("pagelist.php")
    @FormUrlEncoded
    fun getDetails(
        @Field("pageid")pageid:Int
    ):Call<Detail>

    //Get Country list
    @GET("conlist.php")
    fun getCountryList():Call<List<Country>>
}

object JeffApi {

    val retrofitService: AppApiService by lazy { retrofit.create(AppApiService::class.java) }
}