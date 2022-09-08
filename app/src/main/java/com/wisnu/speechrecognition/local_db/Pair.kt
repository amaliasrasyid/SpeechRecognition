package com.wisnu.speechrecognition.local_db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pair(
    var id: Int? = null,
    var gambar: String? = null,
    var kata: String? = null,
    var idSoal: Int? = null,
):Parcelable