package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.upload

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

class UploadGuessQViewModel : ViewModel() {
    private var _guessQ = MutableLiveData<QuestionPlayGuessResponse>()
    private val TAG = UploadGuessQViewModel::class.java.simpleName
    private val RESPONSE_CLASS = QuestionPlayGuessResponse::class.java

    fun store(audio: MultipartBody.Part?, params: HashMap<String, RequestBody>):LiveData<QuestionPlayGuessResponse>{
        storeGuessQ(audio,params)
        return _guessQ
    }

    private fun storeGuessQ(audio: MultipartBody.Part?, params: HashMap<String, RequestBody>) {
        val client = ApiConfig.getApiService().storeGuessQ(audio,params)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionPlayGuessResponse> {
            override fun onResponse(call: Call<QuestionPlayGuessResponse>, response: Response<QuestionPlayGuessResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _guessQ.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _guessQ.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionPlayGuessResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Log.e(TAG, "onFailure: ${t.printStackTrace()}")
            }
        })
    }

}