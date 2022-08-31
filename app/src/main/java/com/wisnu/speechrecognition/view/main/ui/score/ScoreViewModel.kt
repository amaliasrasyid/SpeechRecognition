package com.wisnu.speechrecognition.view.main.ui.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.model.student.StudentScoresResponse
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScoreViewModel : ViewModel() {
    private var _studentScores = MutableLiveData<StudentScoresResponse>()
    private val TAG = ScoreViewModel::class.java.simpleName
    private val RESPONSE_CLASS_SCORES = StudentScoresResponse::class.java

    fun studentScores(materyType: Int,studentId: Int): LiveData<StudentScoresResponse> {
        _studentScores = getStudentScores(materyType,studentId);
        return _studentScores
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

}