package com.wisnu.speechrecognition.view.main.ui.student.study.material_study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.adapter.MaterialStudyScoreAdapter
import com.wisnu.speechrecognition.databinding.FragmentMaterialStudyBinding
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_AZ
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_KONSONAN
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_MEMBACA
import www.sanju.motiontoast.MotionToast
import java.util.*
import kotlin.collections.ArrayList

class MaterialStudyFragment : Fragment(), SearchView.OnQueryTextListener {

    companion object {
        fun newInstance() = MaterialStudyFragment()
    }

    private val viewModel by viewModels<MaterialStudyViewModel>()
    private var _binding: FragmentMaterialStudyBinding? = null
    private val binding get() = _binding!!
    private lateinit var materyAdapter: MaterialStudyScoreAdapter
    private lateinit var args: MaterialStudyFragmentArgs
    private var listMateryStudy = ArrayList<MateryStudy>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMaterialStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = MaterialStudyFragmentArgs.fromBundle(arguments as Bundle)
        val tipeMateri = args.tipeMateri
        prepareView(tipeMateri)
    }

    private fun prepareView(tipeMateri: Int) {
        with(binding){
            when(tipeMateri){
                TIPE_HURUF_AZ -> {
                    observeMaterialStudy(TIPE_HURUF_AZ)
                }
                TIPE_HURUF_KONSONAN -> {
                    observeMaterialStudy(TIPE_HURUF_KONSONAN)
                }
                TIPE_HURUF_VOKAL -> {
                    observeMaterialStudy(TIPE_HURUF_VOKAL)
                }
                TIPE_MEMBACA -> {
                    observeMaterialStudy(TIPE_MEMBACA)
                }
            }

            //adapter
            materyAdapter = MaterialStudyScoreAdapter()
            with(rvMaterialStudy){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = materyAdapter
            }
            //search view
            searchview.clearFocus()
            searchview.setOnQueryTextListener(this@MaterialStudyFragment)
            //hide back and title when searchview clicked
            searchview.setOnSearchClickListener{
                btnBack.visibility = View.GONE
                tvMaterialStudy.visibility = View.GONE
            }
            //show back and title when searchview clicked
            searchview.setOnCloseListener(object: SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    btnBack.visibility = View.VISIBLE
                    tvMaterialStudy.visibility = View.VISIBLE
                    return false //karna aku ingin fungsi close berjalan seperti biasa
                }
            })

            val materyType = args.namaTipe
            tvMaterialStudy.text = materyType
            btnBack.setOnClickListener{findNavController().navigateUp()}
            materyAdapter.setOnItemClickCallBack(object : MaterialStudyScoreAdapter.OnItemClickCallBack {
                override fun onItemClicked(materyStudy: MateryStudy) {
                    // move intent and send id chapter
                    val toQuestion = MaterialStudyFragmentDirections.actionMaterialStudyFragmentToQuestionFragment().apply {
                        idMateriBelajar = materyStudy.id
                        tipeMateriBelajar = materyStudy.tipeMateri
                    }
                    findNavController().navigate(toQuestion)
                }
            })
        }

    }


    private fun observeMaterialStudy(materyId: Int) {
        with(binding){
            viewModel.materialStudy(materyId).observe(viewLifecycleOwner, { response ->
                pbMatery.visibility = View.GONE
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            materyAdapter.setData(result)
                            listMateryStudy.addAll(result)
                        } else {
                            dataNotFound()
                        }
                    }else{
                        dataNotFound()
                    }
                } else {
                    dataNotFound()
                }
            })
        }
    }

    //TODO: sebaiknya pindah ke util karna dipkai multiple diberbagai class
    private fun dataNotFound() {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            layoutEmpty.visibility = View.VISIBLE
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filterList(newText)
        return true
    }

    private fun filterList(text: String?) {
        val filteredlist = ArrayList<Any>()

        for (item in listMateryStudy) {
            if (item.teksMateri.uppercase().contains(text!!.uppercase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(requireActivity(),"Tidak ada data ditemukan", Toast.LENGTH_SHORT).show()
        } else {
            materyAdapter.setFilteredList(filteredlist)
        }
    }

}