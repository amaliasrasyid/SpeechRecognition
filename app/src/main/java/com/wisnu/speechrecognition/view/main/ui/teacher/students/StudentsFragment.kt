package com.wisnu.speechrecognition.view.main.ui.teacher.students

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.adapter.StudentAdapter
import com.wisnu.speechrecognition.databinding.FragmentStudentsBinding
import com.wisnu.speechrecognition.data.model.student.StudentsResult

class StudentsFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<StudentsViewModel>()
    private lateinit var studentsAdapter: StudentAdapter
    private val listStudents = ArrayList<StudentsResult>()

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

            //search view
            searchview.clearFocus()
            searchview.setOnQueryTextListener(this@StudentsFragment)
            //hide back and title when searchview clicked
            searchview.setOnSearchClickListener{
                btnBack.visibility = View.GONE
                tvTitle.visibility = View.GONE
            }
            //show back and title when searchview clicked
            searchview.setOnCloseListener(object: SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    btnBack.visibility = View.VISIBLE
                    tvTitle.visibility = View.VISIBLE
                    return false //karna aku ingin fungsi close berjalan seperti biasa
                }
            })


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
            viewModel.students().observe(viewLifecycleOwner) { response ->
                pbMatery.visibility = View.GONE
                if (response.data != null) {
                    if (!response.data.isEmpty()) {
                        if (response.code == 200) {
                            val result = response.data
                            studentsAdapter.setData(result)
                            listStudents.clear()
                            listStudents.addAll(result)

                            dataNotFound(false)
                        } else {
                            dataNotFound()
                        }
                    } else {
                        dataNotFound()
                    }
                } else {
                    dataNotFound()
                }
            }
        }
    }

    private fun filterList(text: String?) {
        val filteredlist = ArrayList<StudentsResult>()

        for (item in listStudents) {
            if (item.nama!!.uppercase().contains(text!!.uppercase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(requireActivity(),"Tidak ada data ditemukan", Toast.LENGTH_SHORT).show()
        } else {
            studentsAdapter.setFilteredList(filteredlist)
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filterList(newText)
        return true
    }
}