package com.example.jeffaccount.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Country

class CountryBottomSheetViewModel : ViewModel() {

    private val repository = JeffRepository.getInstance()

    //Get Country list
    fun getCountryList(): LiveData<List<Country>> {
        return repository.getcountries()
    }
}
