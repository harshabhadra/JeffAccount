package com.example.jeffaccount.network

import androidx.lifecycle.LiveData
import com.example.jeffaccount.model.Customer
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
        @Field("postcode") postCode: Int,
        @Field("telephone") telephoneNo: Int,
        @Field("customeremail") customerEmail: String,
        @Field("web") webAdd: String
    ): Call<String>

    //Get List of Customer
    @POST("customerlist.php")
    fun getCustomerList():Call<Customer>
}

object JeffApi {

    val retrofitService: AppApiService by lazy { retrofit.create(AppApiService::class.java) }
}