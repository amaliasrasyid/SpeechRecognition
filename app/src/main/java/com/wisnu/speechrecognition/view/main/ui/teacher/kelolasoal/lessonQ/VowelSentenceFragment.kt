package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.adapter.QuestionAdapter
import com.wisnu.speechrecognition.databinding.FragmentVowelSentenceBinding
import com.wisnu.speechrecognition.local_db.QuestionStudyClass
import com.wisnu.speechrecognition.model.questions.Question
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.question.QuestionViewModel
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.GuessQFragment
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity.Companion.EXTRA_DATA_QUESTION
import www.sanju.motiontoast.MotionToast

class VowelSentenceFragment : Fragment() {
    private val viewModel by viewModels<QuestionViewModel>()
    private var _binding: FragmentVowelSentenceBinding? = null
    private val binding get() = _binding!!
    private lateinit var questionAdapter: QuestionAdapter

    private var idMatery = 0

    private val TAG = VowelSentenceFragment::class.java.simpleName


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
        idMatery = VowelSentenceFragmentArgs.fromBundle(arguments as Bundle).idMateri
        prepareView()
    }

    private fun prepareView() {
        with(binding){
            observeQuestion(idMatery)
            questionAdapter = QuestionAdapter()
            with(rvVowelQuestions){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = questionAdapter
            }
            questionAdapter.setOnItemBtnDeleteCallBack(object : QuestionAdapter.OnItemBtnDeleteClickCallBack {
                override fun onDeleteClicked(position: Int,question: Question) {
                    questionAdapter.removeData(position)
                    deleteQuestion(question.id)
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
            btnBack.setOnClickListener{
                findNavController().navigateUp()
            }
        }
    }

    private fun observeQuestion(materyId: Int) {
        viewModel.questions(materyId).observe(viewLifecycleOwner) { response ->
            loader(false)
            if (response.data != null) {
                if (!response.data.isEmpty()) {
                    if (response.code == 200) {
                        val results = response.data
                        questionAdapter.setData(results)
                    } else {
                        dataNotFound()
                    }
                } else {
                    dataNotFound()
                }
            } else {
                Log.e(TAG,"data is null")
            }
        }
    }
    
    private fun deleteQuestion(id: Int) {
        viewModel.delete(id).observe(viewLifecycleOwner, { response ->
            loader(false)
            if (response != null) {
                if (response.code == 404) {
                    showMessage(
                        requireActivity(),
                        TITLE_ERROR,
                        response.message ?: "",
                        style = MotionToast.TOAST_ERROR
                    )
                    observeQuestion(idMatery)//ulang data kembali jika gagal hapus di server
                } else {
                    showMessage(
                        requireActivity(),
                        UtilsCode.TITLE_SUCESS,
                        response.message ?: "",
                        style = MotionToast.TOAST_SUCCESS
                    )
                }
            }else{
                showMessage(
                    requireActivity(),
                    TITLE_ERROR,
                    style = MotionToast.TOAST_ERROR
                )
            }
        })
    }

    private fun editQuestion(question: Question) {
        val parcelableQ = QuestionStudyClass(
            question.id,
            question.gambar,
            question.suara,
            question.teksJawaban,
            question.materiPelajaran
        )
        val intent = Intent(requireActivity(),UploadLessonQActivity::class.java)
        intent.apply {
            putExtra(EXTRA_DATA_QUESTION,parcelableQ)
        }
        startActivity(intent)
    }

    private fun dataNotFound() {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            layoutEmpty.visibility = View.VISIBLE
        }
    }
    
    private fun loader(state: Boolean) {
        with(binding) {
            if (state) {
                pbLoader.visibility = android.view.View.VISIBLE
            } else {
                pbLoader.visibility = android.view.View.GONE
            }
        }
    }
}