package com.example.jeffaccount.ui.home.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.ComPost
import com.example.jeffaccount.model.Company
import com.example.jeffaccount.model.CompanyLogo
import com.example.jeffaccount.model.Logos
import okhttp3.RequestBody

class CompanyViewModel : ViewModel() {

    private val jeffRepository = JeffRepository.getInstance()


    //Update company
    fun updateCompany(
        apiKey: String,
        companyId: String,
        streetAdd: String,
        companyDes:String,
        country: String,
        county:String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ): LiveData<String> {
        return jeffRepository.getUpdateCompanyMessage(
            apiKey,companyId,streetAdd,companyDes,country,county,postCode,telephone,email,web
        )
    }

    //Upload company logo
    fun uploadCompanyLogo(comid:String, logo:RequestBody):LiveData<CompanyLogo>{
        return jeffRepository.uploadLogo(comid,logo)
    }

    //Upload other logos
    fun uploadImage(comid: String,logo: RequestBody):LiveData<String>{
        return jeffRepository.uploadLogoB(comid, logo)
    }

    //Get logo list
    fun getLogoList(comid: String):LiveData<Logos>{
        return jeffRepository.getLogoList(comid)
    }

    //Delete logo
    fun deletelogo(comid:String, imgId:String):LiveData<String>{
        return jeffRepository.deleteLogo(comid,imgId)
    }
}
