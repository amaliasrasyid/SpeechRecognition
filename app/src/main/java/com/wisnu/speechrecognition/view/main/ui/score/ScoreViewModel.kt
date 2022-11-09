package com.wisnu.speechrecognition.view.main.ui.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.wisnu.speechrecognition.data.model.student.StudentScoreResponse
import com.wisnu.speechrecognition.data.model.student.StudentScoresResponse
import com.wisnu.speechrecognition.data.repository.ScoreRepository
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(private val repository: ScoreRepository) : ViewModel() {
    private var _crudScore = MutableLiveData<StudentScoreResponse>()
    private val TAG = ScoreViewModel::class.java.simpleName
    private val RESPONSE_CLASS_SCORE = StudentScoreResponse::class.java

    fun studentScores(materyType: Int,studentId: Int): LiveData<Resource<StudentScoresResponse>> = repository.getStudentScores(materyType,studentId).asLiveData()

    fun studentGameScore(gameType: Int,studentId: Int): LiveData<Resource<StudentScoreResponse>> = repository.getStudentGameScores(gameType,studentId).asLiveData()

    fun storeScore(params: HashMap<String,Any>): LiveData<StudentScoreResponse>{
        storeStudentScore(params)
        return _crudScore
    }

    //api bisa handle sekaligus update jg jika data tidak ditemukan
    fun storeStudentScore(params: HashMap<String, Any>) {
        val client = ApiConfig.getApiService().storeStudentScrore(params)
        val gson = Gson()
        client.enqueue(object : Callback<StudentScoreResponse> {
            override fun onResponse(call: Call<StudentScoreResponse>, response: Response<StudentScoreResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _crudScore.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS_SCORE)
                    _crudScore.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<StudentScoreResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Log.e(TAG, "onFailure: ${t.printStackTrace()}")
            }
        })
    }

}