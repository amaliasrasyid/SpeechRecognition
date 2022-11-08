package com.wisnu.speechrecognition.data.model.matery

import com.google.gson.annotations.SerializedName

data class MateryStudyResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<MateryStudy>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class MateryStudy(

	@field:SerializedName("tipe_materi")
	val tipeMateri: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("teks_materi")
	val teksMateri: String
)
