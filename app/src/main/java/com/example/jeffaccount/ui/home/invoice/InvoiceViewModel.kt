package com.example.jeffaccount.ui.home.invoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Customer
import com.example.jeffaccount.model.Invoice
import com.example.jeffaccount.model.InvoiceList
import com.example.jeffaccount.network.InvoiceUpdate
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.network.QuotationAdd

class InvoiceViewModel : ViewModel() {

    //Initializing Repository class
    private val jeffRepository = JeffRepository.getInstance()

    //Store value of invoice to navigate to the add invoice fragment
    private var _navigateToAddInvoiceFragment = MutableLiveData<Invoice>()
    val navigateToAddInvoiceFragment:LiveData<Invoice>
    get() = _navigateToAddInvoiceFragment

    //Store added items in invoice
    private var _itemAddedToInvoice = MutableLiveData<MutableList<Item>>()
    val itemAddedToInvoice:LiveData<MutableList<Item>>
    get() = _itemAddedToInvoice

    private var _jobNoList = MutableLiveData<Set<String>>()
    val jobNoList:LiveData<Set<String>>
    get() = _jobNoList

    private var _totalAmuont = MutableLiveData<Double>()
    val totalAmount: LiveData<Double>
        get() = _totalAmuont

    //Save invoice
    fun saveInvoice(invoiceAdd:QuotationAdd):LiveData<String>{
        return jeffRepository.getAddInvoiceMessage(invoiceAdd)
    }

    //Update Invoice
    fun updateInvoice(invoiceUpdate: InvoiceUpdate):LiveData<String>{
        return jeffRepository.getUpdateInvoiceMessage(invoiceUpdate)
    }

    //Get invoice list
    fun getInvoiceList(apiKey:String):LiveData<InvoiceList>{
        return jeffRepository.getAllInvoices(apiKey)
    }

    //Get list of all companies
    fun getCustomerList(): LiveData<Customer> {
        return jeffRepository.getAllCustomer()
    }

    //delete invoice
    fun deleteInvoice(apiKey: String, invoiceId:Int):LiveData<String>{
        return jeffRepository.getDeleteInvoiceMessage(apiKey, invoiceId)
    }

    fun invoiceItemClick(invoice: Invoice){
        _navigateToAddInvoiceFragment.value = invoice
    }

    fun doneNavigating(){
        _navigateToAddInvoiceFragment.value = null
    }

    fun addItemToInvoice(itemList:MutableList<Item>){
        _itemAddedToInvoice.value = itemList
    }

    fun createJobNoList(invoiceList:List<Invoice>){
        val noSet = mutableSetOf<String>()
        for (item in invoiceList){
            noSet.add(item.jobNo!!.toString())
        }
        _jobNoList.value = noSet
    }

    fun calculateAmount(qty: Int,unitA: Double, dAmount: Double) {
        if (qty>0 && unitA.times(qty) > dAmount) {
            _totalAmuont.value = unitA.times(qty).minus(dAmount)
        }
    }

    fun setDefaultAmount(){
        _totalAmuont.value = null
    }
}
