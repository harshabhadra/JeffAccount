package com.example.jeffaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginViewModelFactory (val application: JeffApplication):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogInViewModel::class.java)){
            return LogInViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}