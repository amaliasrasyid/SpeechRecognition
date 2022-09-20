package com.wisnu.speechrecognition.utils

import android.speech.SpeechRecognizer

object ErrorCodeSpeechRecognizer {
    fun getErrorCode(erroCode:Int) :String{
        when(erroCode){
            SpeechRecognizer.ERROR_AUDIO -> return "Kesalahan perekaman audio"
            SpeechRecognizer.ERROR_CLIENT -> return "Kesalahan sisi klien"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> return "Izin tidak memadai"
            SpeechRecognizer.ERROR_NETWORK -> return "Kesalahan jaringan"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> return "Jaringan time out"
            SpeechRecognizer.ERROR_NO_MATCH -> return "Tidak ada hasil pengenalan yang cocok."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> return "Recognition Service sedang sibuk"
            SpeechRecognizer.ERROR_SERVER -> return "kesalahan dari server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> return "Tidak ada masukan ucapan"
            else -> return "Tidak mengerti, silakan coba lagi."
        }
    }
}