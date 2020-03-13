package com.example.jeffaccount.ui.home.timeSheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.TimeSheet
import com.example.jeffaccount.model.TimeSheetPost
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class TimeSheetViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

    private var _timeSheetHours = MutableLiveData<Int>()
    val timeSheetHours: LiveData<Int>
        get() = _timeSheetHours

    private var _navigateToAddTimeSheet = MutableLiveData<TimeSheetPost>()
    val navigateToAddTimeSheetFragment: LiveData<TimeSheetPost>
        get() = _navigateToAddTimeSheet

    init {
        _navigateToAddTimeSheet.value = null
    }

    //Add Time sheet
    fun addTimeSheet(
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        name: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        hrs: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ): LiveData<String> {
        return jeffRepository.getAddTimeSheetMessage(
            jobNo, quotationNo, vat, date, name, comment, itemDes
            , paymentMethod, hrs, unitAmount, advanceAmount, discountAmount, totalAmount
        )
    }

    //Update Time sheet
    fun updateTimeSheet(
        tid: Int,
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        name: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        hrs: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ): LiveData<String> {
        return jeffRepository.getUpdateTimeSheetMessage(
            tid, jobNo, quotationNo, vat, date, name, comment, itemDes
            , paymentMethod, hrs, unitAmount, advanceAmount, discountAmount, totalAmount
        )
    }

    //Get list of Time Sheet
    fun getTimeSheetList(): LiveData<TimeSheet> {
        return jeffRepository.getAllTimeSheet()
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

    //Change hours
    fun changeHours(hours: Int) {
        Timber.e(hours.toString())
        _timeSheetHours.value = hours
    }

    fun navigateWithTimeSheet(timeSheetPost: TimeSheetPost) {
        _navigateToAddTimeSheet.value = timeSheetPost
    }

    fun doneNavigatingToAddTimeSheet() {
        _navigateToAddTimeSheet.value = null
    }
}
