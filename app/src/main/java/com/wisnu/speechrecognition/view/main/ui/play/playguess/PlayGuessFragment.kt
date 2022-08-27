package com.wisnu.speechrecognition.view.main.ui.play.playguess

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wisnu.speechrecognition.R

class PlayGuessFragment : Fragment() {

    companion object {
        fun newInstance() = PlayGuessFragment()
    }

    private lateinit var viewModel: PlayGuessViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_play_guess, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlayGuessViewModel::class.java)
        // TODO: Use the ViewModel
    }

}