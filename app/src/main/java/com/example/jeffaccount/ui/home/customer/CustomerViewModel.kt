package com.example.jeffaccount.ui.home.customer

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Customer
import com.example.jeffaccount.model.Post

private lateinit var jeffRepository: JeffRepository

class CustomerViewModel : ViewModel() {

    init {
        jeffRepository = JeffRepository.getInstance()
    }

    private var _navigateToAddCustomerFragment = MutableLiveData<Post>()
    val navigateToAddCustomerFragment: LiveData<Post>
        get() = _navigateToAddCustomerFragment

    //Add Customer
    fun addCustomer(
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ): LiveData<String> {
        return jeffRepository.getAddCustomerMessage(
            customerName,
            streetAdd,
            coutry,
            postCode,
            telephone,
            email,
            web
        )
    }

    //Update customer
    fun updateCustomer(
        customerId:String,
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ):LiveData<String>{
        return jeffRepository.getUpdateCustomerMessage(customerId, customerName, streetAdd, coutry, postCode, telephone, email, web)
    }

    //Delete User
    fun deleteUser(customerId: String):LiveData<String>{
        return jeffRepository.getDeleteCustomerMessage(customerId)
    }

    fun onCustomerItemClick(customer:Post){
        _navigateToAddCustomerFragment.value = customer
    }

    fun doneNavigating(){
        _navigateToAddCustomerFragment.value = null
    }
    //Get Customer List
    fun getCustomerList(): LiveData<Customer> {
        return jeffRepository.getAllCustomer()
    }
}
