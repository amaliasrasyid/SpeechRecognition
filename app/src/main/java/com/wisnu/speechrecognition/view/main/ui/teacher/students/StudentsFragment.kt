package com.wisnu.speechrecognition.view.main.ui.teacher.students

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.adapter.StudentAdapter
import com.wisnu.speechrecognition.databinding.FragmentStudentsBinding
import com.wisnu.speechrecognition.model.student.StudentsResult

class StudentsFragment : Fragment() {
    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<StudentsViewModel>()
    private lateinit var studentsAdapter: StudentAdapter

    companion object {
        fun newInstance() = StudentsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentsBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        with(binding){
            //adapter
            studentsAdapter = StudentAdapter()
            with(rvDataSiswa){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = studentsAdapter
            }
            btnBack.setOnClickListener{
                findNavController().navigateUp()
            }
            studentsAdapter.setOnItemClickCallBack(object : StudentAdapter.OnItemClickCallBack {
                override fun onItemClicked(studentsResult: StudentsResult) {
                    findNavController().navigate(R.id.action_studentsFragment_to_categoryScoreFragment2,
                        bundleOf("id_siswa" to studentsResult.id))
                }
            })
        }
        observeStudents()
    }

    private fun observeStudents() {
        with(binding){
            viewModel.students().observe(viewLifecycleOwner, { response ->
                pbMatery.visibility = View.GONE
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            studentsAdapter.setData(result)
                        } else {
                            dataNotFound()
                        }
                    } else {
                        dataNotFound()
                    }
                } else {
                    dataNotFound()
                }
            })
        }
    }

    private fun dataNotFound() {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            layoutEmpty.visibility = View.VISIBLE
        }
    }

}