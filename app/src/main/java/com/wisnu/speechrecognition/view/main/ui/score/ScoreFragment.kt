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
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_AZ
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_KONSONAN
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_MEMBACA
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyFragmentArgs
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyFragmentDirections
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyViewModel

class ScoreFragment : Fragment() {

    private val viewModel by viewModels<ScoreViewModel>()
    private val viewModel2 by viewModels<MaterialStudyViewModel>()
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var materyStudyScoreAdapter: MaterialStudyScoreAdapter
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
                TIPE_HURUF_AZ -> observeStudentScores(TIPE_HURUF_AZ,userId)
                TIPE_HURUF_KONSONAN -> observeStudentScores(tipeMateri,userId)
                TIPE_HURUF_VOKAL -> observeMaterialStudy(TIPE_HURUF_VOKAL)
                TIPE_MEMBACA -> observeStudentScores(TIPE_MEMBACA,userId)
            }

            //adapter score
            materyStudyScoreAdapter = MaterialStudyScoreAdapter()
            with(rvScore){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = materyStudyScoreAdapter
            }

            tvScore.text = args.namaTipeScore
            materyStudyScoreAdapter.setOnItemClickCallBack(object : MaterialStudyAdapter.OnItemClickCallBack {
                override fun onItemClicked(materyStudy: MateryStudy) {
                    observeStudentScores(materyStudy.tipeMateri,userId,true)
                }
            })
            btnBack.setOnClickListener{findNavController().navigateUp()}
        }
    }

    private fun observeStudentScores(tipeMateri: Int,idSiswa: Int,isVocal: Boolean = false) {
        with(binding.score){
            viewModel.studentScores(tipeMateri,idSiswa).observe(viewLifecycleOwner, { response ->
                pbScore.visibility = View.GONE
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            materyStudyScoreAdapter.setVokal(isVocal)
                            materyStudyScoreAdapter.setData(result)
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

    private fun observeMaterialStudy(materyId: Int) {
        with(binding.score){
            viewModel2.materialStudy(materyId).observe(viewLifecycleOwner, { response ->
                pbScore.visibility = View.GONE
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            materyStudyScoreAdapter.setData(result)
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