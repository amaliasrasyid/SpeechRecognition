package com.wisnu.speechrecognition.service

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.wisnu.speechrecognition.R

class BackgroundSoundService : Service() {
    private val binder = SoundBinder()
    private lateinit var _mediaPlayer : MediaPlayer
    private lateinit var audioRaw: AssetFileDescriptor
    private var secondAudioRaw: AssetFileDescriptor? = null
    private var isReady: Boolean = false
    private var isLooping: Boolean = false

    private var contentType: Int = AudioAttributes.CONTENT_TYPE_SPEECH

    companion object {
        private val TAG = BackgroundSoundService::class.java.simpleName
        private const val MUSIC_BACKGROUND_TYPE = 200
        private const val SPEECH_INTRO_TYPE = 300
    }

    fun setDataMediaPlayer(islooping :Boolean, contentType: Int, secondAudioRaw: AssetFileDescriptor? = null){
//        this.audioRaw = audioRaw
        this.isLooping = isLooping
        this.contentType = contentType
        this.secondAudioRaw = secondAudioRaw
    }

    override fun onCreate() {
        super.onCreate()
        _mediaPlayer = MediaPlayer()
        _mediaPlayer.isLooping = false
        audioRaw = applicationContext.resources.openRawResourceFd(R.raw.speech_intro)

        prepareMediaPlayer()
        startMediaPlayer()
        _mediaPlayer.setOnCompletionListener(object :MediaPlayer.OnCompletionListener{
            override fun onCompletion(mediaPlayer: MediaPlayer) {
//                mediaPlayer.release()
                //reinitialize mediaplayer
                _mediaPlayer = MediaPlayer()
                audioRaw = applicationContext.resources.openRawResourceFd(R.raw.music_app)
                _mediaPlayer.isLooping = true
                prepareMediaPlayer()
                startMediaPlayer()
            }
        })
        Log.d(TAG,"oncreate: music starting")
    }

    private fun startMediaPlayer() {
        _mediaPlayer?.setOnPreparedListener {
            isReady = true
            _mediaPlayer?.start()
        }
        _mediaPlayer?.setOnErrorListener { _, _, _ -> false }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.d(TAG,"start: music starting")
    }



    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        _mediaPlayer.stop()
        Log.d(TAG,"unbind: music stoping")
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        _mediaPlayer.start()
        Log.d(TAG,"rebind: music starting")
    }

    override fun onDestroy() {
        super.onDestroy()
        _mediaPlayer.release()
        Log.d(TAG,"destroy: music released")
    }

    private fun prepareMediaPlayer() {
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(contentType)
            .build()
        try {
            _mediaPlayer?.setAudioAttributes(attribute)
            _mediaPlayer?.setDataSource(audioRaw.fileDescriptor,audioRaw.startOffset,audioRaw.length)
            _mediaPlayer?.prepare()
        } catch (e: Exception) {
            Log.e(TAG, "prepareMediaPlayer: ${e.message}")
        }
    }

    inner class SoundBinder: Binder(){
        fun getService(): BackgroundSoundService = this@BackgroundSoundService
    }
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

}