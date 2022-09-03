package com.wisnu.speechrecognition.view.main.ui.student.play.guess

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.wisnu.speechrecognition.databinding.FragmentGuessBinding
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.GuessQViewModel

class GuessFragment : Fragment() {

    private val viewModel by viewModels<GuessQViewModel>()
    private var _binding: FragmentGuessBinding? = null
    private val binding get() = _binding!!
    private var jumlahBenar = 0

    companion object {
        fun newInstance() = GuessFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuessBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        TODO("Not yet implemented")
    }

    private  fun observeGuessQ(){

    }



}