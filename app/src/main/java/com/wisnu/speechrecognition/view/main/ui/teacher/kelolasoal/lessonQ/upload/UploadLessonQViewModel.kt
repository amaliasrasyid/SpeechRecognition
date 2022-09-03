package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.model.questions.QuestionStudyResponse
import com.wisnu.speechrecognition.network.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadLessonQViewModel: ViewModel() {
    private var _lessonQ = MutableLiveData<QuestionStudyResponse>()
    private val TAG = UploadLessonQViewModel::class.java.simpleName
    private val RESPONSE_CLASS = QuestionStudyResponse::class.java

    fun questions(materyId: Int): LiveData<QuestionStudyResponse>{
        _lessonQ = getQuestionsStudy(materyId)
        return _lessonQ
    }

    fun uploadLessonQ(image: MultipartBody.Part?,audio: MultipartBody.Part?,params: HashMap<String,RequestBody>): LiveData<QuestionStudyResponse>{
        storeOrUpdateLessonQ(audio,image,params)
        return _lessonQ
    }

    fun getQuestionsStudy(materyId: Int): MutableLiveData<QuestionStudyResponse>{
        val client = ApiConfig.getApiService().getQuestionsStudy(materyId)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionStudyResponse> {
            override fun onResponse(call: Call<QuestionStudyResponse>, response: Response<QuestionStudyResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _lessonQ.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _lessonQ.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<QuestionStudyResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _lessonQ
    }


    private fun storeOrUpdateLessonQ(image: MultipartBody.Part?,audio: MultipartBody.Part?, params: HashMap<String, RequestBody>) {
        val client = ApiConfig.getApiService().storeQuestionStudy(audio,image,params)
        val gson = Gson()
        client.enqueue(object : Callback<QuestionStudyResponse> {
            override fun onResponse(call: Call<QuestionStudyResponse>, response: Response<QuestionStudyResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _lessonQ.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _lessonQ.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
//                    [size=22466 text={\n    "message": "SQLSTATE[23000]: Integrity constraint violatio…]
                }
            }

            override fun onFailure(call: Call<QuestionStudyResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Log.e(TAG, "onFailure: ${t.printStackTrace()}")
            }
        })
    }

}