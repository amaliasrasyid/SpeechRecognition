package com.wisnu.speechrecognition.data.source

import com.wisnu.speechrecognition.data.request.LoginRequest
import com.wisnu.speechrecognition.model.user.UserResponse
import com.wisnu.speechrecognition.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthDataSource {
    fun login(request: LoginRequest): Flow<Resource<UserResponse>>
    fun register(params: HashMap<String, RequestBody>): Flow<Resource<UserResponse>>
    fun updateProfile(image: MultipartBody.Part, params: HashMap<String, RequestBody>): Flow<Resource<UserResponse>>
    fun updatePassword(params: HashMap<String, Any>): Flow<Resource<UserResponse>>
    fun getUserDetail(userId: Int): Flow<Resource<UserResponse>>
}