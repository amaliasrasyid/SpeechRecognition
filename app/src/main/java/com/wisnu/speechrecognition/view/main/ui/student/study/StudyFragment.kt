package com.wisnu.speechrecognition.view.main.ui.student.study

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentStudyBinding

class StudyFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = StudyFragment()
        const val TIPE_HURUF_AZ = 1
        const val TIPE_HURUF_KONSONAN = 2
        const val TIPE_HURUF_VOKAL = 3
        const val TIPE_MEMBACA = 4
    }
    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        with(binding){
            cardBelajarHuruf.setOnClickListener(this@StudyFragment)
            cardBelajarKonsonan.setOnClickListener(this@StudyFragment)
            cardBelajarVokal.setOnClickListener(this@StudyFragment)
            cardBelajarMembaca.setOnClickListener(this@StudyFragment)
            btnBack.setOnClickListener(this@StudyFragment)
        }
        //passing data dg observer pattern (live data)
    }

    override fun onClick(view: View?) {
        with(binding){
            if(view?.id == R.id.btn_back){
                findNavController().navigateUp()
            }
            when (view) {
                cardBelajarHuruf -> {
                    val toLetters =
                        StudyFragmentDirections.actionStudyFragmentToMaterialStudyFragment()
                            .apply {
                                tipeMateri = TIPE_HURUF_AZ
                                namaTipe = "Huruf A-Z"
                            }
                    findNavController().navigate(toLetters)
                }
                cardBelajarKonsonan -> {
                    val toConsonant =
                        StudyFragmentDirections.actionStudyFragmentToMaterialStudyFragment()
                            .apply {
                                tipeMateri = TIPE_HURUF_KONSONAN
                                namaTipe = "Huruf Konsonan"
                            }
                    findNavController().navigate(toConsonant)
                }
                cardBelajarVokal -> {
                    val toVowel =
                        StudyFragmentDirections.actionStudyFragmentToMaterialStudyFragment()
                            .apply {
                                tipeMateri = TIPE_HURUF_VOKAL
                                namaTipe = "Huruf Vokal"
                            }
                    findNavController().navigate(toVowel)
                }
                cardBelajarMembaca -> {
                    val toVocabReading =
                        StudyFragmentDirections.actionStudyFragmentToMaterialStudyFragment()
                            .apply {
                                tipeMateri = TIPE_MEMBACA
                                namaTipe = "Membaca Kosakata"
                            }
                    findNavController().navigate(toVocabReading)
                }
            }

        }
    }
}