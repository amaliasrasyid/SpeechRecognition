package com.wisnu.speechrecognition.view.main.ui.student.study.material_study

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.model.matery.MateryStudyResponse
import com.wisnu.speechrecognition.model.student.StudentScoresResponse
import com.wisnu.speechrecognition.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MaterialStudyViewModel : ViewModel() {
    private var _materialStudy = MutableLiveData<MateryStudyResponse>()
    private val TAG = MaterialStudyViewModel::class.java.simpleName
    private val RESPONSE_CLASS = MateryStudyResponse::class.java

    fun materialStudy(materyType: Int): LiveData<MateryStudyResponse>{
        _materialStudy = getMaterialStudy(materyType);
        return _materialStudy
    }

    fun getMaterialStudy(materyType: Int): MutableLiveData<MateryStudyResponse>{
        val client = ApiConfig.getApiService().materyStudy(materyType)
        val gson = Gson()
        client.enqueue(object : Callback<MateryStudyResponse> {
            override fun onResponse(call: Call<MateryStudyResponse>, response: Response<MateryStudyResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _materialStudy.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _materialStudy.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<MateryStudyResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        });
        return _materialStudy
    }
}