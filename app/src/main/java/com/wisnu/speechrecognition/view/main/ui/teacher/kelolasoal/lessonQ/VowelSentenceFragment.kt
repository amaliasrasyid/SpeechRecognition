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
import com.wisnu.speechrecognition.adapter.QuestionAdapter
import com.wisnu.speechrecognition.databinding.FragmentVowelSentenceBinding
import com.wisnu.speechrecognition.local_db.QuestionClass
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.model.questions.Question
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.category.CategoryFragment
import com.wisnu.speechrecognition.view.main.ui.question.QuestionViewModel
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity
import www.sanju.motiontoast.MotionToast

class VowelSentenceFragment : Fragment() {
    private val viewModel by viewModels<QuestionViewModel>()
    private var _binding: FragmentVowelSentenceBinding? = null
    private val binding get() = _binding!!
    private lateinit var questionAdapter: QuestionAdapter
    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVowelSentenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idMatery = VowelSentenceFragmentArgs.fromBundle(arguments as Bundle).idMateri
        prepareView(idMatery)
    }

    private fun prepareView(idMatery: Int) {
        with(binding){
            getQuestions(idMatery)
            questionAdapter = QuestionAdapter()
            with(rvVowelQuestions){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = questionAdapter
            }
            questionAdapter.setOnItemBtnDeleteCallBack(object : QuestionAdapter.OnItemBtnDeleteClickCallBack {
                override fun onDeleteClicked(position: Int,question: Question) {
                    deleteQuestion(position,question.id)
                }
            })
            questionAdapter.setOnItemBtnEditCallBack(object : QuestionAdapter.OnItemBtnEditClickCallBack {
                override fun onEditClicked(question: Question) {
                    editQuestion(question)
                }
            })
            fabAddVowelQ.setOnClickListener{
                val intent = Intent(requireActivity(),UploadLessonQActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getQuestions(materyId: Int) {
        viewModel.questions(materyId).observe(viewLifecycleOwner) { response ->
            binding.pbMatery.visibility = View.GONE
            if (response.data != null) {
                if (!response.data.isEmpty()) {
                    if (response.code == 200) {
                        val results = response.data
                        questionAdapter.setData(results)
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
        }
    }

    
    private fun deleteQuestion(position: Int, id: Int) {
        viewModel.delete(id).observe(viewLifecycleOwner, { response ->
            binding.pbMatery.visibility = View.GONE
            if (response.data != null) {
                if(!response.data.isEmpty()){
                    if (response.code == 200) {
                        questionAdapter.removeData(position)
                    } else {
                        showMessage(
                            requireActivity(),
                            UtilsCode.TITLE_ERROR,
                            response.message ?: "",
                            style = MotionToast.TOAST_ERROR
                        )
                    }
                }else{
                    showMessage(
                        requireActivity(),
                        UtilsCode.TITLE_ERROR,
                        response.message?: "",
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

    private fun editQuestion(question: Question) {
        val parcelableQ = QuestionClass(
            question.id,
            question.gambar,
            question.suara,
            question.teksJawaban,
            question.materiPelajaran
        )
        val intent = Intent(requireActivity(),UploadLessonQActivity::class.java)
        intent.apply {
            putExtra("question",parcelableQ)
        }
        startActivity(intent)
    }
}