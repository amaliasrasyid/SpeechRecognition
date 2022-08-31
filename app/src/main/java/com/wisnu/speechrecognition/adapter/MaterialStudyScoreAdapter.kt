package com.wisnu.speechrecognition.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wisnu.speechrecognition.databinding.ItemListMateryScoreBinding
import com.wisnu.speechrecognition.model.student.StudentScore
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_AZ
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_KONSONAN
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_MEMBACA

class MaterialStudyScoreAdapter : RecyclerView.Adapter<MaterialStudyScoreAdapter.MaterialStudyViewHolder>() {

    private val listStudentScore = ArrayList<StudentScore>()

    private val TAG = MaterialStudyScoreAdapter::class.simpleName

    fun setData(StudentScore: List<StudentScore>?) {
        if (StudentScore == null) return
        listStudentScore.clear()
        listStudentScore.addAll(StudentScore)
        notifyDataSetChanged()

        Log.d(TAG, "setData: $listStudentScore")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialStudyViewHolder {
        val binding =
            ItemListMateryScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MaterialStudyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialStudyViewHolder, position: Int) {
        holder.bind(listStudentScore[position])
    }

    override fun getItemCount() = listStudentScore.size

    inner class MaterialStudyViewHolder(private val binding: ItemListMateryScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(studentScore: StudentScore) {
            with(binding) {
                tvNameMateryVokal.text = studentScore.namaMateri
                tvResultScore.text = "${studentScore.nilai}/100"
            }
        }

    }

}