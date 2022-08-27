package com.wisnu.speechrecognition.network

interface ApiService {

//    /** AUTH */
//    @FormUrlEncoded
//    @POST("auth/login")
//    fun login(@Field("no_handphone") numberPhone: String): Call<ResponseLogin>

//    @FormUrlEncoded
//    @POST("auth/register")
//    fun register(@FieldMap params: HashMap<String, String>): Call<ResponseRegister>


    fun login()

    fun register()

    fun detailUser()

    fun materyStudy() //by tipenya

    fun questionsStudy()

    fun getUrlImageAndSound()

}
