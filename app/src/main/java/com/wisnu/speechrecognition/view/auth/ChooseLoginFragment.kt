package com.wisnu.speechrecognition.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentChooseLoginBinding

class ChooseLoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentChooseLoginBinding

    companion object {
        const val ROLE_ADMIN = 1
        const val ROLE_TEACHER = 2
        const val ROLE_STUDENT = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnTeacher.setOnClickListener(this@ChooseLoginFragment)
            btnStudent.setOnClickListener(this@ChooseLoginFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_teacher -> {
                val toLogin =
                    ChooseLoginFragmentDirections.actionChooseLoginFragmentToLoginFragment(
                        ROLE_TEACHER
                    )
                findNavController().navigate(toLogin)
            }
            R.id.btn_student -> {
                val toLogin =
                    ChooseLoginFragmentDirections.actionChooseLoginFragmentToLoginFragment(
                        ROLE_STUDENT
                    )
                findNavController().navigate(toLogin)
            }
        }
    }
}