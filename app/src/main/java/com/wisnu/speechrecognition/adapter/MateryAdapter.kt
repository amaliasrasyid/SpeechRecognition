package com.wisnu.speechrecognition.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wisnu.speechrecognition.databinding.ItemListDmateryBinding
import com.wisnu.speechrecognition.databinding.ItemListMateryBinding
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment

class MateryAdapter : RecyclerView.Adapter<MateryAdapter.MateryViewHolder>() {
    private val listMatery= ArrayList<MateryStudy>()

    private val TAG = MateryAdapter::class.simpleName

    private var onItemClickCallBack: OnItemClickCallBack? = null
    private var onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack? = null
    private var onItemBtnEditCallBack: OnItemBtnEditClickCallBack? = null

    interface OnItemClickCallBack {
        fun onItemClicked(materyStudy: MateryStudy)
    }

    interface OnItemBtnDeleteClickCallBack {
        fun onDeleteClicked(position: Int,materyStudy: MateryStudy)
    }

    interface OnItemBtnEditClickCallBack {
        fun onEditClicked(materyStudy: MateryStudy)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }
    fun setOnItemBtnDeleteCallBack(onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack) {
        this.onItemBtnDeleteCallBack = onItemBtnDeleteCallBack
    }
    fun setOnItemBtnEditCallBack(onItemBtnEditCallBack: OnItemBtnEditClickCallBack) {
        this.onItemBtnEditCallBack = onItemBtnEditCallBack
    }

    fun setData(data: List<MateryStudy>?) {
        if (data == null) return
        listMatery.clear()
        listMatery.addAll(data)
        notifyDataSetChanged()

        Log.d(TAG, "setData: $listMatery")
    }

    fun removeData(position: Int){
        listMatery.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateryViewHolder {
        val binding =
            ItemListDmateryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MateryViewHolder(binding)    }

    override fun onBindViewHolder(holder: MateryViewHolder, position: Int) {
        holder.bind(listMatery[position])
    }

    override fun getItemCount() = listMatery.size

    inner class MateryViewHolder(private val binding: ItemListDmateryBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(materyStudy: MateryStudy) {
            with(binding) {
                tvNameMatery.text = materyStudy.teksMateri
                btnDelete.setOnClickListener{onItemBtnDeleteCallBack?.onDeleteClicked(bindingAdapterPosition,materyStudy)}
                btnEdit.setOnClickListener{onItemBtnEditCallBack?.onEditClicked(materyStudy)}
                itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(materyStudy) }
            }
        }

    }

}