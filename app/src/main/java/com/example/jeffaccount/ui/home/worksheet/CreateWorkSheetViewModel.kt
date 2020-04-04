package com.example.jeffaccount.ui.home.worksheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.*
import java.sql.Time

class CreateWorkSheetViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

    private var _purchaseList = MutableLiveData<List<PurchasePost>>()
    val purchaseList:LiveData<List<PurchasePost>>
    get() = _purchaseList

    private var _invoiceList = MutableLiveData<List<Invoice>>()
    val invoiceList:LiveData<List<Invoice>>
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
        _jobNoList.value = jobNoList
    }

    //Get purchase list
    fun getPurchaseList(apikey:String):LiveData<Purchase>{
        return jeffRepository.getAllPurchase(apikey)
    }

    //get invoice list
    fun getInvoiceList(apikey: String):LiveData<InvoiceList>{
        return jeffRepository.getAllInvoices(apikey)
    }

    //Get all time sheet
    fun getTimeSheetList(apikey: String):LiveData<TimeSheet>{
        return jeffRepository.getAllTimeSheet(apikey)
    }

    //search purchase
    fun searchPurchase(jobNo:String, apikey: String):LiveData<Purchase>{
        return jeffRepository.searchPurchase(jobNo,apikey)
    }

    //Search invoice
    fun searchInvoice(jobNo: String, apikey: String):LiveData<InvoiceList>{
        return jeffRepository.searchInvoice(jobNo,apikey)
    }

    //Search time sheet
    fun searchTimeSheet(jobNo: String,apikey: String):LiveData<TimeSheet>{
        return jeffRepository.searchTimeSheet(jobNo,apikey)
    }

    //add purchase
    fun addPurchase(list:List<PurchasePost>){
        _purchaseList.value = list
    }

    //Add invoice
    fun addInvoice(list:List<Invoice>){
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
}
