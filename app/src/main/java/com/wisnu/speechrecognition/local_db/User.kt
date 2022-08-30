package com.wisnu.speechrecognition.local_db

data class User(
    var id: Int? = null,
    var nama: String? = null,
    var email: String? = null,
    var role: Int? = null,
    var password: String? = null,
    var gambar: String? = null
//    var numberPhone: String?,
)