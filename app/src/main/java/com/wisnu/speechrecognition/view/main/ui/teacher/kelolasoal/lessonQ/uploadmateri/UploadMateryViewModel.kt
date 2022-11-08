package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.uploadmateri

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.data.model.matery.MateryStudyResponse
import com.wisnu.speechrecognition.data.model.student.StudentScoresResponse
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.view.main.ui.score.ScoreViewModel
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadMateryViewModel : ViewModel() {
    private var _materyStudy = MutableLiveData<MateryStudyResponse>()
    private val TAG = UploadMateryFragment::class.java.simpleName
    private val RESPONSE_CLASS = MateryStudyResponse::class.java

    fun storeMateryStudy(params: HashMap<String,Any>): LiveData<MateryStudyResponse> {
        storeMatery(params)
        return _materyStudy
    }

    fun storeMatery(params: HashMap<String,Any>){
        val client = ApiConfig.getApiService().storeMateryStudy(params)
        val gson = Gson()
        client.enqueue(object : Callback<MateryStudyResponse> {
            override fun onResponse(call: Call<MateryStudyResponse>, response: Response<MateryStudyResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _materyStudy.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _materyStudy.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<MateryStudyResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        });
    }
}