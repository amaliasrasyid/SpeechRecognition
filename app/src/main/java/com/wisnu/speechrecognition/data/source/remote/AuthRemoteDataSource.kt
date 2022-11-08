package com.wisnu.speechrecognition.data.source.remote

import com.google.gson.Gson
import com.wisnu.speechrecognition.data.request.LoginRequest
import com.wisnu.speechrecognition.model.user.UserResponse
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRemoteDataSource {
    fun login(request: LoginRequest): Flow<Resource<UserResponse>> = flow {
        try {
            emit(Resource.loading())
            val response = ApiConfig.getApiService().login(request)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()
                if(result?.code == 200){//TODO karna apinya meski code bukan 200 tp dianggap sukses oleh okhttp
                    emit(Resource.success(result))
                }else{
                    emit(Resource.error(result?.message, null))
                }
            } else if (response.errorBody() != null) {
                val errResult =
                    Gson().fromJson(response.errorBody()?.charStream(), UserResponse::class.java)
                emit(Resource.error(errResult.message, null))
            } else {
                emit(Resource.error(response.message(), null))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message, null))
        }
    }

    fun storeUser(
        image: MultipartBody.Part? = null,
        params: HashMap<String, RequestBody>
    ): Flow<Resource<UserResponse>> = flow {
        try {
            emit(Resource.loading())
            val response = ApiConfig.getApiService().storeUser(image, params)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()
                if(result?.code == 200){//TODO karna apinya meski code bukan 200 tp dianggap sukses oleh okhttp
                    emit(Resource.success(result))
                }else{
                    emit(Resource.error(result?.message, null))
                }
            } else if (response.errorBody() != null) {
                val errResult = Gson().fromJson(response.errorBody()?.charStream(), UserResponse::class.java)
                emit(Resource.error(errResult.message, null))
            } else {
                emit(Resource.error(response.message(), null))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message, null))
        }
    }

    fun updatePassword(params: HashMap<String, Any>): Flow<Resource<UserResponse>> = flow {
        try{
            emit(Resource.loading())
            val response = ApiConfig.getApiService().updateUserPassword(params)
            if(response.isSuccessful && response.body() != null){
                val result = response.body()
                if(result?.code == 200){//TODO karna apinya meski code bukan 200 tp dianggap sukses oleh okhttp
                    emit(Resource.success(result))
                }else{
                    emit(Resource.error(result?.message, null))
                }
            }else if(response.errorBody() != null){
                val errResult = Gson().fromJson(response.errorBody()?.charStream(),UserResponse::class.java)
                emit(Resource.error(errResult.message,null))
            }else {
                emit(Resource.error(response.message(), null))
            }
        }catch (e: Exception){
            emit(Resource.error(e.message,null))
        }
    }

    fun getUserDetail(userId: Int): Flow<Resource<UserResponse>> = flow {
        try{
            emit(Resource.loading())
            val response = ApiConfig.getApiService().detailUser(userId)
            if(response.isSuccessful && response.body() != null){
                val result = response.body()
                if(result?.code == 200){//TODO karna apinya meski code bukan 200 tp dianggap sukses oleh okhttp
                    emit(Resource.success(result))
                }else{
                    emit(Resource.error(result?.message, null))
                }
            }else if(response.errorBody() != null){
                val errResult = Gson().fromJson(response.errorBody()?.charStream(),UserResponse::class.java)
                emit(Resource.error(errResult.message,null))
            }else{
                emit(Resource.error(response.message(),null))
            }
        }catch (e: Exception){
            emit(Resource.error(e.message,null))
        }
    }
}