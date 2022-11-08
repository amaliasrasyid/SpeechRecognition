package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wisnu.speechrecognition.adapter.MateryAdapter
import com.wisnu.speechrecognition.databinding.FragmentMateryBinding
import com.wisnu.speechrecognition.data.model.matery.MateryStudy
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.category.CategoryFragment.Companion.TIPE_HURUF_AZ
import com.wisnu.speechrecognition.view.main.ui.category.CategoryFragment.Companion.TIPE_HURUF_KONSONAN
import com.wisnu.speechrecognition.view.main.ui.category.CategoryFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.category.CategoryFragment.Companion.TIPE_MEMBACA
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity.Companion.EXTRA_DATA_MATERY_ID
import www.sanju.motiontoast.MotionToast

class MateryFragment : Fragment(), SearchView.OnQueryTextListener {

    private val viewModel by viewModels<MateryViewModel>()
    private var _binding: FragmentMateryBinding? = null
    private val binding get() = _binding!!
    private lateinit var materyAdapter: MateryAdapter
    private lateinit var args: MateryFragmentArgs
    private var tipeMateri = 0

    private var listMatery = ArrayList<MateryStudy>()

    private val TAG = MateryFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMateryBinding.inflate(inflater, container, false)
        Log.d(TAG,"oncreateview")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"onviewcreated")
        args = MateryFragmentArgs.fromBundle(arguments as Bundle)
        tipeMateri = args.tipeMateri
        prepareView(tipeMateri) //KENAPA INI DATA LIST TERUPDATE PADAHAL TIDAK ADA NOTIFY DATA SAAT TAMBAH DI FRAGMENT SATUNYA
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
            materyAdapter = MateryAdapter()
            with(rvMaterialStudy){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter = materyAdapter
            }
            //search view
            searchview.clearFocus()
            searchview.setOnQueryTextListener(this@MateryFragment)
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

            materyAdapter.setOnItemClickCallBack(object : MateryAdapter.OnItemClickCallBack {
                override fun onItemClicked(item: MateryStudy) {
                    when(item.tipeMateri){
                        TIPE_HURUF_VOKAL -> {
                            val toVowelSentence = MateryFragmentDirections.actionMateryFragmentToVowelSentenceFragment().apply {
                                idMateri = item.id
                            }
                            findNavController().navigate(toVowelSentence)
                        }
                        else -> {
                            val intent = Intent(requireActivity(),UploadLessonQActivity::class.java)
                            intent.apply {
                                putExtra(EXTRA_DATA_MATERY_ID,item.id)
                            }
                            startActivity(intent)
                        }
                    }

                }
            })
            materyAdapter.setOnItemBtnDeleteCallBack(object : MateryAdapter.OnItemBtnDeleteClickCallBack {
                override fun onDeleteClicked(position: Int,materyStudy: MateryStudy) {
                    materyAdapter.removeData(position)
                    deleteMatery(materyStudy.id)
                    Log.d(ContentValues.TAG,"posisi item-${position}")
                }
            })
            materyAdapter.setOnItemBtnEditCallBack(object : MateryAdapter.OnItemBtnEditClickCallBack {
                override fun onEditClicked(materyStudy: MateryStudy) {
                   editMatery(materyStudy)
                }
            })
            fabAddMatery.setOnClickListener{
                val toUploadMatery = MateryFragmentDirections.actionMateryFragmentToUploadMateryFragment().apply {
                    tipeMateriData = tipeMateri
                }
                findNavController().navigate(toUploadMatery)
            }
        }
    }

    private fun editMatery(materyStudy: MateryStudy) {
        val toUploadMatery = MateryFragmentDirections.actionMateryFragmentToUploadMateryFragment().apply {
            tipeMateriData = materyStudy.tipeMateri
            idData = materyStudy.id
            teksMateri = materyStudy.teksMateri
        }
        findNavController().navigate(toUploadMatery)
    }

    private fun deleteMatery(materyId: Int) {
        with(binding){
            viewModel.delete(materyId).observe(viewLifecycleOwner, { response ->
                pbMatery.visibility = View.GONE
                if (response != null) {
                    if (response.code == 404){
                        showMessage(
                            requireActivity(),
                            TITLE_ERROR,
                            response.message ?: "",
                            style = MotionToast.TOAST_ERROR
                        )
                        observeMaterialStudy(materyId)
                    }else{
                        showMessage(
                            requireActivity(),
                            UtilsCode.TITLE_SUCESS,
                            response.message ?: "",
                            style = MotionToast.TOAST_SUCCESS
                        )
                        Log.d(TAG,"message success delete ${response.message}")
                    }
                } else {
                    showMessage(
                        requireActivity(),
                        TITLE_ERROR,
                        style = MotionToast.TOAST_ERROR
                    )
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
                            listMatery.clear()
                            listMatery.addAll(result)

                            dataNotFound(false)
                        } else {
                            dataNotFound()
                        }
                    }else{
                        dataNotFound()
                    }
                } else {
                    Log.e(TAG,"data is null")
                }
            })
        }
    }

    private fun filterList(text: String?) {
        val filteredlist = ArrayList<MateryStudy>()

        for (item in listMatery) {
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

    private fun dataNotFound(state :Boolean = true) {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            if(state){
                layoutEmpty.visibility = View.VISIBLE
            }else{
                layoutEmpty.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("MateryFragment","onresume")
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filterList(newText)
        return true
    }
}