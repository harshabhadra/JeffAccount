package com.example.jeffaccount.ui.home.quotation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.*
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.network.QuotationAdd
import com.example.jeffaccount.network.QuotationUpdate
import com.example.jeffaccount.network.SearchCustomerList
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

enum class SearchBy { JOB_NO, QUOTATION_NO, CUSTOMER_NAME }
class AddQuotationViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    private var _companyBitmap = MutableLiveData<Bitmap>()
    val companyBitmap:LiveData<Bitmap>
        get() = _companyBitmap

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

    private var _quotationNoList = MutableLiveData<List<String>>()
    val quotationNoList:LiveData<List<String>>
    get() = _quotationNoList

    private var _custNameSet = MutableLiveData<Set<String>>()
    val custNameSet:LiveData<Set<String>>
    get() = _custNameSet

    private var _searchQuotationBy = MutableLiveData<SearchBy>()
    val searchQuotationBy:LiveData<SearchBy>
    get() = _searchQuotationBy

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
    fun getQuotationList(comid: String,apiKey: String): LiveData<Quotation> {
        return jeffRepository.getAllQuotation(comid,apiKey)
    }

    //Get list of all companies
    fun getCustomerList(comid:String): LiveData<Customer> {
        return jeffRepository.getAllCustomer(comid)
    }

    //Get search list of customer for quotation
    fun searchCustomer(comid: String,name: String, apiKey: String): LiveData<SearchCustomerList> {
        return jeffRepository.searchCustomerList(comid,apiKey, name)
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

    fun createJobNoList(invoiceList: List<QuotationPost>) {
        val noSet = mutableSetOf<String>()
        for (item in invoiceList) {
            noSet.add(item.jobNo!!.toString())
        }
        _jobNoList.value = noSet
    }

    fun createQuotationNoList(quotationList:List<QuotationPost>){
        val quotationSet = mutableSetOf<String>()
        for (quotation in quotationList){
            quotationSet.add(quotation.quotationNo!!.toString())
        }
        _quotationNoList.value = quotationSet.toList()
    }

    fun createCustomerNameList(quotationList: List<QuotationPost>){
        val nameSet = mutableSetOf<String>()
        for (quotation in quotationList){
            nameSet.add(quotation.customerName!!)
        }
        _custNameSet.value = nameSet
    }

    fun calculateAmount(qty:Int,unitA: Double, dAmount: Double) {
        Timber.e("qty: $qty, amount: $unitA, discount: $dAmount")
        val totalAmount = unitA.times(qty).minus(dAmount)
        Timber.e("Total Amount: $totalAmount")
            _totalAmuont.value = totalAmount
    }

    fun setDefaultAmount(){
        _totalAmuont.value = null
    }

    //Search quotation by job no
    fun searchQuotation(comid: String, jobNo:String, apiKey: String):LiveData<Quotation>{
        return jeffRepository.searchQuotation(comid,jobNo, apiKey)
    }

    //Search quotation by quotation no
    fun searchQuotationByQuotation(comid: String, quotationNO:String, apiKey: String):LiveData<Quotation>{
        return jeffRepository.searchQuotationByQuotation(comid,quotationNO, apiKey)
    }

    //Search quotation by Customer
    fun searchQuotationByCustomer(comid: String, custName:String, apiKey: String):LiveData<Quotation>{
        return jeffRepository.searchQuotationByCustomer(comid,custName, apiKey)
    }

    fun setSearchBy(value:SearchBy){
        _searchQuotationBy.value = value
    }

    //Get logo list
    fun getLogoList(comid: String): LiveData<Logos> {
        return jeffRepository.getLogoList(comid)
    }

    fun getBitmapFromUrl(url: String) {
        uiScope.launch {
            _imageBitmap.value = convertUrlToBitmap(url)
        }
    }

    fun getCompanyBitmap(url:String){
        uiScope.launch {
            _companyBitmap.value = convertUrlToBitmap(url)
        }
    }

    private suspend fun convertUrlToBitmap(src: String?): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(src)
                val connection =
                    url.openConnection() as HttpsURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                Timber.e("converted to bitmap suceesfully")
                val option = BitmapFactory.Options()
                option.inPreferredConfig = Bitmap.Config.ARGB_8888
                BitmapFactory.decodeStream(input
                ,null, option)
            } catch (e: IOException) {
                e.printStackTrace()
                Timber.e("Failed to convert to bitmap: ${e.message}")
                null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
