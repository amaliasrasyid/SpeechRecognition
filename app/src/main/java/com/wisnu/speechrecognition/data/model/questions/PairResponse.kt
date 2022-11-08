package com.wisnu.speechrecognition.data.model.questions

import com.google.gson.annotations.SerializedName

data class PairResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("kata")
	val kata: String? = null,

	@field:SerializedName("soal")
	val soal: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null
)
