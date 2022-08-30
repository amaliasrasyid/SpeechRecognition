package com.wisnu.speechrecognition.model.questions

import com.google.gson.annotations.SerializedName

data class QuestionStudyResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("data")
	val data: List<Question>? = null,

	@field:SerializedName("message")
	val message: String
)

data class Question(

	@field:SerializedName("materi_pelajaran")
	val materiPelajaran: Int,

	@field:SerializedName("suara")
	val suara: String? = null,

	@field:SerializedName("teks_jawaban")
	val teksJawaban: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("gambar")
	val gambar: String? = null
)
