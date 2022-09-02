package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ

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
import com.wisnu.speechrecognition.adapter.MateryAdapter
import com.wisnu.speechrecognition.databinding.FragmentGuessBinding
import com.wisnu.speechrecognition.databinding.FragmentGuessQBinding
import com.wisnu.speechrecognition.databinding.FragmentMateryBinding
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.MateryFragmentArgs
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.MateryFragmentDirections
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.MateryViewModel

class GuessQFragment : Fragment() {
    private val viewModel by viewModels<GuessQViewModel>()
    private var _binding: FragmentGuessQBinding? = null
    private val binding get() = _binding!!
    private lateinit var materyAdapter: MateryAdapter

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
            materyAdapter = MateryAdapter()
            with(rvMaterialStudy){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = materyAdapter
            }
            materyAdapter.setOnItemBtnDeleteCallBack(object : MateryAdapter.OnItemBtnDeleteClickCallBack {
                override fun onDeleteClicked(position: Int,materyStudy: MateryStudy) {
                    deleteGuessQ(position,materyStudy.id)
                }
            })
            materyAdapter.setOnItemBtnEditCallBack(object : MateryAdapter.OnItemBtnEditClickCallBack {
                override fun onEditClicked(materyStudy: MateryStudy) {
                    //siapkan data soal
                    val toUploadGuessQ = GuessQFragmentDirections.actionGuessQFragmentToUploadGuessQFragment().apply {
                        idSoal = materyStudy.id
                    }
                    findNavController().navigate(toUploadGuessQ)
                }
            })
            fabAddMatery.setOnClickListener{
                findNavController().navigate(R.id.action_guessQFragment_to_uploadGuessQFragment)
            }
        }
    }

    private fun observeGuessQ() {
        TODO("Not yet implemented")
    }

    private fun deleteGuessQ(position: Int, id: Int) {

    }


}