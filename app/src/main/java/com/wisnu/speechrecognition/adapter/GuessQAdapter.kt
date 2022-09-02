package com.wisnu.speechrecognition.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wisnu.speechrecognition.databinding.ItemListDmateryBinding
import com.wisnu.speechrecognition.databinding.ItemListMateryBinding
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.model.questions.Question
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment

class GuessQAdapter : RecyclerView.Adapter<GuessQAdapter.MateryViewHolder>() {
    private val listGuessQ= ArrayList<Question>()

    private val TAG = GuessQAdapter::class.simpleName

    private var onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack? = null
    private var onItemBtnEditCallBack: OnItemBtnEditClickCallBack? = null

    interface OnItemBtnDeleteClickCallBack {
        fun onDeleteClicked(position: Int,question: Question)
    }

    interface OnItemBtnEditClickCallBack {
        fun onEditClicked(question: Question)
    }

    fun setOnItemBtnDeleteCallBack(onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack) {
        this.onItemBtnDeleteCallBack = onItemBtnDeleteCallBack
    }
    fun setOnItemBtnEditCallBack(onItemBtnEditCallBack: OnItemBtnEditClickCallBack) {
        this.onItemBtnEditCallBack = onItemBtnEditCallBack
    }

    fun setData(data: List<Question>?) {
        if (data == null) return
        listGuessQ.clear()
        listGuessQ.addAll(data)
        notifyDataSetChanged()

        Log.d(TAG, "setData: $listGuessQ")
    }

    fun removeData(position: Int){
        listGuessQ.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateryViewHolder {
        val binding =
            ItemListDmateryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MateryViewHolder(binding)    }

    override fun onBindViewHolder(holder: MateryViewHolder, position: Int) {
        holder.bind(listGuessQ[position])
    }

    override fun getItemCount() = listGuessQ.size

    inner class MateryViewHolder(private val binding: ItemListDmateryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: Question) {
            with(binding) {
                tvNameMatery.text = question.teksJawaban
                btnDelete.setOnClickListener{onItemBtnDeleteCallBack?.onDeleteClicked(bindingAdapterPosition,question)}
                btnEdit.setOnClickListener{onItemBtnEditCallBack?.onEditClicked(question)}
            }
        }

    }

}