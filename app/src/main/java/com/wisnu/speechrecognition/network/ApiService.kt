package com.wisnu.speechrecognition.network

import com.wisnu.speechrecognition.model.login.LoginResponse
import com.wisnu.speechrecognition.model.matery.MateryStudyResponse
import com.wisnu.speechrecognition.model.questions.QuestionStudyResponse
import com.wisnu.speechrecognition.model.student.StudentScoreResponse
import com.wisnu.speechrecognition.model.student.StudentsResultResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

//    /** AUTH */
//    @FormUrlEncoded
//    @POST("auth/login")
//    fun login(@Field("no_handphone") numberPhone: String): Call<ResponseLogin>

//    @FormUrlEncoded
//    @POST("auth/register")
//    fun register(@FieldMap params: HashMap<String, String>): Call<ResponseRegister>

    @FormUrlEncoded
    @POST("login")
    fun login(@FieldMap params: HashMap<String,Any>): Call<LoginResponse>

    fun register()

    fun detailUser()

    @GET("user/{roleId}")
    fun getStudents(@Path("roleId") roleId: Int): Call<StudentsResultResponse>

    @GET("matery/{id}")
    fun materyStudy(@Path("id") materyType: Int): Call<MateryStudyResponse>

    @GET("lessonq/{materyId}")
    fun getQuestionsStudy(@Path("materyId") materyId: Int): Call<QuestionStudyResponse>

    @GET("lessonq")
    fun getQuestionsGuess(): Call<Any>

    @GET("lessonq")
    fun getQuestionsPairWords(): Call<Any>

    @FormUrlEncoded
    @POST("student-score")
    fun storeStudentScrore(@FieldMap params: HashMap<String,Any>): Call<StudentScoreResponse>

}
