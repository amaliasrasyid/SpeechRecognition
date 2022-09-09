package com.wisnu.speechrecognition.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wisnu.speechrecognition.databinding.ItemListPairBinding
import com.wisnu.speechrecognition.model.questions.PairsItem

class PairAdapter(private val listPair: List<PairsItem>?) : RecyclerView.Adapter<PairAdapter.PairViewHolder>() {
    private val listPairItem= ArrayList<PairsItem>()

    private val TAG = PairAdapter::class.simpleName

    private var onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack? = null
    private var onItemBtnEditCallBack: OnItemBtnEditClickCallBack? = null

    interface OnItemBtnDeleteClickCallBack {
        fun onDeleteClicked(childPosition: Int)
    }

    interface OnItemBtnEditClickCallBack {
        fun onEditClicked(childPosition: Int)
    }

    fun setOnItemBtnDeleteCallBack(onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack) {
        this.onItemBtnDeleteCallBack = onItemBtnDeleteCallBack
    }
    fun setOnItemBtnEditCallBack(onItemBtnEditCallBack: OnItemBtnEditClickCallBack) {
        this.onItemBtnEditCallBack = onItemBtnEditCallBack
    }

    init {
        if (listPair != null){
            listPairItem.clear()
            listPairItem.addAll(listPair)
            notifyDataSetChanged()

            Log.d(TAG, "setData list pairs: $listPairItem")
        }
    }

    fun removeData(position: Int){
        listPairItem.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairViewHolder {
        val binding =
            ItemListPairBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PairViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PairViewHolder, position: Int) {
        holder.bind(listPairItem[position],position)
    }

    override fun getItemCount() = listPairItem.size

    inner class PairViewHolder(private val binding: ItemListPairBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pairsItem: PairsItem, position: Int) {
            with(binding) {
                tvNamePair.text = pairsItem.kata!!.uppercase()

                btnDelete.setOnClickListener{onItemBtnDeleteCallBack?.onDeleteClicked(position)}
                btnEdit.setOnClickListener{onItemBtnEditCallBack?.onEditClicked(position)}
            }
        }

    }

}