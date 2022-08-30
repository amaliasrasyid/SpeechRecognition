package com.wisnu.speechrecognition.view.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieCompositionFactory.fromJson
import com.google.gson.Gson
import com.wisnu.speechrecognition.model.login.LoginResponse
import com.wisnu.speechrecognition.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel: ViewModel() {
    private var _login = MutableLiveData<LoginResponse>()
    private val TAG = AuthViewModel::class.java.simpleName
    private val RESPONSE_CLASS = LoginResponse::class.java

    fun login(params: HashMap<String,Any>): LiveData<LoginResponse>{
        _login = getLogin(params)
        return _login
    }

    fun register(){
        //userResponse
    }

    private fun getLogin(params: HashMap<String, Any>): MutableLiveData<LoginResponse> {
        val client = ApiConfig.getApiService().login(params)
        val gson = Gson()
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _login.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    val msg = response.message()
                    Log.e(TAG, "onFailure: $errResult")
                    Log.e(TAG, "onFailure: $msg")
                    _login.postValue(errResult)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _login;
    }


}