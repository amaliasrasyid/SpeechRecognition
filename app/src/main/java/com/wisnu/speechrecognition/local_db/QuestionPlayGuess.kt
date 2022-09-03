package com.wisnu.speechrecognition.local_db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionPlayGuess(
    var id: Int? = null,
    var suara: String? = null,
    var opsi1: String? = null,
    var opsi2: String? = null,
    var opsi3: String? = null,
    var kunciJawaban: Int? = null,
):Parcelable