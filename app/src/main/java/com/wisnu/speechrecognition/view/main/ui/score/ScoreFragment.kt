package com.wisnu.speechrecognition.view.main.ui.score

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.adapter.MaterialStudyAdapter
import com.wisnu.speechrecognition.adapter.MaterialStudyScoreAdapter
import com.wisnu.speechrecognition.databinding.FragmentMaterialStudyBinding
import com.wisnu.speechrecognition.databinding.FragmentScoreBinding
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyFragmentArgs
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyFragmentDirections
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyViewModel

class ScoreFragment : Fragment() {

    private val viewModel by viewModels<ScoreViewModel>()
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var materyStudyAdapter: MaterialStudyScoreAdapter
    private lateinit var args: ScoreFragmentArgs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = ScoreFragmentArgs.fromBundle(arguments as Bundle)
        val tipeMateri = args.tipeMateriScore
        prepareView(tipeMateri)
    }

    private fun prepareView(tipeMateri: Int) {
        with(binding.score){
            val userId = UserPreference(requireContext()).getUser().id ?: 0
            when(tipeMateri){
                StudyFragment.TIPE_HURUF_AZ -> {
                    observeStudentScores(StudyFragment.TIPE_HURUF_AZ,userId)
                }
                StudyFragment.TIPE_HURUF_KONSONAN -> {
                    observeStudentScores(StudyFragment.TIPE_HURUF_KONSONAN,userId)
                }
                StudyFragment.TIPE_HURUF_VOKAL -> {
                    observeStudentScores(StudyFragment.TIPE_HURUF_VOKAL,userId)
                }
                StudyFragment.TIPE_MEMBACA -> {
                    observeStudentScores(StudyFragment.TIPE_MEMBACA,userId)
                }
            }

            //adapter
            materyStudyAdapter = MaterialStudyScoreAdapter()
            with(rvScore){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = materyStudyAdapter
            }
            val materyType = args.namaTipeScore
            tvScore.text = materyType
            }
        }

    private fun observeStudentScores(tipeMateri: Int,idSiswa: Int) {
        with(binding.score){
            viewModel.studentScores(tipeMateri,idSiswa).observe(viewLifecycleOwner, { response ->
                pbScore.visibility = View.GONE
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            materyStudyAdapter.setData(result)
                        } else {
                            dataNotFound()
                        }
                    }else{
                        dataNotFound()
                    }
                } else {
                    dataNotFound()
                }
            })
        }
    }

    private fun dataNotFound() {
        with(binding.score) {
            val layoutEmpty = layoutEmpty.root
            layoutEmpty.visibility = View.VISIBLE
        }
    }


}