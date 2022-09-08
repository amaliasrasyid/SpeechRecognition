package com.wisnu.speechrecognition.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wisnu.speechrecognition.databinding.ItemListPairqBinding
import com.wisnu.speechrecognition.model.questions.PairWordQ
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.RvItemClickListener

class PairQAdapter : RecyclerView.Adapter<PairQAdapter.PairQViewHolder>() {
    private var viewPool = RecyclerView.RecycledViewPool()
    private val listPairQ= ArrayList<PairWordQ>()
    lateinit  var childAdapter: PairAdapter

    private val TAG = PairQAdapter::class.simpleName

    private var onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack? = null
    private var onItemClickCallBack: OnItemClickCallBack? = null
    private var onAddButtonClickCallBack: OnAddButtonClickCallBack? = null
    private var rvItemClickListener: RvItemClickListener? = null


    fun setOnAddButtonClickCallBack(onAddButtonClickCallBack: OnAddButtonClickCallBack){
        this.onAddButtonClickCallBack = onAddButtonClickCallBack
    }

    fun setRvitemClickListener(rvItemClickListener: RvItemClickListener){
        this.rvItemClickListener = rvItemClickListener
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    fun setOnItemBtnDeleteCallBack(onItemBtnDeleteCallBack: OnItemBtnDeleteClickCallBack) {
        this.onItemBtnDeleteCallBack = onItemBtnDeleteCallBack
    }

    fun setData(data: List<PairWordQ>?) {
        if (data == null) return
        listPairQ.clear()
        listPairQ.addAll(data)
        notifyDataSetChanged()

        Log.d(TAG, "setData: $listPairQ")
    }

    fun removeData(position: Int){
        listPairQ.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairQViewHolder {
        val binding =
            ItemListPairqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PairQViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PairQViewHolder, position: Int) {
        val item = listPairQ[position]
        val childLayoutManager = LinearLayoutManager(
            holder.childRv.context,
            LinearLayoutManager.VERTICAL,
            false
        )
        childLayoutManager.initialPrefetchItemCount = item.pairs?.size ?: 0
        childAdapter = PairAdapter(item.pairs)
        holder.childRv.apply {
            layoutManager = childLayoutManager
            adapter = childAdapter
            setRecycledViewPool(viewPool)
        }
        holder.bind(item,position)

    }

    override fun getItemCount() = listPairQ.size

    inner class PairQViewHolder(private val binding: ItemListPairqBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val childRv: RecyclerView = binding.rvPair

        fun bind(pairWordQ: PairWordQ, position: Int) {
            with(binding) {
                var positionItem = position + 1
                textView.text = "Soal-${positionItem}"

                btnShow.setOnClickListener {
                    btnShow.visibility = View.GONE
                    rvPair.visibility = View.VISIBLE
                    btnHide.visibility = View.VISIBLE
                    btnAddPair.visibility = View.VISIBLE
                    tvTextPair.visibility = View.VISIBLE
                }
                btnHide.setOnClickListener {
                    btnShow.visibility = View.VISIBLE
                    rvPair.visibility = View.GONE
                    btnHide.visibility = View.GONE
                    btnAddPair.visibility = View.GONE
                    tvTextPair.visibility = View.GONE
                }

                if(pairWordQ.pairs == null || pairWordQ.pairs.isEmpty()){
                    //show no data n add button
                    tvTextPair.text = "Belum ada data pasangan soal, silahkan tambah"
                    tvTextPair.setTextColor(Color.RED)
                }

                itemView.setOnClickListener{onItemClickCallBack?.onItemClicked(pairWordQ)}
                btnAddPair.setOnClickListener { onAddButtonClickCallBack?.onAddButtonClicked(pairWordQ) }
                childAdapter.setOnItemBtnDeleteCallBack(object :PairAdapter.OnItemBtnDeleteClickCallBack{
                    override fun onDeleteClicked(childPosition: Int) {
                        val clickedPair = pairWordQ.pairs!!.get(childPosition)
                        rvItemClickListener?.onChildItemDeleteClick(bindingAdapterPosition,childPosition,clickedPair)
                        Log.d("clicked pair-delete",clickedPair.toString())
                    }

                })
                childAdapter.setOnItemBtnEditCallBack(object: PairAdapter.OnItemBtnEditClickCallBack{
                    override fun onEditClicked(childPosition: Int) {
                        val clickedPair = pairWordQ.pairs!!.get(childPosition)
                        rvItemClickListener?.onChildItemEditClick(bindingAdapterPosition,childPosition,clickedPair)
                        Log.d("clicked pair-edit",clickedPair.toString())
                    }

                })
            }
        }

    }

    inner class SwipeGesture: ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }

    }
    //interface listener add pair (not pair Question)
    interface  OnAddButtonClickCallBack{
        fun onAddButtonClicked(pairWordQ: PairWordQ)
    }

    interface OnItemClickCallBack {
        fun onItemClicked(pairWordQ: PairWordQ)
    }

    interface OnItemBtnDeleteClickCallBack {
        fun onDeleteClicked(position: Int, question: PairWordQ)
    }
}
