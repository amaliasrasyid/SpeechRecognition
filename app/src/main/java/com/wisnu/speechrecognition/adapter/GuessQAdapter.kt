package com.wisnu.speechrecognition.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wisnu.speechrecognition.databinding.ItemListDmateryBinding
import com.wisnu.speechrecognition.model.questions.GuessQItem

class GuessQAdapter : RecyclerView.Adapter<GuessQAdapter.GuessQViewHolder>() {
    private val listGuessQ= ArrayList<GuessQItem>()

    private val TAG = GuessQAdapter::class.simpleName

    private var onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack? = null
    private var onItemBtnEditCallBack: OnItemBtnEditClickCallBack? = null

    interface OnItemBtnDeleteClickCallBack {
        fun onDeleteClicked(position: Int, question: GuessQItem)
    }

    interface OnItemBtnEditClickCallBack {
        fun onEditClicked(question: GuessQItem)
    }

    fun setOnItemBtnDeleteCallBack(onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack) {
        this.onItemBtnDeleteCallBack = onItemBtnDeleteCallBack
    }
    fun setOnItemBtnEditCallBack(onItemBtnEditCallBack: OnItemBtnEditClickCallBack) {
        this.onItemBtnEditCallBack = onItemBtnEditCallBack
    }

    fun setData(data: List<GuessQItem>?) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessQViewHolder {
        val binding =
            ItemListDmateryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuessQViewHolder(binding)    }

    override fun onBindViewHolder(holder: GuessQViewHolder, position: Int) {
        holder.bind(listGuessQ[position],position)
    }

    override fun getItemCount() = listGuessQ.size

    inner class GuessQViewHolder(private val binding: ItemListDmateryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(guessQItem: GuessQItem, position: Int) {
            with(binding) {
                var positionItem = position + 1
                tvNameMatery.text = "Soal-${positionItem}"
                btnDelete.setOnClickListener{onItemBtnDeleteCallBack?.onDeleteClicked(bindingAdapterPosition,guessQItem)}
                btnEdit.setOnClickListener{onItemBtnEditCallBack?.onEditClicked(guessQItem)}

            }
        }

    }

}