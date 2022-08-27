package com.wisnu.speechrecognition.model.matery

import com.google.gson.annotations.SerializedName

data class MateryStudyResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataItem(

	@field:SerializedName("tipe_materi")
	val tipeMateri: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("teks_materi")
	val teksMateri: String? = null
)
