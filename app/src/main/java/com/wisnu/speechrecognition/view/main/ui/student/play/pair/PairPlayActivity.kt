package com.wisnu.speechrecognition.view.main.ui.student.play.pair

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wisnu.speechrecognition.databinding.ActivityPairPlayBinding

class PairPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPairPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPairPlayBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        with(binding){
            val img1 = imgTes1

            val x = img1.x
            val y = img1.y
            Log.d(TAG,"coordinate x:${x}, coordinate y:${y} of img1")
        }
    }
}