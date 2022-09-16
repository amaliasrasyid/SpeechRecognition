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
import com.wisnu.speechrecognition.local_db.User
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_ADMIN
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_GURU
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_SISWA
import com.wisnu.speechrecognition.utils.UtilsCode.TIPE_BERMAIN_TEBAK_KATA
import com.wisnu.speechrecognition.utils.UtilsCode.TIPE_BERMAIN_TEMUKAN_PASANGAN
import com.wisnu.speechrecognition.view.main.ui.student.ResultFragment.Companion.GET_SCORE_GUESS_GAME
import com.wisnu.speechrecognition.view.main.ui.student.ResultFragment.Companion.GET_SCORE_PAIR_GAME
import com.wisnu.speechrecognition.view.main.ui.student.ResultFragment.Companion.PAIR_TYPE
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
    private lateinit var preference :User

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
            cardBermainTebakKata.setOnClickListener(this@CategoryScoreFragment)
            cardBermainTemukanPasangan.setOnClickListener(this@CategoryScoreFragment)
            btnBack.setOnClickListener(this@CategoryScoreFragment)
        }
        preference = UserPreference(requireActivity()).getUser()
        when(preference.role){
            ROLE_ADMIN -> id_siswa = CategoryScoreFragmentArgs.fromBundle(arguments as Bundle).idSiswa
            ROLE_SISWA -> id_siswa = preference.id!!
        }

    }

    
    override fun onClick(view: View?) {
        with(binding) {
            if (view?.id == R.id.btn_back) {
                findNavController().navigateUp()
            }
            when(preference.role){
                ROLE_ADMIN -> {
                    id_siswa = CategoryScoreFragmentArgs.fromBundle(arguments as Bundle).idSiswa
                    when(view){
                        cardBelajarHuruf -> {
                            findNavController().navigate(
                                R.id.action_categoryScoreFragment2_to_scoreFragment2,
                                bundleOf(
                                    "tipe_materi_score" to TIPE_HURUF_AZ,
                                    "nama_tipe_score" to "Nilai Huruf A-Z"
                                )
                            )
                        }
                        cardBelajarKonsonan -> {
                            findNavController().navigate(
                                R.id.action_categoryScoreFragment2_to_scoreFragment2,
                                bundleOf(
                                    "tipe_materi_score" to TIPE_HURUF_KONSONAN,
                                    "nama_tipe_score" to "NIlai Huruf Konsonan"
                                )
                            )
                        }
                        cardBelajarVokal -> {
                            findNavController().navigate(
                                R.id.action_categoryScoreFragment2_to_scoreFragment2,
                                bundleOf(
                                    "tipe_materi_score" to TIPE_HURUF_VOKAL,
                                    "nama_tipe_score" to "Nilai Huruf Vokal"
                                )
                            )
                        }
                        cardBelajarMembaca -> {
                            findNavController().navigate(
                                R.id.action_categoryScoreFragment2_to_scoreFragment2,
                                bundleOf(
                                    "tipe_materi_score" to TIPE_MEMBACA,
                                    "nama_tipe_score" to "Nilai Membaca Kosakata"
                                )
                            )
                        }
                        cardBermainTebakKata -> {
                            findNavController().navigate(
                                R.id.action_categoryScoreFragment2_to_resultFragment2,
                                bundleOf(
                                    "id_siswa" to id_siswa,
                                    "type" to GET_SCORE_GUESS_GAME
                                )
                            )
                        }
                        cardBermainTemukanPasangan -> {
                            findNavController().navigate(
                                R.id.action_categoryScoreFragment2_to_resultFragment2,
                                bundleOf(
                                    "id_siswa" to id_siswa,
                                    "type" to GET_SCORE_PAIR_GAME
                                )
                            )
                        }
                    }
                }
                ROLE_SISWA -> {
                    id_siswa = preference.id!!
                    when(view){
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
                        cardBermainTebakKata -> {
                            findNavController().navigate(
                                R.id.action_categoryScoreFragment_to_resultFragment,
                                bundleOf(
                                    "id_siswa" to id_siswa,
                                    "type" to GET_SCORE_GUESS_GAME
                                )
                            )
                        }
                        cardBermainTemukanPasangan -> {
                            findNavController().navigate(
                                R.id.action_categoryScoreFragment_to_resultFragment,
                                bundleOf(
                                    "id_siswa" to id_siswa,
                                    "type" to GET_SCORE_PAIR_GAME
                                )
                            )
                        }
                    }
                }
            }


        }
    }
}