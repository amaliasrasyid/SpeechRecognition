package com.wisnu.speechrecognition.local_db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentScore (
   var id: Int, //id nilai skor siswa di db (untuk update)
   var score: Int,
   var spokenWords: String,
   var righWord: Int,
   var wrongWord: Int,
   var distance: Int
): Parcelable