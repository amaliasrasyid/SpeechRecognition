package com.wisnu.speechrecognition.network

import com.wisnu.speechrecognition.model.login.LoginResponse
import com.wisnu.speechrecognition.model.matery.MateryStudyResponse
import com.wisnu.speechrecognition.model.questions.QuestionPlayGuessResponse
import com.wisnu.speechrecognition.model.questions.QuestionStudyResponse
import com.wisnu.speechrecognition.model.student.StudentScoreResponse
import com.wisnu.speechrecognition.model.student.StudentScoresResponse
import com.wisnu.speechrecognition.model.student.StudentsResultResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    //ROUTE-DATA MATERY (MATERIAL STUDY)
    @FormUrlEncoded
    @POST("matery")
    fun storeMateryStudy(@FieldMap params: HashMap<String,Any>): Call<MateryStudyResponse>

    @GET("matery/{id}")
    fun materyStudy(@Path("id") materyType: Int): Call<MateryStudyResponse>

    @DELETE("matery/{id}")
    fun deleteMateryStudy(@Path("id") idMateryStudy: Int): Call<MateryStudyResponse>


    // ROUTE-LESSONQ
    @GET("lessonq/{materyId}")
    fun getQuestionsStudy(@Path("materyId") materyId: Int): Call<QuestionStudyResponse>

    @Multipart
    @POST("lessonq")
    fun storeQuestionStudy(
        @Part audio: MultipartBody.Part?,
        @Part image: MultipartBody.Part?,
        @PartMap params: HashMap<String,RequestBody>
    ): Call<QuestionStudyResponse>

    @DELETE("lessonq/{id}")
    fun deleteQuestionStudy(@Path("id") idQuestion: Int): Call<QuestionStudyResponse>


    // ROUTE-GUESSQ
    @GET("guessq")
    fun getQuestionsGuess(): Call<QuestionPlayGuessResponse>

    @Multipart
    @POST("guessq")
    fun storeGuessQ(
        @Part audio: MultipartBody.Part?,
        @PartMap params: HashMap<String, RequestBody>
    ): Call<QuestionPlayGuessResponse>

    @DELETE("guessq/{id}")
    fun deleteQuestionGuess(@Path("id") idGuessQ: Int): Call<QuestionPlayGuessResponse>


    // ROUTE-PAIRQ
    @GET("pairwordq")
    fun getQuestionsPairWords(): Call<Any>


    //ROUTE-STUDENT SCORE
    @FormUrlEncoded
    @POST("student-score")
    fun storeStudentScrore(@FieldMap params: HashMap<String,Any>): Call<StudentScoreResponse>

    @GET("student-score/{tipe_materi}/{id_siswa}")
    fun allStudentScoress(
        @Path("tipe_materi") materyType: Int,
        @Path("id_siswa") studentId: Int,
    ): Call<StudentScoresResponse>

}
