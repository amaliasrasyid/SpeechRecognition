package com.wisnu.speechrecognition.model.questions

import com.google.gson.annotations.SerializedName

data class QuestionPlayGuessResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<GuessQItem>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class GuessQItem(

	@field:SerializedName("suara")
	val suara: String? = null,

	@field:SerializedName("opsi1")
	val opsi1: String? = null,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("opsi2")
	val opsi2: String? = null,

	@field:SerializedName("opsi3")
	val opsi3: String? = null,

	@field:SerializedName("kunci_jawaban")
	val kunciJawaban: Int? = null
)
