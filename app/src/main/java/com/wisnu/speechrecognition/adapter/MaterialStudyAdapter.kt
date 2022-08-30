package com.wisnu.speechrecognition.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.ItemListMateryBinding
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_AZ
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_KONSONAN
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_MEMBACA

class MaterialStudyAdapter : RecyclerView.Adapter<MaterialStudyAdapter.MaterialStudyViewHolder>() {

    private val listMateryStudy = ArrayList<MateryStudy>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    private val TAG = MaterialStudyAdapter::class.simpleName

    fun setData(MateryStudy: List<MateryStudy>?) {
        if (MateryStudy == null) return
        listMateryStudy.clear()
        listMateryStudy.addAll(MateryStudy)
        notifyDataSetChanged()

        Log.d(TAG, "setData: $listMateryStudy")
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialStudyViewHolder {
        val binding =
            ItemListMateryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MaterialStudyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialStudyViewHolder, position: Int) {
        holder.bind(listMateryStudy[position])
    }

    override fun getItemCount() = listMateryStudy.size

    inner class MaterialStudyViewHolder(private val binding: ItemListMateryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(materyStudy: MateryStudy) {
            with(binding) {
                when(materyStudy.tipeMateri){
                    TIPE_HURUF_VOKAL -> {
                        tvNameMateryVokal.visibility = View.VISIBLE
                        tvNameMateryVokal.text = materyStudy.teksMateri
                        tvNameMateryLetters.visibility = View.GONE
                        tvNameMateryReading.visibility = View.GONE
                    }
                    TIPE_MEMBACA -> {
                        tvNameMateryReading.visibility = View.VISIBLE
                        tvNameMateryReading.text = materyStudy.teksMateri
                        tvNameMateryLetters.visibility = View.GONE
                        tvNameMateryVokal.visibility = View.GONE
                    }
                    TIPE_HURUF_AZ,TIPE_HURUF_KONSONAN -> {
                        tvNameMateryLetters.visibility = View.VISIBLE
                        tvNameMateryLetters.text = materyStudy.teksMateri
                        tvNameMateryVokal.visibility = View.GONE
                        tvNameMateryReading.visibility = View.GONE
                    }
                }
                itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(materyStudy) }
            }
        }

    }

    interface OnItemClickCallBack {
        fun onItemClicked(materyStudy: MateryStudy)
    }
}