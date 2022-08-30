package com.wisnu.speechrecognition.model.matery

import com.google.gson.annotations.SerializedName

data class MateryTypeResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItemType?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("isSuccess")
	val isSuccess: Boolean? = null
)

data class DataItemType(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("kategori")
	val kategori: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
