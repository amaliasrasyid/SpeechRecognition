package com.wisnu.speechrecognition.view.main.ui.teacher.students

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.data.model.student.StudentsResultResponse
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_SISWA
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentsViewModel : ViewModel() {
    private var _students = MutableLiveData<StudentsResultResponse>()
    private val TAG = StudentsViewModel::class.java.simpleName
    private val RESPONSE_CLASS = StudentsResultResponse::class.java

    fun students(): LiveData<StudentsResultResponse>{
        _students = getStudents()
        return _students
    }

    private fun getStudents(): MutableLiveData<StudentsResultResponse> {
        val client = ApiConfig.getApiService().getStudents()
        val gson = Gson()
        client.enqueue(object : Callback<StudentsResultResponse> {
            override fun onResponse(call: Call<StudentsResultResponse>, response: Response<StudentsResultResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _students.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _students.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<StudentsResultResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        });
        return _students
    }

}