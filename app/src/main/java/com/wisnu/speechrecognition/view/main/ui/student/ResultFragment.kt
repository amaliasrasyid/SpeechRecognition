package com.wisnu.speechrecognition.view.main.ui.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentResultBinding
import com.wisnu.speechrecognition.session.UserPreference

class ResultFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
//        TODO: Tampilkan hasil skor dan ucapan2 plus2
        val args = ResultFragmentArgs.fromBundle(arguments as Bundle)
        val scoreStudent = args.scoreStudent
        val totalQ = args.totalQuestion
        val userName = UserPreference(requireContext()).getUser().nama

        with(binding) {
            tvName.text = userName
            tvScore.text = "Kamu menjawab ${scoreStudent} soal dengan benar dari total ${totalQ} soal"
            btnFinish.setOnClickListener(this@ResultFragment)
            btnClose.setOnClickListener(this@ResultFragment)
        }

    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                btnFinish,btnClose -> {
                    findNavController().navigate(R.id.action_resultFragment_to_playFragment2)
                }
            }
        }
    }

}