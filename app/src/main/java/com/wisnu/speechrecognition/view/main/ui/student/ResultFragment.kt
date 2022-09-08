package com.wisnu.speechrecognition.view.main.ui.student

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentResultBinding
import com.wisnu.speechrecognition.session.UserPreference

class ResultFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    companion object{
        const val QUESTION_TYPE = 1
        const val PAIR_TYPE = 2
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
//        TODO: Tampilkan hasil skor dan ucapan2 plus2
        val args = ResultFragmentArgs.fromBundle(arguments as Bundle)
        val correctNumber = args.correctNumber
        val totalQ = args.totalQuestion
        val type = args.type
        val userName = UserPreference(requireContext()).getUser().nama
        val score = ((correctNumber.toDouble()/totalQ) * 100).toInt()
        var conclusion = resultConclusion(score)

        with(binding) {
//            tvName.text = userName
            tvResultConclusion.text = conclusion
            when (type){
                QUESTION_TYPE -> {
                    tvScore.text = "Kamu menjawab ${correctNumber} soal dengan benar dari total ${totalQ} soal"
                }
                PAIR_TYPE -> {
                    tvScore.text = "Kamu memasangkan pasangan dengan benar sebanyak ${correctNumber} dari total ${totalQ} pasangan"
                }
                else -> Log.d(TAG,"tipe data yg dikirim tidak diketahui")
            }
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
            btnFinish.setOnClickListener(this@ResultFragment)
            btnClose.setOnClickListener(this@ResultFragment)
        }

    }

    private fun resultConclusion(score: Int): String {
        var mString = "-"
        //TODO: ubah warna teks nilai sesuai tingkatannya
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
                btnFinish,btnClose -> {
                    findNavController().navigate(R.id.action_resultFragment_to_playFragment2)
                }
            }
        }
    }

}