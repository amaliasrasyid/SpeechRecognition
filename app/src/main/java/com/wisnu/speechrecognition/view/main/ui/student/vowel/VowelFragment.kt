package com.wisnu.speechrecognition.view.main.ui.student.vowel

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wisnu.speechrecognition.R

class VowelFragment : Fragment() {

    companion object {
        fun newInstance() = VowelFragment()
    }

    private lateinit var viewModel: VowelViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vowel, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VowelViewModel::class.java)
        // TODO: Use the ViewModel
    }

}