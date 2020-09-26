package com.example.jeffaccount

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.dataBase.LogInCred
import com.example.jeffaccount.model.CompanyDetails
import com.example.jeffaccount.network.AppApiService
import com.example.jeffaccount.network.JeffApi
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class LogInViewModel(application: JeffApplication) : AndroidViewModel(application) {

    private var _loginMessage = MutableLiveData<CompanyDetails>()
    val loginMessage: LiveData<CompanyDetails>
        get() = _loginMessage

    private val viewModelJob = Job()
    private val appApiService = JeffApi.retrofitService

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var jeffRepository: JeffRepository = JeffRepository(application)

    //Store log in credentials from database
    val userList: LiveData<List<LogInCred>>

    private var _loginCredInserted = MutableLiveData<Boolean>()
    val loginCredInserted: LiveData<Boolean>
        get() = _loginCredInserted

    init {
        userList = jeffRepository.getLogInCred()
        _loginCredInserted.value = false
    }

    //Log in User
    fun loginUser(username: String, password: String) {

        uiScope.launch {

            appApiService.loginUser(username, password).enqueue(object : Callback<CompanyDetails> {
                override fun onFailure(call: Call<CompanyDetails>, t: Throwable) {
                    Timber.e("Failed to log in : ${t.message}")
                }

                override fun onResponse(call: Call<CompanyDetails>, response: Response<CompanyDetails>) {

                    _loginMessage.value = response.body()
                }

            })
        }
    }

    //Insert Login Cred
    fun insertLoginCred(logInCred: LogInCred) {
        uiScope.launch {
            jeffRepository.insertLogInCred(logInCred)
            _loginCredInserted.value = true
        }
    }

    fun loginCompleted(){
        _loginMessage.value = null
    }

    fun deleteLogInCred(logInCred: LogInCred) {
        uiScope.launch {
            jeffRepository.deleteLogInCred(logInCred)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
