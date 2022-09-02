package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.adapter.MateryAdapter
import com.wisnu.speechrecognition.databinding.FragmentMateryBinding
import com.wisnu.speechrecognition.local_db.QuestionClass
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.model.questions.Question
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.category.CategoryFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity
import www.sanju.motiontoast.MotionToast

class MateryFragment : Fragment() {

    private val viewModel by viewModels<MateryViewModel>()
    private var _binding: FragmentMateryBinding? = null
    private val binding get() = _binding!!
    private lateinit var materyAdapter: MateryAdapter
    private lateinit var args: MateryFragmentArgs

    companion object {
        fun newInstance() = MateryFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMateryBinding.inflate(inflater, container, false)
        return binding.root    
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = MateryFragmentArgs.fromBundle(arguments as Bundle)
        val tipeMateri = args.tipeMateri
        prepareView(tipeMateri)
    }

    private fun prepareView(tipeMateri: Int) {
        with(binding){
            when(tipeMateri){
                StudyFragment.TIPE_HURUF_AZ -> {
                    observeMaterialStudy(StudyFragment.TIPE_HURUF_AZ)
                }
                StudyFragment.TIPE_HURUF_KONSONAN -> {
                    observeMaterialStudy(StudyFragment.TIPE_HURUF_KONSONAN)
                }
                StudyFragment.TIPE_HURUF_VOKAL -> {
                    observeMaterialStudy(StudyFragment.TIPE_HURUF_VOKAL)
                }
                StudyFragment.TIPE_MEMBACA -> {
                    observeMaterialStudy(StudyFragment.TIPE_MEMBACA)
                }
            }
            //adapter
            materyAdapter = MateryAdapter()
            with(rvMaterialStudy){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = materyAdapter
            }
            val materyType = args.namaTipe
            tvMaterialStudy.text = materyType
            btnBack.setOnClickListener{findNavController().navigateUp()}

            materyAdapter.setOnItemClickCallBack(object : MateryAdapter.OnItemClickCallBack {
                override fun onItemClicked(materyStudy: MateryStudy) {
                    //siapkan data soal
                    getQuestions(materyStudy)
                }
            })
            materyAdapter.setOnItemBtnDeleteCallBack(object : MateryAdapter.OnItemBtnDeleteClickCallBack {
                override fun onDeleteClicked(position: Int,materyStudy: MateryStudy) {
                    deleteMatery(position,materyStudy.id)
                }
            })
            materyAdapter.setOnItemBtnEditCallBack(object : MateryAdapter.OnItemBtnEditClickCallBack {
                override fun onEditClicked(materyStudy: MateryStudy) {
                   editMatery(materyStudy)
                }
            })
            fabAddMatery.setOnClickListener{
                val toUploadMatery = MateryFragmentDirections.actionMateryFragmentToUploadMateryFragment().apply {
                    tipeMateriData = tipeMateri
                }
                findNavController().navigate(toUploadMatery)
            }
        }

    }

    private fun editMatery(materyStudy: MateryStudy) {
        val toUploadMatery = MateryFragmentDirections.actionMateryFragmentToUploadMateryFragment().apply {
            tipeMateriData = materyStudy.tipeMateri
            idData = materyStudy.id
            teksMateri = materyStudy.teksMateri
        }
        findNavController().navigate(toUploadMatery)
    }

    private fun deleteMatery(position: Int, materyId: Int) {
        with(binding){
            viewModel.delete(materyId).observe(viewLifecycleOwner, { response ->
                pbMatery.visibility = View.GONE
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            materyAdapter.removeData(position)
                        } else {
                            showMessage(
                                requireActivity(),
                                TITLE_ERROR,
                                response.message ?: "",
                                style = MotionToast.TOAST_ERROR
                            )
                        }
                    }else{
                        showMessage(
                            requireActivity(),
                            TITLE_ERROR,
                            response.message?: "",
                            style = MotionToast.TOAST_ERROR
                        )
                    }
                } else {
                    showMessage(
                        requireActivity(),
                        TITLE_ERROR,
                        style = MotionToast.TOAST_ERROR
                    )
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

    private fun getQuestions(materyStudy: MateryStudy) { //PASTI SATU KECUALI VOKAL
        viewModel.questions(materyStudy.id).observe(viewLifecycleOwner) { response ->
            binding.pbMatery.visibility = View.GONE
            if (response.data != null) {
                if (!response.data.isEmpty()) {
                    if (response.code == 200) {
                        val results = response.data
                        passDataToForm(results,materyStudy)
                    } else {
                        showMessage(
                            requireActivity(),
                            TITLE_ERROR,
                            response.message ?: "",
                            style = MotionToast.TOAST_ERROR
                        )
                    }
                } else {
                    showMessage(
                        requireActivity(),
                        TITLE_ERROR,
                        response.message ?: "",
                        style = MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(
                    requireActivity(),
                    TITLE_ERROR,
                    style = MotionToast.TOAST_ERROR
                )
            }
        }
    }

    private fun passDataToForm(results: List<Question>, item: MateryStudy) {
        // move intent and send id chapter
        when(item.tipeMateri){
            TIPE_HURUF_VOKAL -> {
                val toVowelSentence = MateryFragmentDirections.actionMateryFragmentToVowelSentenceFragment().apply {
                    idMateri = item.id
                }
                findNavController().navigate(toVowelSentence)
            }
            else ->{
                val resultQ = results.get(0) //hanya vokal yg punya 1:M relasi
                val question = QuestionClass(
                    resultQ.id,
                    resultQ.gambar,
                    resultQ.suara,
                    resultQ.teksJawaban,
                    resultQ.materiPelajaran
                )
                val intent = Intent(requireActivity(),UploadLessonQActivity::class.java)
                intent.apply {
                    putExtra("question",question)
                }
                startActivity(intent)
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