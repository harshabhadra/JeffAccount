package com.example.jeffaccount.ui.home.timeSheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddWorkerViewModel : ViewModel() {

    private var _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double>
        get() = _totalAmount

    private var _hours = MutableLiveData<Int>()
    val hours:LiveData<Int>
    get() = _hours

    fun calculateTotalAmount(hrs: Int, amount: Double, advanceAmount: Double, vat: Double) {
        var workerAmount = amount.times(hrs)
        workerAmount = workerAmount.minus(advanceAmount)
        workerAmount += vat.div(100).times(workerAmount)
        _totalAmount.value = workerAmount
    }

    fun setHour(hr:Int){
        _hours.value = hr
    }
}