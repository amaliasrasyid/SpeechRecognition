package com.wisnu.speechrecognition.view.main.ui.student

import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isReady: Boolean = false
    private var isFinished: Boolean = false

    private  val TAG = MainActivity::class.java.simpleName

    private lateinit var audioRaw: AssetFileDescriptor
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //play music as long as lifecycle this activity not destroyed
        mediaPlayer = MediaPlayer()
        audioRaw = applicationContext.resources.openRawResourceFd(R.raw.speech_intro)
        prepareMediaPlayer()


        mediaPlayer.setOnCompletionListener (object: MediaPlayer.OnCompletionListener{
            override fun onCompletion(_mediaPlayer: MediaPlayer) {
                isFinished = true
                _mediaPlayer.release()
                mediaPlayer = MediaPlayer()
                audioRaw = applicationContext.resources.openRawResourceFd(R.raw.music_app)
                mediaPlayer.isLooping = true
                prepareMediaPlayer()
            }
        })
        Log.d(TAG,"oncreate")
    }

    private fun prepareMediaPlayer() {
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


    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onRestart() {
        super.onRestart()
//        mediaPlayer.start()
        Log.d(TAG,"onrestart")
    }

    override fun onStart() {
        super.onStart()
        mediaPlayer.start()
        Log.d(TAG,"onstart")
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.pause()
        Log.d(TAG,"onstop")
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()

        Log.d(TAG,"ondestroy")
    }
}