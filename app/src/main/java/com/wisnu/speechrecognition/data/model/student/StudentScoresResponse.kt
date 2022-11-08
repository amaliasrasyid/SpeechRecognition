package com.wisnu.speechrecognition.data.model.student

import com.google.gson.annotations.SerializedName

data class StudentScoresResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<StudentScore>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class StudentScore(

	@field:SerializedName("siswa")
	val siswa: Int? = null,

	@field:SerializedName("soal")
	val soal: Int? = null,

	@field:SerializedName("nilai")
	val nilai: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("nama_materi")
	val namaMateri: String? = null,

	@field:SerializedName("kalimat_vokal")
	val kalimatVokal: String? = null
)
