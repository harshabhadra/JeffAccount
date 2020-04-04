package com.example.jeffaccount.ui.home.quotation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Customer
import com.example.jeffaccount.model.Post
import com.example.jeffaccount.model.Quotation
import com.example.jeffaccount.model.QuotationPost
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.network.QuotationAdd
import com.example.jeffaccount.network.QuotationUpdate
import com.example.jeffaccount.network.SearchCustomerList
import java.text.SimpleDateFormat
import java.util.*

class AddQuotationViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

    private var _navigateToAddQuotationFragment = MutableLiveData<QuotationPost>()
    val navigateToAddQuotationFragment: LiveData<QuotationPost>
        get() = _navigateToAddQuotationFragment

    private var _quotationQuantityValue = MutableLiveData<Int>()
    val quotationQuantityValue: LiveData<Int>
        get() = _quotationQuantityValue

    private var _totalAmuont = MutableLiveData<Double>()
    val totalAmount: LiveData<Double>
        get() = _totalAmuont

    //Current Date String
    private var _dateString = MutableLiveData<String>()
    val dateString: LiveData<String>
        get() = _dateString

    private var _itemChangedToQuotation = MutableLiveData<MutableList<Item>>()
    val itemChangedToQuotation: LiveData<MutableList<Item>>
        get() = _itemChangedToQuotation

    private var _customerData = MutableLiveData<Post>()
    val customerData: LiveData<Post>
        get() = _customerData

    private var _jobNoList = MutableLiveData<Set<String>>()
    val jobNoList: LiveData<Set<String>>
        get() = _jobNoList

    init {
        _quotationQuantityValue.value = 0
        _navigateToAddQuotationFragment.value = null
    }

    //Add Quotation
    fun addQuotaiton(quotationAdd: QuotationAdd): LiveData<String> {
        return jeffRepository.getAddQuotationMessage(quotationAdd)
    }

    //Update Quotation
    fun updateQuotation(quotationUpdate: QuotationUpdate): LiveData<String> {
        return jeffRepository.getUpdateQuotationMessage(quotationUpdate)
    }

    //Delete Quotation
    fun deleteQuotaton(id: Int): LiveData<String> {
        return jeffRepository.getDeleteQuotationMessage(id)
    }

    //Get list of quotation
    fun getQuotationList(apiKey: String): LiveData<Quotation> {
        return jeffRepository.getAllQuotation(apiKey)
    }

    //Get list of all companies
    fun getCustomerList(): LiveData<Customer> {
        return jeffRepository.getAllCustomer()
    }

    //Get search list of customer for quotation
    fun searchCustomer(name: String, apiKey: String): LiveData<SearchCustomerList> {
        return jeffRepository.searchCustomerList(apiKey, name)
    }

    //Add quantity
    fun addQuantity(quantity: Int) {
        _quotationQuantityValue.value = quantity
    }

    //Subtract Quantity
    fun removeQuantity(quantity: Int) {
        _quotationQuantityValue.value = quantity
    }

    //On Quotation item click
    fun onQuotationItemClick(quotation: QuotationPost) {
        _navigateToAddQuotationFragment.value = quotation
    }

    fun doneNavigating() {
        _navigateToAddQuotationFragment.value = null
    }

    //Get current Date
    fun getDate() {
        val date = getCurrentDateTime()
        _dateString.value = date.toString("E, dd MMM yyyy")
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    //Get Current Time
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    //Format Date
    fun changeDateFormat(day: Int, month: Int, year: Int): String {

        val calender = Calendar.getInstance()
        calender.set(year, month, day)
        val d = calender.time
        _dateString.value = d.toString("E, dd MMM yyyy")
        return d.toString("E, dd MMM yyyy")
    }

    fun addItemToQuotation(itemList: MutableList<Item>) {
        _itemChangedToQuotation.value = itemList
    }

    fun removeItem(itemList: MutableList<Item>) {
        _itemChangedToQuotation.value = itemList
    }

    fun setCustomerData(customer: Post) {
        _customerData.value = customer
    }

    fun doneSetCustomerData() {
        _customerData.value = null
    }

    fun doneAddingItem() {
        _itemChangedToQuotation.value = null
    }

    fun createJobNoList(invoiceList: List<QuotationPost>) {
        val noSet = mutableSetOf<String>()
        for (item in invoiceList) {
            noSet.add(item.jobNo!!.toString())
        }
        _jobNoList.value = noSet
    }

    fun calculateAmount(qty:Int,unitA: Double, dAmount: Double) {
        if (qty>0 && unitA.times(qty) > dAmount) {
            _totalAmuont.value = unitA.times(qty).minus(dAmount)
        }
    }

    fun setDefaultAmount(){
        _totalAmuont.value = null
    }
}
