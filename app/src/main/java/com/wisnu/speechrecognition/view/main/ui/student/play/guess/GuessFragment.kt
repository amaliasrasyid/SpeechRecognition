package com.wisnu.speechrecognition.view.main.ui.student.play.guess

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentGuessBinding
import com.wisnu.speechrecognition.data.model.questions.GuessQItem
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.student.MainActivity
import com.wisnu.speechrecognition.view.main.ui.student.ResultFragment.Companion.QUESTION_TYPE
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.GuessQViewModel
import www.sanju.motiontoast.MotionToast

class GuessFragment : Fragment(), View.OnClickListener {

    private val viewModel by viewModels<GuessQViewModel>()
    private var _binding: FragmentGuessBinding? = null
    private val binding get() = _binding!!

    private val listSoal: ArrayList<GuessQItem> = ArrayList()
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var question: GuessQItem
    private var score = 0
    private var index = 0
    private var indexProgress = 1

    private lateinit var countDownTimer: CountDownTimer
    private var timeLeft: Long = 60000 //1minute per question

    private lateinit var mainActivity : MainActivity

    private val TAG = GuessFragment::class.simpleName

    companion object{
        private const val COLOR_RIGHT = 1
        private const val COLOR_WRONG = 2
        private const val COLOR_DEFAULT = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        _binding = FragmentGuessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        with(binding){
            cardOpsi1.setOnClickListener(this@GuessFragment)
            cardOpsi2.setOnClickListener(this@GuessFragment)
            cardOpsi3.setOnClickListener(this@GuessFragment)
            btnSound.setOnClickListener(this@GuessFragment)
            loader(true)
            observeGuessQ()
        }
    }

    private fun observeGuessQ(){
        with(binding){
            viewModel.questions().observe(viewLifecycleOwner, { response ->
                loader(false)
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            listSoal.addAll(result)
                            prepareQuestions()
                        } else {
                            dataNotFound()
                        }
                    }else{
                        dataNotFound()
                    }
                } else {
                    Log.e(TAG,"data is null")
                }
            })
        }
    }

    private fun prepareQuestions() {
        with(binding){
            // set default view (kembalikan view seperti semula)
            defaultOptionsView()

            question = listSoal.get(index)
            //prepare audio
            mediaPlayer = MediaPlayer()
            val urlAudio = ApiConfig.URL_SOUNDS + question.suara
            prepareMediaPlayer(urlAudio)

            //prepare Option Choices
            tvOpsi1.text = question.opsi1
            tvOpsi2.text = question.opsi2
            tvOpsi3.text = question.opsi3

            //prepare timer,seek bar and score seekBar.setOnTouchListener { view, motionEvent -> true }
            seekBar.max = 0
            seekBar.max = listSoal.size
            seekBar.progress = indexProgress
            tvRight.text = "Benar\n${score}"


            //automatically play sounds and timer
            playAudioVoice()
            startTimer()
        }
    }

    private fun defaultOptionsView() {
        with(binding) {
            val options = ArrayList<MaterialCardView>()
            options.add(cardOpsi1)
            options.add(cardOpsi2)
            options.add(cardOpsi3)

            for (option in options) {
                option.setCardBackgroundColor(color(COLOR_DEFAULT))
            }
        }
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                cardOpsi1 -> checkAnswer(1)
                cardOpsi2 -> checkAnswer(2)
                cardOpsi3 -> checkAnswer(3)
                btnSound -> playAudioVoice()
            }
        }
    }

    private fun checkAnswer(selectedOption: Int) {
        releaseAudio()
        cancelTimer()
        val answeredKey = question.kunciJawaban

        if(answeredKey == selectedOption){
            score = score + 1
        }
        if(answeredKey != selectedOption){
            answerView(selectedOption,false)
        }
        answerView(answeredKey!!,true)
        //delay 1 second for user can see the result (which is wrong and which is right)
        loader(true)
        Handler(Looper.getMainLooper()).postDelayed({
            loader(false)
            if(index != listSoal.size-1 && index < listSoal.size){
                ++index
                ++indexProgress
                prepareQuestions()
            }else{
                moveToResult()
            }
        },500)
    }

    private fun answerView(selectedOption: Int, condition: Boolean) {
        with(binding){
            when(selectedOption){
                1 -> changeViewSelectedOption(cardOpsi1,condition)
                2 -> changeViewSelectedOption(cardOpsi2,condition)
                3 -> changeViewSelectedOption(cardOpsi3,condition)
            }
        }
    }

    private fun changeViewSelectedOption(cardOption: MaterialCardView, condition: Boolean) {
        if(condition)cardOption.setCardBackgroundColor(color(COLOR_RIGHT)) else cardOption.setCardBackgroundColor(color(COLOR_WRONG))
    }

    private fun color(type: Int): Int {
        when(type){
            COLOR_RIGHT -> return ContextCompat.getColor(requireContext(),R.color.colorRight)
            COLOR_WRONG -> return ContextCompat.getColor(requireContext(),R.color.colorWrong)
            else -> return ContextCompat.getColor(requireContext(),R.color.black)
        }
    }

    private fun moveToResult(){
        val toResult = GuessFragmentDirections.actionGuessFragmentToResultFragment().apply {
            totalQuestion = listSoal.size
            correctNumber = score
            type = QUESTION_TYPE
        }
        findNavController().navigate(toResult)
    }

    private fun cancelTimer() {
        countDownTimer.cancel()
    }

    private fun startTimer() {
        with(binding){
            countDownTimer = object : CountDownTimer(timeLeft, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    tvTimer.text = "00:${millisUntilFinished / 1000}"
                }

                override fun onFinish() {
                    showMessage(
                        requireActivity(),
                        "Waktu Habis",
                        "Sayang sekali kesempatanmu untuk menjawab habis",
                        MotionToast.TOAST_WARNING
                    )
                    checkAnswer(0)
                }
            }.start()
        }
    }

    private fun playAudioVoice() {
        mediaPlayer?.start()
    }

    private fun releaseAudio(emptyMediaPlayer: Boolean = true) {
        mediaPlayer?.release()
        if(emptyMediaPlayer)mediaPlayer = null
    }

    private fun prepareMediaPlayer(urlAudio: String) {
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        try {
            mediaPlayer?.setAudioAttributes(attribute)
            mediaPlayer?.setDataSource(urlAudio) // URL music file
            mediaPlayer?.prepare()
        } catch (e: Exception) {
            Log.e(TAG, "prepareMediaPlayer: ${e.message}")
        }
    }

    private fun loader(state: Boolean) {
        with(binding) {
            if (state) {
                pbLoader.visibility = android.view.View.VISIBLE
            } else {
                pbLoader.visibility = android.view.View.GONE
            }
        }
    }

    private fun dataNotFound() {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            layoutEmpty.visibility = View.VISIBLE
            cardOpsi1.visibility = View.INVISIBLE
            cardOpsi2.visibility = View.INVISIBLE
            cardOpsi3.visibility = View.INVISIBLE
            seekBar.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        if(mainActivity != null) mainActivity.mediaPlayer.pause()
    }

    override fun onStop() {
        super.onStop()
        releaseAudio(emptyMediaPlayer = false)
        if(countDownTimer != null) cancelTimer()
        if(mainActivity != null) mainActivity.mediaPlayer.start()
    }
}