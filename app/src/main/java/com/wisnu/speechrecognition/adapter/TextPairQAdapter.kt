package com.wisnu.speechrecognition.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.wisnu.speechrecognition.R

class TextPairQAdapter(context: Context,
                       resource: Int = 0,
                       textData: List<String>
) : ArrayAdapter<String>(context, resource, textData) {
    private var onTextClickCallback: OnTextClickCallback? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        val textData = getItem(position)
        if(listItem == null){
            listItem = LayoutInflater.from(context).inflate(R.layout.item_list_text_pair,parent,false)
        }
        with(convertView){
            var textview = listItem!!.findViewById<TextView>(R.id.tv_text)
            var textPoint = listItem!!.findViewById<ImageView>(R.id.point_text)
            textview.setText(textData!!.uppercase())
            textview.setOnClickListener { onTextClickCallback?.onTextClicked(textview,textPoint) }
        }
        return listItem!!
    }

    fun setOnTextClickCallback(onTextClickCallback: OnTextClickCallback) {
        this.onTextClickCallback = onTextClickCallback
    }

    interface OnTextClickCallback {
        fun onTextClicked(textView: TextView,view: ImageView)
    }
}
