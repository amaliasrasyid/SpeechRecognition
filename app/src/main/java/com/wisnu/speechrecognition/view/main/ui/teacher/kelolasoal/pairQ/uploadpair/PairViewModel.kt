package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.uploadpair

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wisnu.speechrecognition.model.questions.PairResponse
import com.wisnu.speechrecognition.network.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PairViewModel: ViewModel() {
    private var _pair = MutableLiveData<PairResponse>()
    private val TAG = PairViewModel::class.java.simpleName
    private val RESPONSE_CLASS = PairResponse::class.java

    
    fun uploadPair(
        image: MultipartBody.Part?,
        params: HashMap<String,RequestBody>): LiveData<PairResponse>{
        _pair = storeOrUpdatePair(image,params)
        return _pair
    }

    fun delete(idPair: Int): LiveData<PairResponse>{
        _pair = deletePair(idPair)
        return _pair
    }

    private fun deletePair(idPair: Int): MutableLiveData<PairResponse> {
        return _pair
    }

    private fun storeOrUpdatePair(
        image: MultipartBody.Part?,
        params: HashMap<String, RequestBody>): MutableLiveData<PairResponse> {
        val client = ApiConfig.getApiService().storePair(image,params)
        val gson = Gson()
        client.enqueue(object : Callback<PairResponse> {
            override fun onResponse(call: Call<PairResponse>, response: Response<PairResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _pair.postValue(result!!)
                } else {
                    val errResult = gson.fromJson(response.errorBody()?.string(),RESPONSE_CLASS)
                    _pair.postValue(errResult)
                    Log.e(TAG, "onFailure: $errResult")
                }
            }

            override fun onFailure(call: Call<PairResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                Log.e(TAG, "onFailure: ${t.printStackTrace()}")
            }
        })
        return _pair
    }

}