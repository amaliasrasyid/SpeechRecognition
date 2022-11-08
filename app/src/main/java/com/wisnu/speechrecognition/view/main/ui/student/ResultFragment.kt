package com.wisnu.speechrecognition.view.main.ui.student

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentResultBinding
import com.wisnu.speechrecognition.data.model.student.Data
import com.wisnu.speechrecognition.data.model.student.StudentScoreResponse
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.UtilsCode.TIPE_BERMAIN_TEBAK_KATA
import com.wisnu.speechrecognition.utils.UtilsCode.TIPE_BERMAIN_TEMUKAN_PASANGAN
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.score.ScoreViewModel
import www.sanju.motiontoast.MotionToast

class ResultFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private var score: Int = 0
    private var gameType: Int = 0

    private val viewModelScore by viewModels<ScoreViewModel>()

    companion object{
        const val QUESTION_TYPE = 1
        const val PAIR_TYPE = 2
        const val GET_SCORE_GUESS_GAME = 400
        const val GET_SCORE_PAIR_GAME = 600
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        with(binding) {
            val args = ResultFragmentArgs.fromBundle(arguments as Bundle)
            val type = args.type

            if(type == PAIR_TYPE || type == QUESTION_TYPE){
                val correctNumber = args.correctNumber
                val totalQ = args.totalQuestion
                val userName = UserPreference(requireContext()).getUser().nama
                score = ((correctNumber.toDouble()/totalQ) * 100).toInt()
                showImageResult(score)
                when(type){
                    QUESTION_TYPE -> {
                        tvScore.text = "Kamu menjawab ${correctNumber} soal dengan benar dari total ${totalQ} soal"
                        gameType = QUESTION_TYPE
                    }
                    PAIR_TYPE -> {
                        tvScore.text = "Kamu memasangkan pasangan dengan benar sebanyak ${correctNumber} dari total ${totalQ} pasangan"
                        gameType = PAIR_TYPE
                    }
                }
            }else{
                btnFinish.visibility = View.GONE
                btnBack.visibility = View.VISIBLE
                loader(true)
                val studentId = args.idSiswa
                when(type){
                    GET_SCORE_GUESS_GAME -> {
                        getGameScore(TIPE_BERMAIN_TEBAK_KATA,studentId)
                    }
                    GET_SCORE_PAIR_GAME -> {
                        getGameScore(TIPE_BERMAIN_TEMUKAN_PASANGAN,studentId)
                    }
                }
            }
            btnFinish.setOnClickListener(this@ResultFragment)
            btnBack.setOnClickListener(this@ResultFragment)
        }
    }

    private fun showImageResult(score: Int) {
        with(binding){
            when (score){
                in 0..55 -> {
                    imgFail.visibility = View.VISIBLE
                    imgCongrat.visibility = View.GONE
                }
                in 56..100 -> {
                    imgCongrat.visibility = View.VISIBLE
                    imgFail.visibility = View.GONE
                }
            }
            var conclusion = resultConclusion(score)
            tvResultConclusion.text = conclusion
        }
    }

    private fun storeGameScore() {
        val userId = UserPreference(requireActivity()).getUser().id

        val studentScore = hashMapOf<String,Any>()
        studentScore["id_siswa"] = userId!!
        studentScore["id_tipe_game"] = if(gameType == QUESTION_TYPE) TIPE_BERMAIN_TEBAK_KATA else TIPE_BERMAIN_TEMUKAN_PASANGAN
        studentScore["id_soal"] = 0
        studentScore["nilai"] = score
        viewModelScore.storeScore(studentScore).observe(viewLifecycleOwner, { response ->
            loader(false)
            if (response.data != null) {
                if (response.code == 200) {
                    showMessage(
                        requireActivity(),
                        UtilsCode.TITLE_SUCESS,
                        response.message ?: "",
                        style = MotionToast.TOAST_SUCCESS
                    )
                    findNavController().navigate(R.id.action_resultFragment_to_playFragment2)
                } else {
                    showMessage(
                        requireActivity(),
                        UtilsCode.TITLE_ERROR,
                        response.message ?: "",
                        style = MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(
                    requireActivity(),
                    UtilsCode.TITLE_ERROR,
                    style = MotionToast.TOAST_ERROR
                )
            }
        })
    }

    private fun getGameScore(gametypeId: Int, studentId: Int) {
        viewModelScore.studentGameScore(gametypeId,studentId).observe(viewLifecycleOwner, { response ->
            loader(false)
            if (response.data != null) {
                if (response.code == 200) {
                   prepareViewWithData(response.data)
                } else {
                   dataNotFound()
                }
            } else {
               dataNotFound()
            }
        })
    }

    private fun prepareViewWithData(data: Data) {
        score = data.nilai ?: 0
        showImageResult(score)
        binding.tvScore.text = "Dengan skor nilai yang didapat adalah ${score}/100"
    }

    private fun resultConclusion(score: Int): String {
        var mString = "-"
        when (score) {
            in 0..30 -> {
                mString = "Sangat Tidak Memuaskan"
            }
            in 31..50 -> {
                mString = "Tidak Memuaskan"
            }
            in 56..70 -> {
                mString = "Cukup Memuaskan"
            }
            in 71..85 -> {
                mString = "Memuaskan"
            }
            in 86..100 -> {
                mString = "Sangat Memuaskan"
            }
        }
        return mString
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                btnFinish -> {
                    loader(true)
                    storeGameScore()
                }
                btnBack -> {
                    findNavController().navigateUp()
                }
                else -> {}
            }
        }
    }

    private fun dataNotFound() {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            layoutEmpty.visibility = android.view.View.VISIBLE
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

}