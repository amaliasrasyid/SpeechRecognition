package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.uploadmateri

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.adapter.MaterialStudyScoreAdapter
import com.wisnu.speechrecognition.databinding.FragmentMaterialStudyBinding
import com.wisnu.speechrecognition.databinding.FragmentUploadMateryBinding
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyFragmentArgs
import com.wisnu.speechrecognition.view.main.ui.student.study.material_study.MaterialStudyViewModel
import www.sanju.motiontoast.MotionToast

class UploadMateryFragment : Fragment(), View.OnClickListener {

    private val viewModel by viewModels<UploadMateryViewModel>()
    private var _binding: FragmentUploadMateryBinding? = null
    private val binding get() = _binding!!
    private lateinit var args: UploadMateryFragmentArgs
    private var tipeMateri = 0
    private var idMateri = 0
    private var teksMateri = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadMateryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = UploadMateryFragmentArgs.fromBundle(arguments as Bundle)
        tipeMateri = args.tipeMateriData
        idMateri = args.idData
        teksMateri = args.teksMateri
        prepareView(tipeMateri)
    }

    private fun prepareView(tipeMateri: Int) {
        with(binding){
            btnSimpan.setOnClickListener(this@UploadMateryFragment)

            if(idMateri != 0){
                edtTeksJawaban.setText(teksMateri ?: "")
            }
        }
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                btnSimpan -> storeMatery()
            }
        }
    }

    private fun storeMatery() {
        with(binding){
            val materyParams = hashMapOf<String,Any>()
            materyParams["id"] = idMateri
            materyParams["tipe_materi"] = tipeMateri
            materyParams["teks_materi"] = edtTeksJawaban.text.toString()

            viewModel.storeMateryStudy(materyParams).observe(viewLifecycleOwner, { response ->
                loader(false)
                if (response != null) {
                    if (response.code == 200) {
                        showMessage(
                            requireActivity(),
                            UtilsCode.TITLE_SUCESS,
                            "Berhasil menyimpan nilai",
                            style = MotionToast.TOAST_SUCCESS
                        )
                        findNavController().navigateUp()
                    } else {
                        showMessage(
                            requireActivity(),
                            UtilsCode.TITLE_ERROR,
                            response.message ?: "",
                            style = MotionToast.TOAST_ERROR
                        )
                    }
                } else {
                    showMessage(
                        requireActivity(),
                        UtilsCode.TITLE_ERROR,
                        "nilai gagal disimpan",
                        style = MotionToast.TOAST_ERROR
                    )
                }
            })
        }
    }

    private fun loader(state: Boolean) {
        with(binding) {
            if (state) {
                pbMatery.visibility = View.VISIBLE
            } else {
                pbMatery.visibility = View.GONE
            }
        }
    }
}