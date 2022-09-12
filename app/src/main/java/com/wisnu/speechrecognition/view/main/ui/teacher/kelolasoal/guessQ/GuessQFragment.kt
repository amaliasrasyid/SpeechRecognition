package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ

import android.content.ContentValues.TAG
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.adapter.GuessQAdapter
import com.wisnu.speechrecognition.databinding.FragmentGuessQBinding
import com.wisnu.speechrecognition.local_db.QuestionPlayGuess
import com.wisnu.speechrecognition.model.questions.GuessQItem
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_SUCESS
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.student.play.guess.GuessFragment
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.upload.UploadGuessQActivity
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.upload.UploadGuessQActivity.Companion.EXTRA_DATA_QUESTION
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.upload.UploadGuessQActivity.Companion.REQUEST_ADD
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.upload.UploadGuessQActivity.Companion.REQUEST_EDIT
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.upload.UploadGuessQActivity.Companion.TYPE
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.MateryFragment
import www.sanju.motiontoast.MotionToast

class GuessQFragment : Fragment(), SearchView.OnQueryTextListener {
    private val viewModel by viewModels<GuessQViewModel>()
    private var _binding: FragmentGuessQBinding? = null
    private val binding get() = _binding!!
    private lateinit var guessQAdapter: GuessQAdapter
    private var listGuessQ = ArrayList<GuessQItem>()

    private val TAG = GuessQFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuessQBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        with(binding){
            observeGuessQ()
            guessQAdapter = GuessQAdapter()
            with(rvMaterialStudy){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = guessQAdapter
            }
            //search view
            searchview.clearFocus()
            searchview.setOnQueryTextListener(this@GuessQFragment)
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

            guessQAdapter.setOnItemBtnDeleteCallBack(object : GuessQAdapter.OnItemBtnDeleteClickCallBack {
                override fun onDeleteClicked(position: Int, question: GuessQItem) {
                    guessQAdapter.removeData(position)
                    deleteGuessQ(question.id)
                    Log.d(TAG,"posisi item-${position}")
                }
            })
            guessQAdapter.setOnItemBtnEditCallBack(object : GuessQAdapter.OnItemBtnEditClickCallBack {
                override fun onEditClicked(item: GuessQItem) {
                    val question = QuestionPlayGuess(
                        id = item.id,
                        suara = item.suara,
                        opsi1 = item.opsi1,
                        opsi2 = item.opsi2,
                        opsi3 = item.opsi3,
                        kunciJawaban =  item.kunciJawaban,
                    )
                    val intent = Intent(requireActivity(), UploadGuessQActivity::class.java)
                    intent.apply {
                        putExtra(TYPE,REQUEST_EDIT)
                        putExtra(EXTRA_DATA_QUESTION,question)
                    }
                    startActivity(intent)
                }
            })
            fabAddMatery.setOnClickListener{
                val intent = Intent(requireActivity(), UploadGuessQActivity::class.java)
                intent.apply {
                    putExtra(TYPE,REQUEST_ADD)
                }
                startActivity(intent)
            }
        }
    }

    private fun observeGuessQ(){
        with(binding){
            viewModel.questions().observe(viewLifecycleOwner, { response ->
                pbMatery.visibility = View.GONE
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            guessQAdapter.setData(result)
                            listGuessQ.clear()
                            listGuessQ.addAll(result)

                            dataNotFound(false)
                        } else {
                            dataNotFound()
                        }
                    }else{
                        dataNotFound()
                    }
                } else {
                    Log.e(TAG,"data is null")
                }
            })
        }
    }

    private fun deleteGuessQ(id: Int) {
        with(binding) {
            viewModel.delete(id).observe(viewLifecycleOwner) { response ->
                pbMatery.visibility = View.GONE
                if (response != null) {
                    if (response.code == 404) {
                        showMessage(
                            requireActivity(),
                            TITLE_ERROR,
                            response.message ?: "",
                            style = MotionToast.TOAST_ERROR
                        )
                        observeGuessQ()//ulang data kembali jika gagal hapus di server
                    } else {
                        showMessage(
                            requireActivity(),
                            TITLE_SUCESS,
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
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filterList(newText)
        return true
    }

    private fun filterList(text: String?) {
        val filteredlist = ArrayList<GuessQItem>()

        for (item in listGuessQ) {
            if (item.opsi1!!.uppercase().contains(text!!.uppercase())) {
                filteredlist.add(item)
            }else if (item.opsi2!!.uppercase().contains(text!!.uppercase())) {
                filteredlist.add(item)
            }else if (item.opsi3!!.uppercase().contains(text!!.uppercase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(requireActivity(),"Tidak ada data ditemukan", Toast.LENGTH_SHORT).show()
        } else {
            guessQAdapter.setFilteredList(filteredlist)
        }
    }

    private fun find(list: List<Any>, view :View): Boolean {
        return list.filter{it == view}.isNotEmpty()
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

    override fun onResume() {
        super.onResume()
        Log.d("GuessQFragment","onresume")
        Log.d("ListGuessQ",listGuessQ.toString())
        observeGuessQ()
    }
}