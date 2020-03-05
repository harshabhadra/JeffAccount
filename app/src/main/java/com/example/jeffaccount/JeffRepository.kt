package com.example.jeffaccount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.jeffaccount.model.Customer
import com.example.jeffaccount.network.JeffApi
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JeffRepository {

    private val apiService = JeffApi.retrofitService

    companion object {
        fun getInstance(): JeffRepository {
            return JeffRepository()
        }
    }

    //Store message after adding customer
    private var _addCustomerMessage = MutableLiveData<String>()

    //Store list of Customer
    private var _customerList = MutableLiveData<Customer>()

    //Add customer
    fun getAddCustomerMessage(
        customerName: String, streetAdd: String, coutry: String,
        postCode: Int, telephone: Int, email: String, web: String
    ): LiveData<String> {
        addCustomer(customerName, streetAdd, coutry, postCode, telephone, email, web)
        return _addCustomerMessage
    }

    //Get All Customer
    fun getAllCustomer():LiveData<Customer>{
        getCustomerList()
        return _customerList
    }

    //Network call to get all customer list
    private fun getCustomerList() {

        apiService.getCustomerList().enqueue(object :Callback<Customer>{
            override fun onFailure(call: Call<Customer>, t: Throwable) {
                Log.e("JeffRepository","${t.message}")
            }

            override fun onResponse(call: Call<Customer>, response: Response<Customer>) {

                _customerList.value = response.body()
            }

        })
    }

    //Network call to add customer
    private fun addCustomer(
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: Int,
        telephone: Int,
        email: String,
        web: String
    ) {

        apiService.addCustomer(customerName, streetAdd, coutry, postCode, telephone, email, web)
            .enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("JeffRepository", "${t.message}")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {

                    val jsonObject = JSONObject(response.body()!!)
                    val message = jsonObject.optString("message")
                    _addCustomerMessage.value = message
                }

            })
    }
}