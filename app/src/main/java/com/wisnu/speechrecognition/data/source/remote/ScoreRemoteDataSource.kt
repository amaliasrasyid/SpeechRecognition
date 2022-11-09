package com.wisnu.speechrecognition.data.source.remote

import com.google.gson.Gson
import com.wisnu.speechrecognition.data.model.student.StudentScoreResponse
import com.wisnu.speechrecognition.data.model.student.StudentScoresResponse
import com.wisnu.speechrecognition.data.source.ScoreDataSource
import com.wisnu.speechrecognition.model.user.UserResponse
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScoreRemoteDataSource {

    fun getStudentScores(materyType: Int, studentId: Int): Flow<Resource<StudentScoresResponse>> = flow {
        try {
            emit(Resource.loading())
            val response = ApiConfig.getApiService().allStudentScoress(materyType,studentId)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()
                if(result?.code == 200){//TODO karna apinya meski code bukan 200 tp dianggap sukses oleh okhttp
                    emit(Resource.success(result))
                }else{
                    emit(Resource.error(result?.message, null))
                }
            } else if (response.errorBody() != null) {
                val errResult =
                    Gson().fromJson(response.errorBody()?.charStream(), StudentScoresResponse::class.java)
                emit(Resource.error(errResult.message, null))
            } else {
                emit(Resource.error(response.message(), null))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message, null))
        }
    }
    fun getStudentGameScores(gameType: Int, studentId: Int): Flow<Resource<StudentScoreResponse>> = flow {
        try {
            emit(Resource.loading())
            val response = ApiConfig.getApiService().studentGameScore(gameType,studentId)
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()
                if(result?.code == 200){//TODO karna apinya meski code bukan 200 tp dianggap sukses oleh okhttp
                    emit(Resource.success(result))
                }else{
                    emit(Resource.error(result?.message, null))
                }
            } else if (response.errorBody() != null) {
                val errResult =
                    Gson().fromJson(response.errorBody()?.charStream(),StudentScoreResponse::class.java)
                emit(Resource.error(errResult.message, null))
            } else {
                emit(Resource.error(response.message(), null))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message, null))
        }
    }
}