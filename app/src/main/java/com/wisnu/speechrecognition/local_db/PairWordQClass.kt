package com.wisnu.speechrecognition.local_db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PairWordQClass(
    var id: Int? = null,
    var suara: String? = null,
):Parcelable