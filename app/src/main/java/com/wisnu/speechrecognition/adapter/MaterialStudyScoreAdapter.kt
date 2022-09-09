package com.wisnu.speechrecognition.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wisnu.speechrecognition.databinding.ItemListMateryBinding
import com.wisnu.speechrecognition.databinding.ItemListMateryScoreBinding
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.model.student.StudentScore
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_AZ
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_KONSONAN
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_MEMBACA

class MaterialStudyScoreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_TYPE_SCORE = 1
    private val ITEM_TYPE_MATERY = 2
    private var listData = ArrayList<Any>()

    private var isVokal = false

   fun setVokal(boolean: Boolean){
        isVokal = boolean
    }

    private var onItemClickCallBack: OnItemClickCallBack? = null

    private val TAG = MaterialStudyScoreAdapter::class.simpleName

    fun setData(data: List<Any>?) {
        if (data == null) return
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()

        Log.d(TAG, "setData: $listData")
    }

    fun setFilteredListMatery(filteredList: ArrayList<Any>) {
        listData = filteredList
        notifyDataSetChanged()

        Log.d(TAG, "filtered list: $filteredList")
    }

    fun setFilteredList(filteredList: ArrayList<Any>) {
        listData = filteredList
        notifyDataSetChanged()

        Log.d(TAG, "filtered list: $filteredList")
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    interface OnItemClickCallBack {
        fun onItemClicked(materyStudy: MateryStudy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_TYPE_MATERY -> {
                val binding =
                    ItemListMateryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MaterialStudyViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemListMateryScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MaterialStudyScoreViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(listData.get(position) is StudentScore){
            return ITEM_TYPE_SCORE
        }else{
            return ITEM_TYPE_MATERY
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val objectData = listData.get(position)
        when(holder){
            is MaterialStudyScoreViewHolder -> {
                holder.bind(objectData as StudentScore)
            }
            is MaterialStudyViewHolder -> {
                holder.bind(objectData as MateryStudy)
            }
        }
    }

    override fun getItemCount() = listData.size

    inner class MaterialStudyScoreViewHolder(private val binding: ItemListMateryScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(studentScore: StudentScore) {
            with(binding) {
                if(isVokal){
                    tvNameMateryVokal.text = studentScore.kalimatVokal //KHUSUS VOKAL
                }else{
                    tvNameMateryVokal.text = studentScore.namaMateri
                }
                tvResultScore.text = "${studentScore.nilai}/100"
            }
        }
    }

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
                    TIPE_HURUF_AZ, TIPE_HURUF_KONSONAN -> {
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
}