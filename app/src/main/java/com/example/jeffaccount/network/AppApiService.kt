package com.example.jeffaccount.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private const val BASE_URL = "https://alphabusinessdesigns.com/wordpress/appproject/jtapp/"

val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface AppApiService{

    @POST("login.php")
    @FormUrlEncoded
    fun loginUser(@Field("username")username:String,
                  @Field("password")password:String):Call<String>
}

object JeffApi{

    val retrofitService : AppApiService by lazy { retrofit.create(AppApiService::class.java) }
}