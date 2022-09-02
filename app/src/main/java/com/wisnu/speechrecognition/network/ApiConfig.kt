package com.wisnu.speechrecognition.network

import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        //  url
        const val URL = "http://192.168.1.8:8000" // local  WIFI HOME
//        const val URL = "http://192.168.100.215:8000" // local WIFI KANTOR
        const val URL_IMAGE = "$URL/images/"
        const val URL_SOUNDS = "$URL/sounds/"
//        const val URL = "http://10.0.2.2:3000" // local emulator

        private const val ENDPOINT = "$URL/api/"

        private fun client(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                )
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }

        fun getApiService(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}