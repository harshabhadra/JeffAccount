package com.example.jeffaccount.ui.home.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Customer
import com.example.jeffaccount.model.Post
import com.example.jeffaccount.network.SearchCustomer
import com.example.jeffaccount.network.SearchCustomerList

private lateinit var jeffRepository: JeffRepository

class CustomerViewModel : ViewModel() {

    init {
        jeffRepository = JeffRepository.getInstance()
    }

    private var _navigateToAddCustomerFragment = MutableLiveData<Post>()
    val navigateToAddCustomerFragment: LiveData<Post>
        get() = _navigateToAddCustomerFragment

    private var _nameList = MutableLiveData<List<String>>()
    val nameList:LiveData<List<String>>
    get() = _nameList

    private var _custList = MutableLiveData<List<Post>>()
    val custList:LiveData<List<Post>>
    get() = _custList

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

    fun onCustomerItemClick(customer: Post){
        _navigateToAddCustomerFragment.value = customer
    }

    fun doneNavigating(){
        _navigateToAddCustomerFragment.value = null
    }
    //Get Customer List
    fun getCustomerList(): LiveData<Customer> {
        return jeffRepository.getAllCustomer()
    }

    //Get search list of customer for quotation
    fun searchCustomer(name:String,apiKey: String):LiveData<SearchCustomerList>{
        return jeffRepository.searchCustomerList(apiKey, name)
    }

    //Crate CustomerName list
    fun createCustomerNameList(custList:List<Post>){
        val custNameList = mutableListOf<String>()
        for (item in custList){
            custNameList.add(item.custname!!)
        }
        _nameList.value = custNameList.toList()
    }

    fun convertSearchCustomerToCustomer(list:List<SearchCustomer>){
        var customerList:MutableList<Post> = mutableListOf()
        for (item in list!!){
            val customer = Post(item.comid,item.custname, item.street,
                item.postcode, item.telephone, item.customerEmail, item.web, item.country)
            customerList.add(customer)
        }
        _custList.value = customerList
    }

    fun doneSubmittingList(){
        _custList.value = null
    }
}
