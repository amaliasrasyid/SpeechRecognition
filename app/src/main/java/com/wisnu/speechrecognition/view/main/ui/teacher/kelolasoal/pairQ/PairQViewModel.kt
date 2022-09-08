package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.model.questions.PairResponse
import com.wisnu.speechrecognition.model.questions.QuestionPlayPairWordResponse
import com.wisnu.speechrecognition.network.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PairQViewModel : ViewModel() {
    private var _questions = MutableLiveData<QuestionPlayPairWordResponse>()
    private var _crudQuestions = MutableLiveData<QuestionPlayPairWordResponse>()
    private var _crudPair = MutableLiveData<PairResponse>()
    private val TAG = PairQViewModel::class.java.simpleName
    private val RESPONSE_CLASS = QuestionPlayPairWordResponse::class.java
    private val RESPONSE_CLASS_PAIR = PairResponse::class.java

    fun questions(): LiveData<QuestionPlayPairWordResponse> {
        _questions = getPairQ()
        return _questions
    }

    fun store(
        idPairQ :RequestBody,
        audio: MultipartBody.Part?
    ): LiveData<QuestionPlayPairWordResponse> {
        _crudQuestions = storePairQ(idPairQ,audio)
        return _crudQuestions
    }

    fun delete(idPairQ: Int): LiveData<QuestionPlayPairWordResponse> {
        _crudQuestions = deletePairQ(idPairQ)
        return _crudQuestions
    }

    fun deletePair(idPair: Int): LiveData<PairResponse> {
        _crudPair = deletePairItem(idPair)
        return _crudPair
    }

    private fun deletePairItem(idPair: Int): MutableLiveData<PairResponse> {
        val client = ApiConfig.getApiService().deletePairItem(idPair)
        val gson = Gson()
        client.enqueue(object : Callback<PairResponse> {
            override fun onResponse(call: Call<PairResponse>, response: Response<PairResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _crudPair.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS_PAIR)
                    _crudPair.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<PairResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _crudPair
    }


    private fun deletePairQ(idPairQ: Int): MutableLiveData<QuestionPlayPairWordResponse>{
        val client = ApiConfig.getApiService().deleteQuestionPairW(idPairQ)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionPlayPairWordResponse> {
            override fun onResponse(call: Call<QuestionPlayPairWordResponse>, response: Response<QuestionPlayPairWordResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _crudQuestions.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _crudQuestions.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionPlayPairWordResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _crudQuestions
    }

    private fun storePairQ(
        idPairQ: RequestBody,
        audio: MultipartBody.Part?): MutableLiveData<QuestionPlayPairWordResponse> {
        val client = ApiConfig.getApiService().storeQuestionPairW(idPairQ,audio)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionPlayPairWordResponse> {
            override fun onResponse(call: Call<QuestionPlayPairWordResponse>, response: Response<QuestionPlayPairWordResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _crudQuestions.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _crudQuestions.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionPlayPairWordResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        });
        return _crudQuestions
    }

    private fun getPairQ(): MutableLiveData<QuestionPlayPairWordResponse> {
        val client = ApiConfig.getApiService().getQuestionsPairWords()
        val gson = Gson()
        client.enqueue(object : Callback<QuestionPlayPairWordResponse> {
            override fun onResponse(
                call: Call<QuestionPlayPairWordResponse>,
                response: Response<QuestionPlayPairWordResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _questions.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(), RESPONSE_CLASS)
                    _questions.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionPlayPairWordResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _questions
    }
    
}