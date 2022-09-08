package com.wisnu.speechrecognition.view.main.ui.student.play.pair

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.wisnu.speechrecognition.databinding.ActivityPairPlayBinding
import com.wisnu.speechrecognition.local_db.PairWordQClass
import com.wisnu.speechrecognition.model.questions.PairWordQ
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_WARNING
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyViewModel
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.PairQViewModel
import www.sanju.motiontoast.MotionToast

class PairPlayActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPairPlayBinding

    private var _drawView: DrawView? = null
    private var selectedImage: ImageView? = null
    private var listSelectedImg = ArrayList<ImageView>()
    private var listSelectedText = ArrayList<TextView>()
    private var listPairQ = ArrayList<PairWordQ>()
    private var score :Int = 0

    private val viewModel by viewModels<PairQViewModel>()

    private val TAG = PairPlayActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPairPlayBinding.inflate(layoutInflater)
        val view = binding.root
//        drawView = DrawView(this@PairPlayActivity)
        setContentView(view)

        with(binding){
            point1.setOnClickListener(this@PairPlayActivity)
            point2.setOnClickListener(this@PairPlayActivity)
            point1Tv.setOnClickListener(this@PairPlayActivity)
            point2Tv.setOnClickListener(this@PairPlayActivity)
            imgTes1.setOnClickListener(this@PairPlayActivity)
            imgTest2.setOnClickListener(this@PairPlayActivity)
            tvTest1.setOnClickListener(this@PairPlayActivity)
            tvTest2.setOnClickListener(this@PairPlayActivity)

            imgTes1.contentDescription ="medali"
            imgTest2.contentDescription = "orang"
        }
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                imgTes1 -> {
                    if(selectedImage != imgTes1){
                        val isImageHadSelected = find(listSelectedImg,imgTes1)
                        if(!isImageHadSelected){
                            selectedImage = imgTes1
                            drawStartLine(imgTes1)
                            listSelectedImg.add(imgTes1)
                        }else{
                            showMessage(
                                this@PairPlayActivity,
                                TITLE_WARNING,
                                "gambar sudah dipasangkan dengan katanya, silahkan pasangkan gambar yang lain",
                                MotionToast.TOAST_WARNING
                            )
                        }
                    }else{
                        showMessage(
                            this@PairPlayActivity,
                            TITLE_WARNING,
                            "gambar sudah dipilih, pilihlah pasangan katanya",
                            MotionToast.TOAST_WARNING
                        )
                    }

                }
                imgTest2 -> {
                    if(selectedImage != imgTest2){
                        val isImageHadSelected = find(listSelectedImg,imgTest2)
                        if(!isImageHadSelected){
                            selectedImage = imgTest2
                            drawStartLine(imgTest2)
                            listSelectedImg.add(imgTest2)
                        }else{
                            showMessage(
                                this@PairPlayActivity,
                                TITLE_WARNING,
                                "gambar sudah dipasangkan dengan katanya, silahkan pasangkan gambar yang lain",
                                MotionToast.TOAST_WARNING
                            )
                        }
                    }else{
                        showMessage(
                            this@PairPlayActivity,
                            TITLE_WARNING,
                            "gambar sudah dipilih, pilihlah pasangan katanya",
                            MotionToast.TOAST_WARNING
                        )
                    }
                }
                tvTest1 -> {
                    val isTextViewHadSelected = find(listSelectedText,tvTest1)
                    if(!isTextViewHadSelected){
                        drawDestLine(tvTest1)
                        checkPair(tvTest1.text.toString())
                        listSelectedText.add(tvTest1)
                    }else{
                        showMessage(
                            this@PairPlayActivity,
                            TITLE_WARNING,
                            "kata sudah dipasangkan",
                            MotionToast.TOAST_WARNING
                        )
                    }

                }
                tvTest2 -> {
                    val isTextViewHadSelected = find(listSelectedText,tvTest2)
                    if(!isTextViewHadSelected){
                        drawDestLine(tvTest2)
                        checkPair(tvTest2.text.toString())
                        listSelectedText.add(tvTest2)
                    }else{
                        showMessage(
                            this@PairPlayActivity,
                            TITLE_WARNING,
                            "kata sudah dipasangkan",
                            MotionToast.TOAST_WARNING
                        )
                    }
                }
                else -> {}
            }
        }

    }

    private fun checkPair(text: String) {
        val imgPair = selectedImage!!.contentDescription.toString().uppercase()
        if(text == imgPair){
            ++score
        }
        Log.d(TAG,"content description img : ${imgPair}")
        Log.d(TAG,"skor saat ini : ${score}")
    }

    private fun find(list: List<Any>, view :View): Boolean {
        return list.filter{it == view}.isNotEmpty()
    }

    private fun drawStartLine(view: View) {
        val x1 = view.x
        val y1 = view.y
        binding.drawView.addSourcePoint(x1,y1)
        Log.d(TAG,"img point position x1:${x1},y1:${y1}")
    }

    private fun drawDestLine(view: View) {
        val x2 = view.x
        val y2 = view.y
        binding.drawView.addDestinationPoint(x2,y2)
        Log.d(TAG,"text point position x2:${x2},y2:${y2}")
    }



}