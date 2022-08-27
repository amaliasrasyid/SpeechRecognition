package com.wisnu.speechrecognition.view.main.ui.home

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wisnu.speechrecognition.databinding.FragmentHomeBinding
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.view.main.ui.home.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        determineRoleUser(requireContext());
        //passing data dg observer pattern (live data)
    }

    private fun determineRoleUser(requireContext: Context) {
        val roleSession = UserPreference(requireContext()).getUser().role
        with(binding){
            when(roleSession){
                0 -> {
                    //ANGGAP ini guru
                }
                1 -> {
                    //ANGGAP ini guru
                    menuStudent.visibility = View.GONE
                    menuTeacher.visibility = View.VISIBLE
                }
                2 -> {

                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}