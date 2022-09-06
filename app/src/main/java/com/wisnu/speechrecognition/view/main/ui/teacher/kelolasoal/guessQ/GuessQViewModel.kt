package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.model.questions.QuestionPlayGuessResponse
import com.wisnu.speechrecognition.network.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GuessQViewModel : ViewModel() {
    private var _questions = MutableLiveData<QuestionPlayGuessResponse>()
    private var _crudQuestions = MutableLiveData<QuestionPlayGuessResponse>()
    private val TAG = GuessQViewModel::class.java.simpleName
    private val RESPONSE_CLASS = QuestionPlayGuessResponse::class.java
    
    fun questions(): LiveData<QuestionPlayGuessResponse>{
        _questions = getGuessQ()
        return _questions
    }
    
    fun store(audio: MultipartBody.Part?,
               params: HashMap<String,RequestBody>
    ): LiveData<QuestionPlayGuessResponse>{
        _crudQuestions = storeGuessQ(audio,params)
        return _crudQuestions
    }
    
    fun delete(idGuessQ: Int): LiveData<QuestionPlayGuessResponse>{
        _crudQuestions = deleteGuessQ(idGuessQ)
        return _crudQuestions
    }

    private fun deleteGuessQ(idGuessQ: Int): MutableLiveData<QuestionPlayGuessResponse>{
        val client = ApiConfig.getApiService().deleteQuestionGuess(idGuessQ)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionPlayGuessResponse> {
            override fun onResponse(call: Call<QuestionPlayGuessResponse>, response: Response<QuestionPlayGuessResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _crudQuestions.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _crudQuestions.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionPlayGuessResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _crudQuestions
    }

    private fun storeGuessQ(
        audio: MultipartBody.Part?,
        params: HashMap<String, RequestBody>): MutableLiveData<QuestionPlayGuessResponse> {
        val client = ApiConfig.getApiService().storeGuessQ(audio,params)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionPlayGuessResponse> {
            override fun onResponse(call: Call<QuestionPlayGuessResponse>, response: Response<QuestionPlayGuessResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _crudQuestions.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _crudQuestions.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionPlayGuessResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        });
        return _crudQuestions
    }


    private fun getGuessQ(): MutableLiveData<QuestionPlayGuessResponse> {
        val client = ApiConfig.getApiService().getQuestionsGuess()
        val gson = Gson()
        client.enqueue(object : Callback<QuestionPlayGuessResponse> {
            override fun onResponse(call: Call<QuestionPlayGuessResponse>, response: Response<QuestionPlayGuessResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _questions.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _questions.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionPlayGuessResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _questions
    }



}