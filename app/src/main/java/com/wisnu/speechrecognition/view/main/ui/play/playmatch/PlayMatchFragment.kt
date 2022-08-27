package com.wisnu.speechrecognition.view.main.ui.play.playmatch

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wisnu.speechrecognition.R

class PlayMatchFragment : Fragment() {

    companion object {
        fun newInstance() = PlayMatchFragment()
    }

    private lateinit var viewModel: PlayMatchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_play_match, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlayMatchViewModel::class.java)
        // TODO: Use the ViewModel
    }

}