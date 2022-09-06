package com.wisnu.speechrecognition.view.main.ui.student.play.pair

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import com.wisnu.speechrecognition.databinding.ActivityPairPlayBinding
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyViewModel

class PairPlayActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPairPlayBinding

    private val TAG = PairPlayActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPairPlayBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        with(binding){
            point1.setOnClickListener(this@PairPlayActivity)
            point2.setOnClickListener(this@PairPlayActivity)
            point1Tv.setOnClickListener(this@PairPlayActivity)
            point2Tv.setOnClickListener(this@PairPlayActivity)
        }
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                point1 -> drawStartLine(point1)
                point2 -> drawStartLine(point2)
                point1Tv -> drawDestLine(point1Tv)
                point2Tv -> drawDestLine(point2Tv)
            }
        }

    }

    private fun drawStartLine(view: ImageButton) {
        val drawView = DrawView(this@PairPlayActivity)
        val x1 = view.x
        val y1 = view.y
        drawView.addSourcePoint(x1,y1)
        Log.d(TAG,"img point position x1:${x1},y1:${y1}")
    }

    private fun drawDestLine(view: ImageButton) {
        val drawView = DrawView(this@PairPlayActivity)
        val x2 = view.x
        val y2 = view.y
        drawView.addDestinationPoint(x2,y2)
        Log.d(TAG,"text point position x2:${x2},y2:${y2}")
    }
}