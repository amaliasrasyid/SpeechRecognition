package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wisnu.speechrecognition.adapter.PairQAdapter
import com.wisnu.speechrecognition.databinding.FragmentPairQBinding
import com.wisnu.speechrecognition.local_db.Pair
import com.wisnu.speechrecognition.local_db.PairWordQClass
import com.wisnu.speechrecognition.model.questions.PairWordQ
import com.wisnu.speechrecognition.model.questions.PairsItem
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.uploadpair.UploadPairActivity
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.uploadpair.UploadPairActivity.Companion.EXTRA_DATA_ID_Q
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.uploadpair.UploadPairActivity.Companion.EXTRA_DATA_PAIR
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.uploadpairq.UploadPairQActivity
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.uploadpairq.UploadPairQActivity.Companion.EXTRA_DATA_PAIRQ
import www.sanju.motiontoast.MotionToast

class PairQFragment : Fragment(), RvItemClickListener, SearchView.OnQueryTextListener {
    private val viewModel by viewModels<PairQViewModel>()
    private var _binding: FragmentPairQBinding? = null
    private val binding get() = _binding!!
    private lateinit var pairQAdapter: PairQAdapter
    private var listPairQ = ArrayList<PairWordQ>()

    private var isSearching = false

    private val TAG = PairQFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPairQBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        with(binding){
            observePairQ()
            initRecyclerView()
            ItemTouchHelper(object: SimpleCallback(0, LEFT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val deletedPosition = viewHolder.bindingAdapterPosition
                    val deletedItem = pairQAdapter.getData(deletedPosition)
                    deletePairQ(deletedItem.id!!)
                }

            }).attachToRecyclerView(binding.rvPairq)
            fabAddPairQ.setOnClickListener{
                val intent = Intent(requireActivity(), UploadPairQActivity::class.java)
                startActivity(intent)
            }
            btnBack.setOnClickListener{ findNavController().navigateUp() }
        }
    }

    private fun initRecyclerView() {
        with(binding){
            pairQAdapter = PairQAdapter()
            with(rvPairq){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                this.adapter =pairQAdapter
            }
            //search view
            searchview.clearFocus()
            searchview.setOnQueryTextListener(this@PairQFragment)
            //hide back and title when searchview clicked
            searchview.setOnSearchClickListener{
                btnBack.visibility = View.GONE
                tvMaterialStudy.visibility = View.GONE
                isSearching = true
            }
            //show back and title when searchview clicked
            searchview.setOnCloseListener(object: SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    btnBack.visibility = View.VISIBLE
                    tvMaterialStudy.visibility = View.VISIBLE
                    isSearching = false
                    return false //karna aku ingin fungsi close berjalan seperti biasa
                }
            })

            pairQAdapter.setOnItemClickCallBack(object : PairQAdapter.OnItemClickCallBack{
                override fun onItemClicked(pairWordQ: PairWordQ) {
                    Log.d(TAG,"terklik tombol soal (parent")
                    val intent = Intent(requireActivity(), UploadPairQActivity::class.java)
                    val pairWQ = PairWordQClass(
                        id = pairWordQ.id,
                        suara = pairWordQ.suara
                    )
                    intent.apply { 
                        intent.putExtra(EXTRA_DATA_PAIRQ,pairWQ)
                    }
                    startActivity(intent)
                }
            })
            pairQAdapter.setOnAddButtonClickCallBack(object :PairQAdapter.OnAddButtonClickCallBack{
                override fun onAddButtonClicked(pairWordQ: PairWordQ) {
                    val intent = Intent(requireActivity(),UploadPairActivity::class.java)
                    intent.putExtra(EXTRA_DATA_ID_Q,pairWordQ.id)
                    startActivity(intent)
                }
            })
            pairQAdapter.setRvitemClickListener(this@PairQFragment)
        }
    }

    override fun onChildItemDeleteClick(parentPosition: Int, childPosition: Int, pair: PairsItem) {
//        pairQAdapter.childAdapter.removeData(childPosition)
        loader(true)
        deletePair(pair.id ?: 0)
    }

    override fun onChildItemEditClick(parentPosition: Int, childPosition: Int, pair: PairsItem) {
        val pair = Pair(
            id = pair.id,
            kata = pair.kata,
            gambar = pair.gambar,
            idSoal = pair.soal,
        )
        val intent = Intent(requireActivity(),UploadPairActivity::class.java)
        intent.apply {
            putExtra(EXTRA_DATA_PAIR,pair)
        }
        startActivity(intent)
    }

    private fun observePairQ() {
        with(binding){
            viewModel.questions().observe(viewLifecycleOwner, { response ->
                loader(false)
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            pairQAdapter.setData(result)
                            listPairQ.clear()
                            listPairQ.addAll(result)

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

    private fun deletePair(id: Int) {
        with(binding) {
            viewModel.deletePair(id).observe(viewLifecycleOwner) { response ->
                loader(false)
                if (response != null) {
                    if (response.code == 404) {
                        showMessage(
                            requireActivity(),
                            UtilsCode.TITLE_ERROR,
                            response.message ?: "",
                            style = MotionToast.TOAST_ERROR
                        )
                        observePairQ()//ulang data kembali jika gagal hapus di server
                    } else {
                        showMessage(
                            requireActivity(),
                            UtilsCode.TITLE_SUCESS,
                            response.message ?: "",
                            style = MotionToast.TOAST_SUCCESS
                        )
                        observePairQ()
                    }
                }else{
                    showMessage(
                        requireActivity(),
                        UtilsCode.TITLE_ERROR,
                        style = MotionToast.TOAST_ERROR
                    )
                }
            }
        }
    }

    private fun deletePairQ(id: Int){
        with(binding) {
            viewModel.delete(id).observe(viewLifecycleOwner) { response ->
                loader(false)
                if (response != null) {
                    if (response.code == 404) {
                        showMessage(
                            requireActivity(),
                            UtilsCode.TITLE_ERROR,
                            response.message ?: "",
                            style = MotionToast.TOAST_ERROR
                        )
                        observePairQ()//ulang data kembali jika gagal hapus di server
                    } else {
                        showMessage(
                            requireActivity(),
                            UtilsCode.TITLE_SUCESS,
                            response.message ?: "",
                            style = MotionToast.TOAST_SUCCESS
                        )
                        observePairQ()
                    }
                }else{
                    showMessage(
                        requireActivity(),
                        UtilsCode.TITLE_ERROR,
                        style = MotionToast.TOAST_ERROR
                    )
                }
            }
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
        val filteredlist = ArrayList<PairWordQ>()

        //filter berdasarkan pasangan2 yg dimiliki (kata)
        for ((index,item) in listPairQ.withIndex()) {
            val pairs = listPairQ.get(index).pairs
            for (pair in pairs!!){
                if(pair.kata!!.uppercase().contains(text!!.uppercase())){
                    filteredlist.add(item)
                    break//jika sudah ketemu sekali aja langsung stop untuk item pairQ itu
                }
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(requireActivity(),"Tidak ada data ditemukan", Toast.LENGTH_SHORT).show()
        } else {
            pairQAdapter.setFilteredList(filteredlist)
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

    private fun loader(state: Boolean) {
        with(binding) {
            if (state) {
                pbLoader.visibility = android.view.View.VISIBLE
            } else {
                pbLoader.visibility = android.view.View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onresume")
        Log.d("ListGuessQ",listPairQ.toString())
        if(!isSearching){
            observePairQ()
        }
    }
}

interface RvItemClickListener{
    fun onChildItemDeleteClick(parentPosition: Int,childPosition: Int,pair :PairsItem)
    fun onChildItemEditClick(parentPosition: Int,childPosition: Int,pair :PairsItem)
}