package com.example.jeffaccount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.jeffaccount.model.Customer
import com.example.jeffaccount.model.Supplier
import com.example.jeffaccount.network.JeffApi
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class JeffRepository {

    private val apiService = JeffApi.retrofitService

    companion object {
        fun getInstance(): JeffRepository {
            return JeffRepository()
        }
    }

    //Store message after adding customer
    private var _addCustomerMessage = MutableLiveData<String>()

    //Store message after update customer
    private var _updateCustomerMessage = MutableLiveData<String>()

    //Store list of Customer
    private var _customerList = MutableLiveData<Customer>()

    //Store delete customer message
    private var _deleteCustomerMessage = MutableLiveData<String>()

    //Store add supplier message
    private var addSupplierMessage = MutableLiveData<String>()

    //Store update supplier message
    private var updateSupplierMessage = MutableLiveData<String>()

    //Store delete supplier message
    private var deleteSupplierMessage = MutableLiveData<String>()

    //Store list of supplier
    private var supplierList = MutableLiveData<Supplier>()

    //Add customer
    fun getAddCustomerMessage(
        customerName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        addCustomer(customerName, streetAdd, coutry, postCode, telephone, email, web)
        return _addCustomerMessage
    }

    //Add Supplier
    fun getAddSupplierMessage(
        supplierName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        addSupplier(supplierName, streetAdd, coutry, postCode, telephone, email, web)
        return addSupplierMessage
    }

    //Update customer
    fun getUpdateCustomerMessage(
        customerId: String,
        customerName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        updateCustomer(customerId, customerName, streetAdd, coutry, postCode, telephone, email, web)
        return _updateCustomerMessage
    }

    //Update Supplier
    fun getUpdateSupplierMessage(
        supplierId: String, supplierName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ):LiveData<String> {
        updateSupplier(
           supplierId, supplierName, streetAdd, coutry, postCode, telephone, email, web
        )
        return updateSupplierMessage
    }

    //Delete customer
    fun getDeleteCustomerMessage(customerId: String): LiveData<String> {
        deleteUser(customerId)
        return _deleteCustomerMessage
    }

    //Delete Supplier
    fun getDeleteSupplierMessage(supplierId: String):LiveData<String>{
        deleteSupplier(supplierId)
        return deleteSupplierMessage
    }

    //Get All Customer
    fun getAllCustomer(): LiveData<Customer> {
        getCustomerList()
        return _customerList
    }

    //Get All Suppliers
    fun getAllSuppliers(): LiveData<Supplier> {
        getSupplierList()
        return supplierList
    }

    //Network call to get all customer list
    private fun getCustomerList() {

        apiService.getCustomerList().enqueue(object : Callback<Customer> {
            override fun onFailure(call: Call<Customer>, t: Throwable) {
                Log.e("JeffRepository", "${t.message}")
            }

            override fun onResponse(call: Call<Customer>, response: Response<Customer>) {

                _customerList.value = response.body()
            }

        })
    }

    //Network call to get all supplier list
    private fun getSupplierList() {

        apiService.getSupplierList().enqueue(object : Callback<Supplier> {
            override fun onFailure(call: Call<Supplier>, t: Throwable) {
                Log.e("JeffRepository", "${t.message}")
            }

            override fun onResponse(call: Call<Supplier>, response: Response<Supplier>) {
                supplierList.value = response.body()
            }
        })
    }

    //Network call to add customer
    private fun addCustomer(
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
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

    //Network call to add supplier
    private fun addSupplier(
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {

        apiService.addSupplier(supplierName, streetAdd, coutry, postCode, telephone, email, web)
            .enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("JeffRepository", "${t.message}")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val jsonObject = JSONObject(response.body()!!)
                    val message = jsonObject.optString("message")
                    addSupplierMessage.value = message
                }
            })
    }

    //Network call to update customer details
    private fun updateCustomer(
        customerId: String,
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        apiService.updateCustomer(
            customerId,
            customerName,
            streetAdd,
            coutry,
            postCode,
            telephone,
            email,
            web
        ).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("JeffRepository", "${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                _updateCustomerMessage.value = message
            }
        })
    }

    //Network call to update supplier data
    private fun updateSupplier(
        supplierId: String,
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        apiService.updateSupplier(supplierId,supplierName,streetAdd,coutry,postCode,telephone,email,web).enqueue(object :Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Failed to Update supplier: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                updateSupplierMessage.value = message
            }
        })
    }


    //Network call to delete Customer
    private fun deleteUser(customerId: String) {
        apiService.deleteCustomer(customerId).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("JeffRepository", "${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                _deleteCustomerMessage.value = message
            }

        })
    }

    //Network call to delete supplier
    private fun deleteSupplier(supplierId: String) {

        apiService.deleteSupplier(supplierId).enqueue(object :Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Failed to delete supplier ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                deleteSupplierMessage.value = message
            }
        })
    }

}