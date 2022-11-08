package com.wisnu.speechrecognition.data.model.questions

import com.google.gson.annotations.SerializedName

data class QuestionPlayPairWordResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<PairWordQ>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class PairWordQ(

	@field:SerializedName("suara")
	val suara: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("pairs")
	val pairs: List<PairsItem>? = null
)

data class PairsItem(

	@field:SerializedName("kata")
	val kata: String? = null,

	@field:SerializedName("soal")
	val soal: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null
)
