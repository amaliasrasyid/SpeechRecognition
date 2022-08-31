package com.wisnu.speechrecognition.view.main.ui.score.kategory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentCategoryScoreBinding
import com.wisnu.speechrecognition.databinding.FragmentStudyBinding
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_AZ
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_KONSONAN
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_MEMBACA
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragmentDirections


class CategoryScoreFragment : Fragment(), View.OnClickListener  {


    private var _binding: FragmentCategoryScoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        with(binding){
            cardBelajarHuruf.setOnClickListener(this@CategoryScoreFragment)
            cardBelajarKonsonan.setOnClickListener(this@CategoryScoreFragment)
            cardBelajarVokal.setOnClickListener(this@CategoryScoreFragment)
            cardBelajarMembaca.setOnClickListener(this@CategoryScoreFragment)
        }
    }

    

    override fun onClick(view: View?) {
        with(binding){
            when (view) {
                cardBelajarHuruf -> {
                    val toLetters = CategoryScoreFragmentDirections.actionCategoryScoreFragmentToScoreFragment()
                            .apply {
                                tipeMateriScore = TIPE_HURUF_AZ
                                namaTipeScore = "Nilai Huruf A-Z"
                            }
                    findNavController().navigate(toLetters)
                }
                cardBelajarKonsonan -> {
                    val toConsonant = CategoryScoreFragmentDirections.actionCategoryScoreFragmentToScoreFragment()
                            .apply {
                                tipeMateriScore = TIPE_HURUF_KONSONAN
                                namaTipeScore = "NIlai Huruf Konsonan"
                            }
                    findNavController().navigate(toConsonant)
                }
                cardBelajarVokal -> {
                    val toVowel = CategoryScoreFragmentDirections.actionCategoryScoreFragmentToScoreFragment()
                            .apply {
                                tipeMateriScore = TIPE_HURUF_VOKAL
                                namaTipeScore = "Nilai Huruf Vokal"
                            }
                    findNavController().navigate(toVowel)
                }
                cardBelajarMembaca -> {
                    val toVocabReading = CategoryScoreFragmentDirections.actionCategoryScoreFragmentToScoreFragment()
                            .apply {
                                tipeMateriScore = TIPE_MEMBACA
                                namaTipeScore = "Nilai Membaca Kosakata"
                            }
                    findNavController().navigate(toVocabReading)
                }
            }

        }
    }
}