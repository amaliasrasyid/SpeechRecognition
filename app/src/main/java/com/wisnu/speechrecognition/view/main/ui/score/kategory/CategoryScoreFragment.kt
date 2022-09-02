package com.wisnu.speechrecognition.view.main.ui.score.kategory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
    private var id_siswa = 0

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
            btnBack.setOnClickListener(this@CategoryScoreFragment)
        }
        id_siswa = CategoryScoreFragmentArgs.fromBundle(arguments as Bundle).idSiswa
    }

    
    override fun onClick(view: View?) {
        with(binding) {
            if (view?.id == R.id.btn_back) {
                findNavController().navigateUp()
            }

            if (id_siswa == 0) {
                when (view) {
                    cardBelajarHuruf -> {
                        findNavController().navigate(
                            R.id.action_categoryScoreFragment_to_scoreFragment,
                            bundleOf(
                                "tipe_materi_score" to TIPE_HURUF_AZ,
                                "nama_tipe_score" to "Nilai Huruf A-Z"
                            )
                        )
                    }
                    cardBelajarKonsonan -> {
                        findNavController().navigate(
                            R.id.action_categoryScoreFragment_to_scoreFragment,
                            bundleOf(
                                "tipe_materi_score" to TIPE_HURUF_KONSONAN,
                                "nama_tipe_score" to "NIlai Huruf Konsonan"
                            )
                        )
                    }
                    cardBelajarVokal -> {
                        findNavController().navigate(
                            R.id.action_categoryScoreFragment_to_scoreFragment,
                            bundleOf(
                                "tipe_materi_score" to TIPE_HURUF_VOKAL,
                                "nama_tipe_score" to "Nilai Huruf Vokal"
                            )
                        )
                    }
                    cardBelajarMembaca -> {
                        findNavController().navigate(
                            R.id.action_categoryScoreFragment_to_scoreFragment,
                            bundleOf(
                                "tipe_materi_score" to TIPE_MEMBACA,
                                "nama_tipe_score" to "Nilai Membaca Kosakata"
                            )
                        )
                    }
                }
            } else {
                when (view) {
                    cardBelajarHuruf -> {
                        findNavController().navigate(
                            R.id.action_categoryScoreFragment2_to_scoreFragment2,
                            bundleOf(
                                "tipe_materi_score" to TIPE_HURUF_AZ,
                                "nama_tipe_score" to "Nilai Huruf A-Z",
                                "id_siswa" to id_siswa
                            )
                        )
                    }
                    cardBelajarKonsonan -> {
                        findNavController().navigate(
                            R.id.action_categoryScoreFragment2_to_scoreFragment2,
                            bundleOf(
                                "tipe_materi_score" to TIPE_HURUF_KONSONAN,
                                "nama_tipe_score" to "NIlai Huruf Konsonan",
                                "id_siswa" to id_siswa
                            )
                        )
                    }
                    cardBelajarVokal -> {
                        findNavController().navigate(
                            R.id.action_categoryScoreFragment2_to_scoreFragment2,
                            bundleOf(
                                "tipe_materi_score" to TIPE_HURUF_VOKAL,
                                "nama_tipe_score" to "Nilai Huruf Vokal",
                                "id_siswa" to id_siswa
                            )
                        )
                    }
                    cardBelajarMembaca -> {
                        findNavController().navigate(
                            R.id.action_categoryScoreFragment2_to_scoreFragment2,
                            bundleOf(
                                "tipe_materi_score" to TIPE_MEMBACA,
                                "nama_tipe_score" to "Nilai Membaca Kosakata",
                                "id_siswa" to id_siswa
                            )
                        )
                    }
                }

            }
        }
    }
}