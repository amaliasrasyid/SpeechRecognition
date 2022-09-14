package com.wisnu.speechrecognition.view.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.model.user.UserResponse
import com.wisnu.speechrecognition.network.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel: ViewModel() {
    private var _auth = MutableLiveData<UserResponse>()
    private val TAG = AuthViewModel::class.java.simpleName
    private val RESPONSE_CLASS = UserResponse::class.java

    fun login(params: HashMap<String,Any>): LiveData<UserResponse>{
        _auth = getLogin(params)
        return _auth
    }

    fun updateProfile(image: MultipartBody.Part, params: HashMap<String, RequestBody>): LiveData<UserResponse>{
        storeUser(image,params)
        return _auth
    }

    fun register(params: HashMap<String, RequestBody>): LiveData<UserResponse>{
        storeUser(params = params)
        return _auth
    }

    fun userDetail (userId: Int): LiveData<UserResponse>{
        _auth = getUserDetail(userId)
        return _auth
    }

    fun updatePassword(params: HashMap<String, Any>): LiveData<UserResponse>{
        updateUserPassword(params)
        return _auth
    }

    private fun updateUserPassword(params: HashMap<String, Any>) {
        val client = ApiConfig.getApiService().updateUserPassword(params)
        val gson = Gson()
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _auth.postValue(result!!)
                    Log.d(TAG,result.toString())
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    val msg = response.message()
                    Log.e(TAG, "onFailure: $errResult")
                    Log.e(TAG, "onFailure: $msg")
                    _auth.postValue(errResult)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getUserDetail(userId: Int): MutableLiveData<UserResponse> {
        val client = ApiConfig.getApiService().detailUser(userId)
        val gson = Gson()
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _auth.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    val msg = response.message()
                    Log.e(TAG, "onFailure: $errResult")
                    Log.e(TAG, "onFailure: $msg")
                    _auth.postValue(errResult)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _auth
    }

    private fun storeUser(image: MultipartBody.Part? = null, params: HashMap<String, RequestBody>) {
        val client = ApiConfig.getApiService().storeUser(image,params)
        val gson = Gson()
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _auth.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    val msg = response.message()
                    Log.e(TAG, "onFailure: $errResult")
                    Log.e(TAG, "onFailure: $msg")
                    _auth.postValue(errResult)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getLogin(params: HashMap<String, Any>): MutableLiveData<UserResponse> {
        val client = ApiConfig.getApiService().login(params)
        val gson = Gson()
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _auth.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    val msg = response.message()
                    Log.e(TAG, "onFailure: $errResult")
                    Log.e(TAG, "onFailure: $msg")
                    _auth.postValue(errResult)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _auth;
    }


}