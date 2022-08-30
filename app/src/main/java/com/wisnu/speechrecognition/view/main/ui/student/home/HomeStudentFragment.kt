package com.wisnu.speechrecognition.view.main.ui.student.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentHomeStudentBinding
import com.wisnu.speechrecognition.network.ApiConfig.Companion.URL_IMAGE
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_GURU
import com.wisnu.speechrecognition.utils.UtilsCode.ROLE_SISWA

class HomeStudentFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeStudentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeStudentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeStudentBinding.inflate(inflater, container, false)
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
            Glide.with(requireContext())
                .load(URL_IMAGE+user.gambar)
                .error(R.drawable.no_profile_images)
                .into(imgUser)
            cardMenu1.setOnClickListener(this@HomeStudentFragment)
            cardMenu2.setOnClickListener(this@HomeStudentFragment)
            cardMenu3.setOnClickListener(this@HomeStudentFragment)
        }

        //passing data dg observer pattern (live data)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                cardMenu1 -> {
                    findNavController().navigate(R.id.action_navigation_home_to_studyFragment)
                }
                cardMenu2 -> {
                    findNavController().navigate(R.id.action_navigation_home_to_playFragment)
                }
                cardMenu3 -> {
//                    findNavController().navigate(R.id.action_navigation_home_to_studyFragment)
                }
            }
        }
    }
}