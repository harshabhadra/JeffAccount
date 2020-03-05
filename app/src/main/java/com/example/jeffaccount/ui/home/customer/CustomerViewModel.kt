package com.example.jeffaccount.ui.home.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Customer

private lateinit var jeffRepository: JeffRepository
class CustomerViewModel : ViewModel() {

    init {
        jeffRepository = JeffRepository.getInstance()
    }

    //Add Customer
    fun addCustomer( customerName: String,
                     streetAdd: String,
                     coutry: String,
                     postCode: Int,
                     telephone: Int,
                     email: String,
                     web: String):LiveData<String>{
        return jeffRepository.getAddCustomerMessage(customerName, streetAdd, coutry, postCode, telephone, email, web)
    }

    //Get Customer List
    fun getCustomerList():LiveData<Customer>{
        return jeffRepository.getAllCustomer()
    }
}
