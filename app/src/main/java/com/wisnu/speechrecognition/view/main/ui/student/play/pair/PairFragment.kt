package com.wisnu.speechrecognition.view.main.ui.student.play.pair

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wisnu.speechrecognition.R

class PairFragment : Fragment() {

    companion object {
        fun newInstance() = PairFragment()
    }

    private lateinit var viewModel: PairViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pair, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PairViewModel::class.java)
        // TODO: Use the ViewModel
    }

}