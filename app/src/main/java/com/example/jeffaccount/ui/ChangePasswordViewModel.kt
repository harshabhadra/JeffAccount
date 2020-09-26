package com.example.jeffaccount.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Country

class ChangePasswordViewModel : ViewModel() {

    private val repository = JeffRepository.getInstance()

    //Change password
    fun changePassword(comid:String, pass:String, newPass:String):LiveData<String>{
        return repository.changePassword(comid,pass,newPass)
    }
}
