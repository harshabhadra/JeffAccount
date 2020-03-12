package com.example.jeffaccount.ui.home.purchase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Purchase
import com.example.jeffaccount.model.PurchasePost
import java.text.SimpleDateFormat
import java.util.*

class AddPurchaseViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

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

    init {
        _navigateToAddPurchaseFragment.value = null
    }

    //Add purchase
    fun addPurchase(
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        supplierName: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        quantity: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ): LiveData<String> {
        return jeffRepository.getAddPurchaseMessage(
            jobNo, quotationNo, vat, date, supplierName, comment, itemDes
            , paymentMethod, quantity, unitAmount, advanceAmount, discountAmount, totalAmount
        )
    }

    //Get List of purchase
    fun getPurchaseList(): LiveData<Purchase> {
        return jeffRepository.getAllPurchase()
    }

    //Update purchase
    fun updatePurchase(
        purchaseId: Int,
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        supplierName: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        quantity: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ): LiveData<String> {
        return jeffRepository.getUpdatePurchaseMessage(
            purchaseId, jobNo, quotationNo, vat, date, supplierName, comment, itemDes
            , paymentMethod, quantity, unitAmount, advanceAmount, discountAmount, totalAmount
        )
    }

    //Delete purchase
    fun deletePurchase(purchaseId: Int): LiveData<String> {
        return jeffRepository.getDeletePurchaseMessage(purchaseId)
    }

    //function to change quantity
    fun changeQuantity(qty: Int) {
        _purchaseQuantityValue.value = qty
    }

    fun navigateToAddPurchaseFragment(purchase:PurchasePost){
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
}
