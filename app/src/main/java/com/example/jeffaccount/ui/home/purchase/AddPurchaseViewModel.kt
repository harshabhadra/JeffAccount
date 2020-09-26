package com.example.jeffaccount.ui.home.purchase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.*
import com.example.jeffaccount.network.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

class AddPurchaseViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    private var _companyBitmap = MutableLiveData<Bitmap>()
    val companyBitmap:LiveData<Bitmap>
        get() = _companyBitmap

    //Store quantity value
    private var _purchaseQuantityValue = MutableLiveData<Int>()
    val purchaseQuantityValue: LiveData<Int>
        get() = _purchaseQuantityValue

    private var _navigateToAddPurchaseFragment = MutableLiveData<PurchasePost>()
    val navigateToAddPurchaseFragment: LiveData<PurchasePost>
        get() = _navigateToAddPurchaseFragment

    private var _dateString = MutableLiveData<String>()
    val dateString: LiveData<String>
        get() = _dateString

    private var _jobNoList = MutableLiveData<Set<String>>()
    val jobNoList:LiveData<Set<String>>
    get() = _jobNoList

    private var _quotationList = MutableLiveData<Set<String>>()
    val quotationList:LiveData<Set<String>>
    get() = _quotationList

    private var _customerNameList = MutableLiveData<Set<String>>()
    val customerNameList:LiveData<Set<String>>
    get() = _customerNameList

    private var _itemChangedToPurchase = MutableLiveData<List<SupList>>()
    val supplierChangedToPurchase:LiveData<List<SupList>>
    get() = _itemChangedToPurchase

    private var _totalAmuont = MutableLiveData<Double>()
    val totalAmount: LiveData<Double>
        get() = _totalAmuont

    init {
        _navigateToAddPurchaseFragment.value = null
    }

    //Add purchase
    fun addPurchase(purchaseAdd: PurchaseAdd): LiveData<String> {
        return jeffRepository.getAddPurchaseMessage(purchaseAdd)
    }

    //Get List of purchase
    fun getPurchaseList(comid: String, apikey: String): LiveData<Purchase> {
        return jeffRepository.getAllPurchase(comid,apikey)
    }

    //Update purchase
    fun updatePurchase(purchaseUpdate: PurchaseUpdate): LiveData<String> {
        return jeffRepository.getUpdatePurchaseMessage(purchaseUpdate)
    }

    //Delete purchase
    fun deletePurchase(purchaseId: Int): LiveData<String> {
        return jeffRepository.getDeletePurchaseMessage(purchaseId)
    }

    //Get list of customers
    fun getCustomerList(comid:String): LiveData<Customer> {
        return jeffRepository.getAllCustomer(comid)
    }

    //function to change quantity
    fun changeQuantity(qty: Int) {
        _purchaseQuantityValue.value = qty
    }

    fun navigateToAddPurchaseFragment(purchase: PurchasePost){
        _navigateToAddPurchaseFragment.value = purchase
    }

    fun doneNavigating(){
        _navigateToAddPurchaseFragment.value = null
    }

    //Format Date
    fun changeDateFormat(day: Int, month: Int, year: Int): String {

        val calender = Calendar.getInstance()
        calender.set(year, month, day)
        val d = calender.time
        _dateString.value = d.toString("E, dd MMM yyyy")
        return d.toString("E, dd MMM yyyy")
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun changeItemToSupList(itemList: List<SupList>){
        _itemChangedToPurchase.value = itemList
    }

    //Get list of supplier
    fun getSuppliers(comid:String): LiveData<Supplier> {
        return jeffRepository.getAllSuppliers(comid)
    }


    //Get search supplier list
    fun getSearchSupplierList(comid: String,name:String, apikey: String):LiveData<SearchSupplier>{
        return jeffRepository.getSearchSupplierList(comid,name,apikey)
    }


    fun createJobNoList(purchaseList:List<PurchasePost>){
        val nolist = mutableSetOf<String>()
        val quotationSet = mutableSetOf<String>()
        val customerSet = mutableSetOf<String>()
        for (items in purchaseList){
            nolist.add(items.jobNo!!.toString())
            quotationSet.add(items.quotationNo!!.toString())
            if (items.custname != ""){
                customerSet.add(items.custname)
            }
        }
        _jobNoList.value = nolist
        _quotationList.value = quotationSet
        _customerNameList.value = customerSet
    }

    fun calculateAmount(qty: Int,unitA: Double, dAmount: Double){
            _totalAmuont.value = unitA.times(qty).minus(dAmount)
    }

    fun setDefaultAmount(){
        _totalAmuont.value = null
    }

    //search purchase
    fun searchPurchase(comid: String, jobNo:String?, quotationNO:String?, customerName:String?, apikey: String):LiveData<Purchase>{
        return jeffRepository.searchPurchase(comid,jobNo,quotationNO, customerName, apikey)
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
                BitmapFactory.decodeStream(input,null, option)
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
