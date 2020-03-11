package com.example.jeffaccount.ui.home.purchase

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Purchase

class AddPurchaseViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

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
    ):LiveData<String>{
        return jeffRepository.getAddPurchaseMessage( jobNo, quotationNo, vat, date, supplierName, comment, itemDes
            , paymentMethod, quantity, unitAmount, advanceAmount, discountAmount, totalAmount)
    }

    //Get List of purchase
    fun getPurchaseList():LiveData<Purchase>{
        return jeffRepository.getAllPurchase()
    }

    //Update purchase
    fun updatePurchase(
        purchaseId:Int,
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
    ):LiveData<String>{
        return jeffRepository.getUpdatePurchaseMessage(purchaseId,jobNo, quotationNo, vat, date, supplierName, comment, itemDes
            , paymentMethod, quantity, unitAmount, advanceAmount, discountAmount, totalAmount)
    }

    //Delete purchase
    fun deletePurchase(purchaseId:Int):LiveData<String>{
        return jeffRepository.getDeletePurchaseMessage(purchaseId)
    }
}
