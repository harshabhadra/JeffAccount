package com.example.jeffaccount.ui.home.invoice

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.*
import com.example.jeffaccount.network.InvoiceAdd
import com.example.jeffaccount.network.InvoiceUpdate
import com.example.jeffaccount.network.Item
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class InvoiceViewModel : ViewModel() {

    //Initializing Repository class
    private val jeffRepository = JeffRepository.getInstance()

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Store value of invoice to navigate to the add invoice fragment
    private var _navigateToAddInvoiceFragment = MutableLiveData<Invoice>()
    val navigateToAddInvoiceFragment: LiveData<Invoice>
        get() = _navigateToAddInvoiceFragment

    //Store added items in invoice
    private var _itemAddedToInvoice = MutableLiveData<MutableList<Item>>()
    val itemAddedToInvoice: LiveData<MutableList<Item>>
        get() = _itemAddedToInvoice

    private var _jobNoList = MutableLiveData<Set<String>>()
    val jobNoList: LiveData<Set<String>>
        get() = _jobNoList

    private var _quotationNoList = MutableLiveData<Set<String>>()
    val quotationNoList: LiveData<Set<String>>
        get() = _quotationNoList

    private var _customerNameList = MutableLiveData<Set<String>>()
    val customerNameList: LiveData<Set<String>>
        get() = _customerNameList

    private var _totalAmuont = MutableLiveData<Double>()
    val totalAmount: LiveData<Double>
        get() = _totalAmuont

    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    private var _companyBitmap = MutableLiveData<Bitmap>()
    val companyBitmap: LiveData<Bitmap>
        get() = _companyBitmap

    //Save invoice
    fun saveInvoice(invoiceAdd: InvoiceAdd): LiveData<String> {
        return jeffRepository.getAddInvoiceMessage(invoiceAdd)
    }

    //Update Invoice
    fun updateInvoice(invoiceUpdate: InvoiceUpdate): LiveData<String> {
        return jeffRepository.getUpdateInvoiceMessage(invoiceUpdate)
    }

    //Get invoice list
    fun getInvoiceList(comid: String, apiKey: String): LiveData<InvoiceList> {
        return jeffRepository.getAllInvoices(comid, apiKey)
    }

    //Get list of all companies
    fun getCustomerList(comid: String): LiveData<Customer> {
        return jeffRepository.getAllCustomer(comid)
    }

    //delete invoice
    fun deleteInvoice(apiKey: String, invoiceId: Int): LiveData<String> {
        return jeffRepository.getDeleteInvoiceMessage(apiKey, invoiceId)
    }

    fun invoiceItemClick(invoice: Invoice) {
        _navigateToAddInvoiceFragment.value = invoice
    }

    fun doneNavigating() {
        _navigateToAddInvoiceFragment.value = null
    }

    fun addItemToInvoice(itemList: MutableList<Item>?) {
        _itemAddedToInvoice.value = itemList
    }

    fun createJobNoList(invoiceList: List<Invoice>) {
        val noSet = mutableSetOf<String>()
        val quotationSet = mutableSetOf<String>()
        val customerSet = mutableSetOf<String>()

        for (item in invoiceList) {
            noSet.add(item.jobNo!!.toString())
            quotationSet.add(item.quotationNo!!.toString())
            if (item.customerName != "") {
                customerSet.add(item.customerName.toString())
            }
        }
        _jobNoList.value = noSet
        _quotationNoList.value = quotationSet
        _customerNameList.value = customerSet
    }

    fun calculateAmount(qty: Int, unitA: Double, dAmount: Double) {
        _totalAmuont.value = unitA.times(qty).minus(dAmount)
    }

    //Search invoice
    fun searchInvoice(
        comid: String,
        jobNo: String?,
        quotationNo: String?,
        customerName: String?,
        apikey: String
    ): LiveData<InvoiceList> {
        return jeffRepository.searchInvoice(comid, jobNo, quotationNo, customerName, apikey)
    }

    fun setDefaultAmount() {
        _totalAmuont.value = null
    }

    //Get list of all companies
    fun getCompanyList(): LiveData<Company> {
        return jeffRepository.getAllCompany()
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

    fun getCompanyBitmap(url: String) {
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
                BitmapFactory.decodeStream(input, null, option)
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
