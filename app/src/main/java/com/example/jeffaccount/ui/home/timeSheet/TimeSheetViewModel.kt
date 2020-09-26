package com.example.jeffaccount.ui.home.timeSheet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.*
import com.example.jeffaccount.network.TimeSheetAdd
import com.example.jeffaccount.network.TimeSheetUpdate
import com.example.jeffaccount.network.WorkerList
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

private var list:MutableSet<WorkerList> = mutableSetOf()
class TimeSheetViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    private var _companyBitmap = MutableLiveData<Bitmap>()
    val companyBitmap:LiveData<Bitmap>
        get() = _companyBitmap

    private var _navigateToAddTimeSheet = MutableLiveData<TimeSheetPost>()
    val navigateToAddTimeSheetFragment: LiveData<TimeSheetPost>
        get() = _navigateToAddTimeSheet

    private var _jobNoList = MutableLiveData<Set<String>>()
    val jobNoList:LiveData<Set<String>>
        get() = _jobNoList

    private var _quotationList = MutableLiveData<Set<String>>()
    val quotationList:LiveData<Set<String>>
    get() = _quotationList

    private var _customerList = MutableLiveData<Set<String>>()
    val customerList:LiveData<Set<String>>
    get() = _customerList

    private var _workerList = MutableLiveData<List<WorkerList>>()
    val workerList:LiveData<List<WorkerList>>
    get() = _workerList

    init {
        _navigateToAddTimeSheet.value = null
    }

    //Add Time sheet
    fun addTimeSheet(timeSheetAdd: TimeSheetAdd): LiveData<String> {
        return jeffRepository.getAddTimeSheetMessage(timeSheetAdd)
    }

    //Update Time sheet
    fun updateTimeSheet(timeSheetUpdate: TimeSheetUpdate): LiveData<String> {
        return jeffRepository.getUpdateTimeSheetMessage(timeSheetUpdate)
    }

    //Get list of Time Sheet
    fun getTimeSheetList(comid:String,apikey: String): LiveData<TimeSheet> {
        return jeffRepository.getAllTimeSheet(comid,apikey)
    }

    //Get list of customers
    fun getCustomerList(comid:String): LiveData<Customer> {
        return jeffRepository.getAllCustomer(comid)
    }

    //Delete Time Sheet
    fun deleteTimeSheet(tid: Int): LiveData<String> {
        return jeffRepository.getDeleteTimeSheetMessage(tid)
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    //Format Date
    fun changeDateFormat(day: Int, month: Int, year: Int): String {
        val calender = Calendar.getInstance()
        calender.set(year, month, day)
        val d = calender.time
        return d.toString("E, dd MMM yyyy")
    }


    fun navigateWithTimeSheet(timeSheetPost: TimeSheetPost) {
        _navigateToAddTimeSheet.value = timeSheetPost
    }

    fun doneNavigatingToAddTimeSheet() {
        _navigateToAddTimeSheet.value = null
    }

    fun createJobNoList(invoiceList:List<TimeSheetPost>){
        val noSet = mutableSetOf<String>()
        val quotationSet = mutableSetOf<String>()
        val nameSet = mutableSetOf<String>()

        for (item in invoiceList){
            noSet.add(item.jobNo!!.toString())
            quotationSet.add(item.quotationNo)
            if (item.custname != "") {
                nameSet.add(item.custname)
            }
        }
        _jobNoList.value = noSet
        _quotationList.value = quotationSet
        _customerList.value = nameSet
    }

    //Search time sheet
    fun searchTimeSheet(comid: String, jobNo: String?,
                        quotationNo: String?,
                        custName: String?,apikey: String):LiveData<TimeSheet>{
        return jeffRepository.searchTimeSheet(comid, jobNo, quotationNo, custName,apikey)
    }

    fun modifyWorkerList(worker:WorkerList){
        list.add(worker)
        _workerList.value = list.toList()
    }

    fun removeWorker(worker: WorkerList){
        list.remove(worker)
        _workerList.value = list.toList()
    }

    fun clearWorkerList(){
        _workerList.value = null
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
