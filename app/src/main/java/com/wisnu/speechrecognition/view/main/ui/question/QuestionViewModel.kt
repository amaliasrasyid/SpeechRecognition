package com.wisnu.speechrecognition.view.main.ui.question

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.data.model.matery.MateryStudyResponse
import com.wisnu.speechrecognition.data.model.questions.QuestionStudyResponse
import com.wisnu.speechrecognition.data.model.student.StudentScoreResponse
import com.wisnu.speechrecognition.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionViewModel : ViewModel() {
    private var _questions = MutableLiveData<QuestionStudyResponse>()
    private var _score = MutableLiveData<StudentScoreResponse>()
    private val TAG = QuestionViewModel::class.java.simpleName
    private val RESPONSE_CLASS = QuestionStudyResponse::class.java
    private val RESPONSE_CLASS_SCORE = StudentScoreResponse::class.java

    fun questions(materyId: Int): LiveData<QuestionStudyResponse>{
        _questions = getQuestionsStudy(materyId)
        return _questions
    }

    fun questionsByType(materyTypeId: Int): LiveData<QuestionStudyResponse>{
        _questions = getQuestionsStudyByType(materyTypeId)
        return _questions
    }

    fun storeScore(params: HashMap<String,Any>): LiveData<StudentScoreResponse>{
        storeStudentScore(params)
        return _score
    }

    //api bisa handle sekaligus update jg jika data tidak ditemukan
    fun storeStudentScore(params: HashMap<String, Any>) {
        val client = ApiConfig.getApiService().storeStudentScrore(params)
        val gson = Gson()
        client.enqueue(object : Callback<StudentScoreResponse> {
            override fun onResponse(call: Call<StudentScoreResponse>, response: Response<StudentScoreResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _score.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS_SCORE)
                    _score.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<StudentScoreResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Log.e(TAG, "onFailure: ${t.printStackTrace()}")
            }
        })
    }

    fun delete(questionId: Int):LiveData<QuestionStudyResponse>{
        deleteQuestion(questionId)
        return _questions
    }

    private fun deleteQuestion(questionId: Int){
        val client = ApiConfig.getApiService().deleteQuestionStudy(questionId)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionStudyResponse> {
            override fun onResponse(call: Call<QuestionStudyResponse>, response: Response<QuestionStudyResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _questions.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _questions.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionStudyResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getQuestionsStudy(materyId: Int): MutableLiveData<QuestionStudyResponse>{
        val client = ApiConfig.getApiService().getQuestionsStudy(materyId)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionStudyResponse> {
            override fun onResponse(call: Call<QuestionStudyResponse>, response: Response<QuestionStudyResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _questions.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _questions.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionStudyResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _questions
    }

    fun getQuestionsStudyByType(materyTypeId: Int): MutableLiveData<QuestionStudyResponse>{
        val client = ApiConfig.getApiService().getQuestionsStudyByType(materyTypeId)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionStudyResponse> {
            override fun onResponse(call: Call<QuestionStudyResponse>, response: Response<QuestionStudyResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _questions.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _questions.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionStudyResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _questions
    }

}