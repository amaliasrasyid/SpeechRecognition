package com.wisnu.speechrecognition.view.main.ui.student.play.guess

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.adapter.GuessQAdapter
import com.wisnu.speechrecognition.databinding.FragmentGuessBinding
import com.wisnu.speechrecognition.model.questions.GuessQItem
import com.wisnu.speechrecognition.model.questions.Question
import com.wisnu.speechrecognition.model.questions.QuestionStudyResponse
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.question.QuestionFragment
import com.wisnu.speechrecognition.view.main.ui.student.ResultFragment.Companion.QUESTION_TYPE
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.GuessQViewModel
import www.sanju.motiontoast.MotionToast

class GuessFragment : Fragment(), View.OnClickListener {

    private val viewModel by viewModels<GuessQViewModel>()
    private var _binding: FragmentGuessBinding? = null
    private val binding get() = _binding!!

    private lateinit var guessQAdapter: GuessQAdapter
    private val listSoal: ArrayList<GuessQItem> = ArrayList()
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var question: GuessQItem
    private var score = 0
    private var index = 0
    private var indexProgress = 1

    private lateinit var countDownTimer: CountDownTimer
    private var timeLeft: Long = 60000 //1minute per question


    private val TAG = GuessFragment::class.simpleName

    companion object {
        fun newInstance() = GuessFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            question = listSoal.get(index)
            //prepare audio
            mediaPlayer = MediaPlayer()
            val urlAudio = ApiConfig.URL_SOUNDS + question.suara
            prepareMediaPlayer(urlAudio)

            //prepare Option Choices
            tvOpsi1.text = question.opsi1
            tvOpsi2.text = question.opsi2
            tvOpsi3.text = question.opsi3

            //prepare timer,seek bar and score
//            var currentProgress = (indexProgress.toDouble()/listSoal.size)*100
            seekBar.setOnTouchListener { view, motionEvent -> true }
            seekBar.max = 0
            seekBar.max = listSoal.size
            seekBar.progress = indexProgress
            tvRight.text = "Benar\n${score}"


            //automatically play sounds and timer
            playAudioVoice()
            startTimer()
        }
    }
    override fun onClick(view: View?) {
        with(binding){
            when(view){
                cardOpsi1 -> checkAnswer(1)
                cardOpsi2 -> checkAnswer(2)
                cardOpsi3 -> checkAnswer(3)
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
        if(index != listSoal.size-1 && index < listSoal.size){
            ++index
            ++indexProgress
            prepareQuestions()
        }else{
            //TODO: buka tampilan result fragment
            val toResult = GuessFragmentDirections.actionGuessFragmentToResultFragment().apply {
                totalQuestion = listSoal.size
                correctNumber = score
                type = QUESTION_TYPE
            }
            findNavController().navigate(toResult)
        }

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

    private fun releaseAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun prepareMediaPlayer(urlAudio: String) {
        try {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
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
        }
    }



}