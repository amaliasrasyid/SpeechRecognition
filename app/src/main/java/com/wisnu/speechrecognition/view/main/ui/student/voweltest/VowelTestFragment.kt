package com.wisnu.speechrecognition.view.main.ui.student.voweltest

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wisnu.speechrecognition.R

class VowelTestFragment : Fragment() {

    companion object {
        fun newInstance() = VowelTestFragment()
    }

    private lateinit var viewModel: VowelTestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vowel_test, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VowelTestViewModel::class.java)
        // TODO: Use the ViewModel
    }

}