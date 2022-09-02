package com.wisnu.speechrecognition.local_db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionClass(
    var id: Int? = null,
    var gambar: String? = null,
    var suara: String? = null,
    var teksJawaban: String? = null,
    var materiPelajaran: Int? = null,
):Parcelable