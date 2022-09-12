package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity.Companion.EXTRA_DATA_MATERY_ID
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity.Companion.EXTRA_DATA_QUESTION
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity.Companion.TYPE_VOWEL
import www.sanju.motiontoast.MotionToast

class VowelSentenceFragment : Fragment(), SearchView.OnQueryTextListener {
    private val viewModel by viewModels<QuestionViewModel>()
    private var _binding: FragmentVowelSentenceBinding? = null
    private val binding get() = _binding!!
    private lateinit var questionAdapter: QuestionAdapter
    private val listQuestion = ArrayList<Question>()

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

            //search view
            searchview.clearFocus()
            searchview.setOnQueryTextListener(this@VowelSentenceFragment)
            //hide back and title when searchview clicked
            searchview.setOnSearchClickListener{
                btnBack.visibility = View.GONE
                tvMaterialStudy.visibility = View.GONE
            }
            //show back and title when searchview clicked
            searchview.setOnCloseListener(object: SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    btnBack.visibility = View.VISIBLE
                    tvMaterialStudy.visibility = View.VISIBLE
                    return false //karna aku ingin fungsi close berjalan seperti biasa
                }
            })

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
                intent.putExtra(EXTRA_DATA_MATERY_ID,idMatery)
                intent.putExtra(TYPE_VOWEL,true)
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
                        listQuestion.addAll(results)
                        dataNotFound(false)
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
            putExtra(EXTRA_DATA_MATERY_ID,parcelableQ.materiPelajaran)
        }
        startActivity(intent)
    }

    private fun filterList(text: String?) {
        val filteredlist = ArrayList<Question>()

        for (item in listQuestion) {
            if (item.teksJawaban.uppercase().contains(text!!.uppercase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(requireActivity(),"Tidak ada data ditemukan", Toast.LENGTH_SHORT).show()
        } else {
            questionAdapter.setFilteredList(filteredlist)
        }
    }

    private fun dataNotFound(state :Boolean = true) {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            if(state){
                layoutEmpty.visibility = View.VISIBLE
            }else{
                layoutEmpty.visibility = View.GONE
            }
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

    override fun onResume() {
        super.onResume()
        observeQuestion(idMatery)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filterList(newText)
        return true
    }
}