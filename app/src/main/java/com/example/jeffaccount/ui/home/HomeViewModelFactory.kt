package com.example.jeffaccount.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jeffaccount.JeffApplication
import java.lang.IllegalArgumentException

class HomeViewModelFactory (val application: JeffApplication):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(application)as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}