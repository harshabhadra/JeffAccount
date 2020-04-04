package com.example.jeffaccount.ui.home.supplier

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Post
import com.example.jeffaccount.model.SupPost
import com.example.jeffaccount.model.Supplier

private lateinit var jeffRepository: JeffRepository

class AddSupplierViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    init {
        jeffRepository = JeffRepository.getInstance()
    }

    private var _navigateToAddSupplierFragment = MutableLiveData<SupPost>()
    val navigateToAddSupplierFragment: LiveData<SupPost>
        get() = _navigateToAddSupplierFragment

    private var _nameList = MutableLiveData<List<String>>()
    val nameList:LiveData<List<String>>
        get() = _nameList

    //Add Supplier
    fun addSupplier(
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ): LiveData<String> {
        return jeffRepository.getAddSupplierMessage(
            supplierName,
            streetAdd,
            coutry,
            postCode,
            telephone,
            email,
            web
        )
    }

    //Update Supplier
    fun updateSupplier(
        supplierId: String,
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ): LiveData<String> {
        return jeffRepository.getUpdateSupplierMessage(
            supplierId,
            supplierName,
            streetAdd,
            coutry,
            postCode,
            telephone,
            email,
            web
        )
    }

    //Delete Supplier
    fun deleteSupplier(
        supplierId: String
    ): LiveData<String> {
        return jeffRepository.getDeleteSupplierMessage(supplierId)
    }

    fun onSupplierItemClick(supplier: SupPost) {
        _navigateToAddSupplierFragment.value = supplier
    }

    fun doneNavigating() {
        _navigateToAddSupplierFragment.value = null
    }

    //Get list of supplier
    fun getSuppliers(): LiveData<Supplier> {
        return jeffRepository.getAllSuppliers()
    }

    //Crate CustomerName list
    fun createSupplierNameList(custList:List<SupPost>){
        val custNameList = mutableListOf<String>()
        for (item in custList){
            custNameList.add(item.supname!!)
        }
        _nameList.value = custNameList.toList()
    }
}
