package com.wisnu.speechrecognition.view.main.ui.student.home

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
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
import com.wisnu.speechrecognition.view.auth.AuthActivity
import com.wisnu.speechrecognition.view.main.ui.profile.UserProfileActivity
import com.wisnu.speechrecognition.view.main.ui.profile.UserProfileActivity.Companion.TYPE

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
            imgUser.setOnClickListener(this@HomeStudentFragment)
            btnLogOut.setOnClickListener(this@HomeStudentFragment)
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
                btnLogOut -> showAlertDialog()
                cardMenu1 -> {
                    findNavController().navigate(R.id.action_navigation_home_to_studyFragment)
                }
                cardMenu2 -> {
                    findNavController().navigate(R.id.action_navigation_home_to_playFragment)
                }
                cardMenu3 -> {
                    val toKategoriNilaiSiswa = HomeStudentFragmentDirections.actionNavigationHomeToCategoryScoreFragment()
                    findNavController().navigate(toKategoriNilaiSiswa)
                }
                imgUser ->  {
                    Log.d(ContentValues.TAG,"gambar profil diklik")
                    startActivity(Intent(requireActivity(), UserProfileActivity::class.java))
                }
            }
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
        prepareView()
    }
}