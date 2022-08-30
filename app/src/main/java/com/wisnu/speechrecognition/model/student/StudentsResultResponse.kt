package com.wisnu.speechrecognition.model.student

import com.google.gson.annotations.SerializedName

data class StudentsResultResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<StudentsResult>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class StudentsResult(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("role")
	val role: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
