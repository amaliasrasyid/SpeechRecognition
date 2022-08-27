package com.wisnu.speechrecognition.model.questions

import com.google.gson.annotations.SerializedName

data class QuestionStudyResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataItem(

	@field:SerializedName("materi_pelajaran")
	val materiPelajaran: Int? = null,

	@field:SerializedName("suara")
	val suara: String? = null,

	@field:SerializedName("teks_jawaban")
	val teksJawaban: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null
)
