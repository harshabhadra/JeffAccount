package com.example.jeffaccount.ui.home.quotation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Quotation
import com.example.jeffaccount.model.QuotationPost

class AddQuotationViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

    private var _navigateToAddQuotationFragment = MutableLiveData<QuotationPost>()
    val navigateToAddQuotationFragment: LiveData<QuotationPost>
        get() = _navigateToAddQuotationFragment

    private var _quotationQuantityValue = MutableLiveData<Int>()
    val quotationQuantityValue: LiveData<Int>
        get() = _quotationQuantityValue

    init {
        _quotationQuantityValue.value = 0
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

    //SusTrace Quantity
    fun removeQuantity(quantity: Int) {
        _quotationQuantityValue.value = quantity
    }
}
