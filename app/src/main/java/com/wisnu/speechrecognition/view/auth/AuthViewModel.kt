package com.wisnu.speechrecognition.view.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.wisnu.speechrecognition.data.repository.AuthRepository
import com.wisnu.speechrecognition.data.request.LoginRequest
import com.wisnu.speechrecognition.model.user.UserResponse
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {
    private var _auth = MutableLiveData<UserResponse>()
    private val TAG = AuthViewModel::class.java.simpleName
    private val RESPONSE_CLASS = UserResponse::class.java

    fun login(request: LoginRequest): LiveData<Resource<UserResponse>> = repository.login(request).asLiveData()

    fun updateProfile(image: MultipartBody.Part, params: HashMap<String, RequestBody>): LiveData<Resource<UserResponse>> = repository.updateProfile(image,params).asLiveData()

    fun register(params: HashMap<String, RequestBody>): LiveData<Resource<UserResponse>> = repository.register(params).asLiveData()

    fun userDetail (userId: Int): LiveData<Resource<UserResponse>> = repository.getUserDetail(userId).asLiveData()

    fun updatePassword(params: HashMap<String, Any>): LiveData<Resource<UserResponse>> = repository.updatePassword(params).asLiveData()





}