package com.wisnu.speechrecognition.view.main.ui.student.vocabulary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wisnu.speechrecognition.R

class VocabularyFragment : Fragment() {

    companion object {
        fun newInstance() = VocabularyFragment()
    }

    private lateinit var viewModel: VocabularyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vocabulary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VocabularyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}