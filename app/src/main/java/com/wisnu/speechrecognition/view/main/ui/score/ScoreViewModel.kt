package com.wisnu.speechrecognition.view.main.ui.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.data.model.student.StudentScoreResponse
import com.wisnu.speechrecognition.data.model.student.StudentScoresResponse
import com.wisnu.speechrecognition.network.ApiConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//@HiltViewModel
class ScoreViewModel : ViewModel() {
    private var _studentScores = MutableLiveData<StudentScoresResponse>()
    private var _crudScore = MutableLiveData<StudentScoreResponse>()
    private var _gameScore = MutableLiveData<StudentScoreResponse>()
    private val TAG = ScoreViewModel::class.java.simpleName
    private val RESPONSE_CLASS_SCORES = StudentScoresResponse::class.java
    private val RESPONSE_CLASS_SCORE = StudentScoreResponse::class.java

    fun studentScores(materyType: Int,studentId: Int): LiveData<StudentScoresResponse> {
        _studentScores = getStudentScores(materyType,studentId);
        return _studentScores
    }

    fun studentGameScore(gameType: Int,studentId: Int): LiveData<StudentScoreResponse> {
        _gameScore = getGameScore(gameType,studentId);
        return _gameScore
    }

    private fun getGameScore(gameType: Int, studentId: Int): MutableLiveData<StudentScoreResponse> {
        val client = ApiConfig.getApiService().studentGameScore(gameType,studentId)
        val gson = Gson()
        client.enqueue(object : Callback<StudentScoreResponse> {
            override fun onResponse(call: Call<StudentScoreResponse>, response: Response<StudentScoreResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _gameScore.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS_SCORE)
                    _gameScore.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<StudentScoreResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        });
        return _gameScore
    }

    fun storeScore(params: HashMap<String,Any>): LiveData<StudentScoreResponse>{
        storeStudentScore(params)
        return _crudScore
    }

    private fun getStudentScores(materyType: Int, studentId: Int): MutableLiveData<StudentScoresResponse> {
        val client = ApiConfig.getApiService().allStudentScoress(materyType,studentId)
        val gson = Gson()
        client.enqueue(object : Callback<StudentScoresResponse> {
            override fun onResponse(call: Call<StudentScoresResponse>, response: Response<StudentScoresResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _studentScores.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS_SCORES)
                    _studentScores.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<StudentScoresResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        });
        return _studentScores
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