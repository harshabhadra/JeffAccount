package com.example.jeffaccount.ui.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Detail

class AboutViewModel : ViewModel() {

    private val repository = JeffRepository.getInstance()

    //GEt Details
    fun getDetails(pageId: Int): LiveData<Detail> {
        return repository.getDetails(pageId)
    }
}
