package com.wisnu.speechrecognition.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.view.main.ui.teacher.TeacherActivity

object BackgroundSound {
    private var isReady: Boolean = false
    private var isFinished: Boolean = false
    var mediaPlayer: MediaPlayer

    private  val TAG = BackgroundSound::class.java.simpleName

    init {
        mediaPlayer = MediaPlayer()
    }

    fun inits (applicationContext: Context) {
        val audioRaw = applicationContext!!.resources.openRawResourceFd(R.raw.speech_intro)
        prepareMediaPlayer(audioRaw)

        mediaPlayer.setOnCompletionListener (object: MediaPlayer.OnCompletionListener{
            override fun onCompletion(_mediaPlayer: MediaPlayer) {
                isFinished = true
                _mediaPlayer.release()
                mediaPlayer = MediaPlayer()
                val audioRaw = applicationContext!!.resources.openRawResourceFd(R.raw.music_app)
                mediaPlayer.isLooping = true
                prepareMediaPlayer(audioRaw)
            }
        })
    }

    private fun prepareMediaPlayer(audioRaw: AssetFileDescriptor) {
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        try {
            mediaPlayer?.setAudioAttributes(attribute)
            mediaPlayer?.setDataSource(audioRaw.fileDescriptor,audioRaw.startOffset,audioRaw.length)
            mediaPlayer?.prepare()
        } catch (e: Exception) {
            Log.e(TAG, "prepareMediaPlayer: ${e.message}")
        }

        mediaPlayer?.setOnPreparedListener {
            isReady = true
            mediaPlayer?.start()
        }
        mediaPlayer?.setOnErrorListener { _, _, _ -> false }
    }
}