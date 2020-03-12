package com.example.jeffaccount.ui.home.quotation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Quotation
import com.example.jeffaccount.model.QuotationPost
import com.itextpdf.xmp.XMPDateTimeFactory.getCurrentDateTime
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

    //Current Date String
    private var _dateString = MutableLiveData<String>()
    val dateString: LiveData<String>
        get() = _dateString

    init {
        _quotationQuantityValue.value = 0
        _navigateToAddQuotationFragment.value = null
    }

    //Add Quotation
    fun addQuotaiton(
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        customerName: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        quantity: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ): LiveData<String> {
        return jeffRepository.getAddQuotationMessage(
            jobNo,
            quotationNo,
            vat,
            date,
            customerName,
            comment,
            itemDes,
            paymentMethod,
            quantity,
            unitAmount,
            advanceAmount,
            discountAmount,
            totalAmount
        )
    }

    //Update Quotation
    fun updateQuotation(
        id: Int,
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        customerName: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        quantity: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ): LiveData<String> {
        return jeffRepository.getUpdateQuotationMessage(
            id,
            jobNo,
            quotationNo,
            vat,
            date,
            customerName,
            comment,
            itemDes,
            paymentMethod,
            quantity,
            unitAmount,
            advanceAmount,
            discountAmount,
            totalAmount
        )
    }

    //Delete Quotation
    fun deleteQuotaton(id: Int): LiveData<String> {
        return jeffRepository.getDeleteQuotationMessage(id)
    }

    //Get list of quotation
    fun getQuotationList(): LiveData<Quotation> {
        return jeffRepository.getAllQuotation()
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
    fun onQuotationItemClick(quotation:QuotationPost){
        _navigateToAddQuotationFragment.value = quotation
    }

    fun doneNavigating(){
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
}
