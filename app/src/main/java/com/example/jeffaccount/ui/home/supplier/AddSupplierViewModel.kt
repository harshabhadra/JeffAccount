package com.example.jeffaccount.ui.home.supplier

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Post

private lateinit var jeffRepository: JeffRepository

class AddSupplierViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    init {
        jeffRepository = JeffRepository.getInstance()
    }

    private var _navigateToAddCustomerFragment = MutableLiveData<Post>()
    val navigateToAddCustomerFragment: LiveData<Post>
        get() = _navigateToAddCustomerFragment

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
}
