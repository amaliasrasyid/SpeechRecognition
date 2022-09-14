package com.wisnu.speechrecognition.utils

import com.wisnu.speechrecognition.R

object UtilsCode {
    const val TIPE_HURUF_AZ = 1
    const val TIPE_HURUF_KONSONAN = 2
    const val TIPE_HURUF_VOKAL = 3
    const val TIPE_MEMBACA = 4
    const val TIPE_BERMAIN_TEBAK_KATA = 5
    const val TIPE_BERMAIN_TEMUKAN_PASANGAN = 6


    const val MODE_NIGHT = 1
    const val MODE_LIGHT = 0
    const val UNDEFINIED_MODE = -1

    const val REQUEST_SUCCESS = 200
    const val REQUEST_SUCCESS_CREATE = 201
    const val REQUEST_NOT_FOUND = 404

    const val REQUEST_CODE_PERMISSIONS = 1
    const val REQUEST_CODE_AUDIO_RECORD = 2
    const val REQUEST_CODE_CAPTURE_IMAGE = 3
    const val REQUEST_CODE_SELECT_IMAGE = 4

    const val PATTERN_DATE_VIEW = "dd MMMM yyyy"
    const val PATTERN_DATE_API = "dd-MM-yyyy"
    const val PATTERN_DATE_VIEW_YEAR = "yyyy"
    const val PATTERN_DATE_VIEW_MONTH = "MM"
    const val PATTERN_DATE_VIEW_DAY = "dd"
    const val PATTERN_TIMESTAMP_FROM_API = "yyyy-MM-dd HH:mm:ss"
    const val PATTERN_TIMESTAMP_VIEW_STATUS_PEMBAYARAN = "dd MMMM yyyy HH:mm"
    const val PATTERN_DATE_VIEW_MILESTONE = "E, dd MMMM yyyy"
    const val PATTERN_TIME_VIEW_MILESTONE = "HH:mm"

    const val TYPE_AUTH_LOGIN = 1
    const val TYPE_AUTH_REGISTER = 2

    const val TIME_DELAY_SCREEN = 3000L
    const val TIME_DELAY_SCREEN_CHECK_INBOX = 5000L
    const val TIME_DELAY_SCREEN_VERIFICATION_SUCCESS = 3000L

    const val ROLE_ADMIN = 1
    const val ROLE_GURU = 2
    const val ROLE_SISWA = 3

    const val DEFAULT_TIMER = "00 : 00"

    const val REQUEST_KEY = "request_key"

    const val TITLE_ERROR = "Gagal"
    const val TITLE_SUCESS = "Sukses"
    const val TITLE_WARNING = "Peringatan"
    const val TITLE_INFO = "Informasi"
}