package com.example.jeffaccount.ui.home.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.ComPost
import com.example.jeffaccount.model.Company

class CompanyViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()

    private var _navigateToAddComFragment = MutableLiveData<ComPost>()
    val navigateToAddFragment: LiveData<ComPost>
        get() = _navigateToAddComFragment

    init {
        _navigateToAddComFragment.value = null
    }
    //Add Company
    fun addCompany(
        companyName: String,
        streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        return jeffRepository.getAddCompanyMessage(
            companyName,
            streetAdd,
            coutry,
            postCode,
            telephone,
            email,
            web
        )
    }

    //Get list of all companies
    fun getCompanyList(): LiveData<Company> {
        return jeffRepository.getAllCompany()
    }

    //Update company
    fun updateCompany(
        comId: Int, companyName: String,
        streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        return jeffRepository.getUpdateCompanyMessage(
            comId,
            companyName,
            streetAdd,
            coutry,
            postCode,
            telephone,
            email,
            web
        )
    }

    //Delete company
    fun deleteCompany(companyId: Int): LiveData<String> {
        return jeffRepository.getDeleteCompanyMessage(companyId)
    }

    fun navigateToAddCompany(company: ComPost){
        _navigateToAddComFragment.value = company
    }

    fun doneNavigating(){
        _navigateToAddComFragment.value = null
    }
}
