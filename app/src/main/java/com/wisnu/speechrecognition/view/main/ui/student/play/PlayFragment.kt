package com.wisnu.speechrecognition.view.main.ui.student.play

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentPlayBinding
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragmentDirections

class PlayFragment : Fragment(),View.OnClickListener {

    companion object {
        fun newInstance() = PlayFragment()
    }
    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {

    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                cardGuessWord -> {
                    findNavController().navigate(R.id.action_playFragment_to_guessFragment)
                }
                cardFindPairWords -> {
                    findNavController().navigate(R.id.action_playFragment_to_pairFragment)
                }
            }
        }
    }

}