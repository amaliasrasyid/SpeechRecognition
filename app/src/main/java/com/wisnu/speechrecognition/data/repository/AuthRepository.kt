package com.wisnu.speechrecognition.data.repository

import com.wisnu.speechrecognition.data.request.LoginRequest
import com.wisnu.speechrecognition.data.source.AuthDataSource
import com.wisnu.speechrecognition.data.source.remote.AuthRemoteDataSource
import com.wisnu.speechrecognition.model.user.UserResponse
import com.wisnu.speechrecognition.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AuthRepository @Inject constructor(private val dataSource: AuthRemoteDataSource) : AuthDataSource {
    override fun login(request: LoginRequest): Flow<Resource<UserResponse>>  = dataSource.login(request)

    override fun register(params: HashMap<String, RequestBody>): Flow<Resource<UserResponse>> = dataSource.storeUser(null,params)

    override fun updateProfile(
        image: MultipartBody.Part,
        params: HashMap<String, RequestBody>
    ): Flow<Resource<UserResponse>> = dataSource.storeUser(image,params)

    override fun updatePassword(params: HashMap<String, Any>): Flow<Resource<UserResponse>> = dataSource.updatePassword(params)

    override fun getUserDetail(userId: Int): Flow<Resource<UserResponse>>  = dataSource.getUserDetail(userId)
}