package com.wisnu.speechrecognition.view.main.ui.student.study.material_study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.adapter.MaterialStudyAdapter
import com.wisnu.speechrecognition.adapter.MaterialStudyScoreAdapter
import com.wisnu.speechrecognition.databinding.FragmentMaterialStudyBinding
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_AZ
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_KONSONAN
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_MEMBACA
import www.sanju.motiontoast.MotionToast

class MaterialStudyFragment : Fragment() {

    companion object {
        fun newInstance() = MaterialStudyFragment()
    }

    private val viewModel by viewModels<MaterialStudyViewModel>()
    private var _binding: FragmentMaterialStudyBinding? = null
    private val binding get() = _binding!!
    private lateinit var materyAdapter: MaterialStudyScoreAdapter
    private lateinit var args: MaterialStudyFragmentArgs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMaterialStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = MaterialStudyFragmentArgs.fromBundle(arguments as Bundle)
        val tipeMateri = args.tipeMateri
        prepareView(tipeMateri)
    }

    private fun prepareView(tipeMateri: Int) {
        with(binding){
            when(tipeMateri){
                TIPE_HURUF_AZ -> {
                    observeMaterialStudy(TIPE_HURUF_AZ)
                }
                TIPE_HURUF_KONSONAN -> {
                    observeMaterialStudy(TIPE_HURUF_KONSONAN)
                }
                TIPE_HURUF_VOKAL -> {
                    observeMaterialStudy(TIPE_HURUF_VOKAL)
                }
                TIPE_MEMBACA -> {
                    observeMaterialStudy(TIPE_MEMBACA)
                }
            }

            //adapter
            materyAdapter = MaterialStudyScoreAdapter()
            with(rvMaterialStudy){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = materyAdapter
            }
            val materyType = args.namaTipe
            tvMaterialStudy.text = materyType
            btnBack.setOnClickListener{findNavController().navigateUp()}

            materyAdapter.setOnItemClickCallBack(object : MaterialStudyAdapter.OnItemClickCallBack {
                override fun onItemClicked(materyStudy: MateryStudy) {
                    // move intent and send id chapter
                    val toQuestion = MaterialStudyFragmentDirections.actionMaterialStudyFragmentToQuestionFragment().apply {
                        idMateriBelajar = materyStudy.id
                        tipeMateriBelajar = materyStudy.tipeMateri
                    }
                    findNavController().navigate(toQuestion)
                }
            })
        }

    }


    private fun observeMaterialStudy(materyId: Int) {
        with(binding){
            viewModel.materialStudy(materyId).observe(viewLifecycleOwner, { response ->
                pbMatery.visibility = View.GONE
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            materyAdapter.setData(result)
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

    //TODO: sebaiknya pindah ke util karna dipkai multiple diberbagai class
    private fun dataNotFound() {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            layoutEmpty.visibility = View.VISIBLE
        }
    }


}