package com.wisnu.speechrecognition.view.main.ui.teacher.home

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.wisnu.speechrecognition.view.auth.AuthActivity
import com.wisnu.speechrecognition.view.main.ui.profile.UserProfileActivity
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
            Glide.with(requireContext())
                .load(ApiConfig.URL_IMAGE +user.gambar)
                .error(R.drawable.no_profile_images)
                .into(imgUser)
            menuQuestions.setOnClickListener{
                findNavController().navigate(R.id.action_homeTeacherFragment_to_categoryFragment)
            }
            menuStudents.setOnClickListener{
                findNavController().navigate(R.id.action_homeTeacherFragment_to_studentsFragment)
            }
            imgUser.setOnClickListener{
                Log.d(TAG,"gambar profil diklik")
                startActivity(Intent(requireActivity(),UserProfileActivity::class.java))
            }
            btnLogOut.setOnClickListener{showAlertDialog()}
        }
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        alertDialogBuilder.setTitle(getString(R.string.log_out))
            .setMessage(getString(R.string.message_log_out))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                // clear all preferences
                UserPreference(requireActivity()).apply {
                    removeUser()
                }
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
            }
            .setNegativeButton("Tidak") { dialog, i ->
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        val user = UserPreference(requireContext()).getUser()
        with(binding){
            tvName.text = user.nama
            Glide.with(requireContext())
                .load(ApiConfig.URL_IMAGE +user.gambar)
                .error(R.drawable.no_profile_images)
                .into(imgUser)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}