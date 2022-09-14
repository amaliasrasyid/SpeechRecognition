package com.wisnu.speechrecognition.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        //  url
//        const val URL = "http://192.168.1.8:8000" // local  WIFI HOME
//        const val URL = "http://192.168.100.215:8000" // local WIFI KANTOR
//        const val URL_IMAGE = "$URL/images/"
//        const val URL_SOUNDS = "$URL/sounds/"
        const val URL = "https://tonenyahudi.com" //RELEASE
        const val URL_IMAGE = "$URL/app-api/public/images/"
        const val URL_SOUNDS = "$URL/app-api/public/sounds/"
//        const val URL = "http://10.0.2.2:3000" // local emulator

        private const val ENDPOINT = "$URL/api/"


        //ALLOWED RETROFIT TO ACCESS LARAVEL API
        //laravel protect unauthorized access and prevents from unknown client request. and for this case, it was Retrofit
        var allowedClient = Interceptor { chain: Interceptor.Chain ->
            val response: Response
            val newRequest = chain.request().newBuilder()
                .addHeader("User-Agent", System.getProperty("http.agent")) //IMPORTANT
                .addHeader(
                    "Accept",
                    "application/json"
                ) //                    .addHeader("Content-Length",)
                .method(chain.request().method, chain.request().body)
                .build()
            newRequest.headers["Cookie"]
            response = chain.proceed(newRequest)
            response.headers["Set-Cookie"]
            response
        }

        private fun client(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(allowedClient)
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