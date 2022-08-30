package com.wisnu.speechrecognition.model.student

import com.google.gson.annotations.SerializedName

data class StudentScoreResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("siswa")
	val siswa: String? = null,

	@field:SerializedName("soal")
	val soal: String? = null,

	@field:SerializedName("nilai")
	val nilai: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
