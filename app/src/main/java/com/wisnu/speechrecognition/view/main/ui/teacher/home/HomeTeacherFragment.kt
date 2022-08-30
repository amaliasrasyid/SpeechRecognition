package com.wisnu.speechrecognition.view.main.ui.teacher.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentHomeTeacherBinding
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.view.main.ui.student.home.HomeStudentViewModel

class HomeTeacherFragment : Fragment() {
    private var _binding: FragmentHomeTeacherBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        val user = UserPreference(requireContext()).getUser()
        with(binding){
            tvName.text = user.nama
            com.bumptech.glide.Glide.with(requireContext())
                .load(com.wisnu.speechrecognition.network.ApiConfig.URL_IMAGE +user.gambar)
                .error(com.wisnu.speechrecognition.R.drawable.no_profile_images)
                .into(imgUser)
            menuQuestions.setOnClickListener{
                findNavController().navigate(R.id.action_homeTeacherFragment_to_categoryFragment)
            }
            menuStudents.setOnClickListener{
                findNavController().navigate(R.id.action_homeTeacherFragment_to_studentsFragment)
            }
        }
        //passing data dg observer pattern (live data)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}