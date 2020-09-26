package com.example.jeffaccount.ui.home.worksheet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.net.URL
import java.sql.Time
import javax.net.ssl.HttpsURLConnection

class CreateWorkSheetViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()
    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    private var _companyBitmap = MutableLiveData<Bitmap>()
    val companyBitmap:LiveData<Bitmap>
        get() = _companyBitmap

    private var _purchaseList = MutableLiveData<List<PurchasePost>>()
    val purchaseList:LiveData<List<PurchasePost>>
    get() = _purchaseList

    private var _invoiceList = MutableLiveData<List<Quotation>>()
    val invoiceList:LiveData<List<Quotation>>
    get() = _invoiceList

    private var _timeSheetList = MutableLiveData<List<TimeSheetPost>>()
    val timeSheetList:LiveData<List<TimeSheetPost>>
    get() = _timeSheetList

    private var _worksheet = MutableLiveData<WorkSheet>()
    val worksheet:LiveData<WorkSheet>
    get() = _worksheet

    private var _jobNoList = MutableLiveData<ArrayList<String>>()
    val jobNoList:LiveData<ArrayList<String>>
    get() = _jobNoList

    fun addJobNoList(jobNoList:ArrayList<String>){
        Timber.e("job no list size: ${jobNoList.size}")
        _jobNoList.value = jobNoList
    }

    //Get purchase list
    fun getPurchaseList(comid: String, apikey:String):LiveData<Purchase>{
        return jeffRepository.getAllPurchase(comid,apikey)
    }

    //get invoice list
    fun getInvoiceList(comid:String, apikey: String):LiveData<Quotation>{
        return jeffRepository.getAllQuotation(comid, apikey)
    }

    //Get all time sheet
    fun getTimeSheetList(comid: String, apikey: String):LiveData<TimeSheet>{
        return jeffRepository.getAllTimeSheet(comid,apikey)
    }

    //search purchase
    fun searchPurchase(comid: String, jobNo:String, apikey: String):LiveData<Purchase>{
        return jeffRepository.searchPurchase(comid, jobNo, null, null,apikey)
    }

    //Search invoice
    fun searchQuottion(comid: String, jobNo: String, apikey: String):LiveData<Quotation>{
        return jeffRepository.searchQuotation(comid,jobNo,apikey)
    }

    //Search time sheet
    fun searchTimeSheet(comid: String, jobNo: String,apikey: String):LiveData<TimeSheet>{
        return jeffRepository.searchTimeSheet(comid,jobNo,null, null,apikey)
    }

    //Search invoice list
    fun searchInvoice(comid: String, jobNo: String, apikey: String):LiveData<InvoiceList>{
        return jeffRepository.searchInvoice(comid,jobNo, null, null,apikey)
    }
    fun clearJobNoList(){
        _jobNoList.value = null
    }

    //add purchase
    fun addPurchase(list:List<PurchasePost>){
        _purchaseList.value = list
    }

    //Add invoice
    fun addInvoice(list:List<Quotation>){
        _invoiceList.value = list
    }

    //Add time sheet
    fun addTimeSheet(list:List<TimeSheetPost>){
        _timeSheetList.value = list
    }

    //add items to worksheet
    fun addWorkSheet(workSheet: WorkSheet){
        _worksheet.value = workSheet
    }

    fun doneCreatingWorkSheet(){
        _purchaseList.value = null
        _invoiceList.value = null
        _timeSheetList.value = null
        _worksheet.value = null
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
