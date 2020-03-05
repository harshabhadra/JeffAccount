package com.example.jeffaccount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

class LogInViewModel : ViewModel() {

    private var _loginMessage = MutableLiveData<String>()
    val loginMessage: LiveData<String>
        get() = _loginMessage

    private val viewModelJob = Job()
    private val appApiService = JeffApi.retrofitService

    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun loginUser(username:String, password:String){

        uiScope.launch {

            appApiService.loginUser(username, password).enqueue(object :Callback<String>{
                override fun onFailure(call: Call<String>, t: Throwable) {
                    _loginMessage.value = "Failure response: ${t.message}"
                    Log.e("LoginViewModel","Failure response: ${t.message}")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {

                    val jsonObject:JSONObject = JSONObject(response.body()!!)
                    val message = jsonObject.optString("message")
                    _loginMessage.value = message
                }

            })
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
