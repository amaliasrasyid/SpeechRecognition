package com.wisnu.speechrecognition.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.data.model.questions.PairsItem
import com.wisnu.speechrecognition.network.ApiConfig

class ImagePairQAdapter(context: Context,
                        resource: Int = 0,
                        imgData: List<PairsItem>
) : ArrayAdapter<PairsItem>(context, resource, imgData) {
    private var onImageClickCallback: OnImageClickCallback? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        val imgData = getItem(position)
        if(listItem == null){
            listItem = LayoutInflater.from(context).inflate(R.layout.item_list_img_pair,parent,false)
        }
        with(convertView){
            var image = listItem!!.findViewById<ImageView>(R.id.image)
            var imgPoint = listItem.findViewById<ImageView>(R.id.img_point)

            Glide.with(context)
                .load(ApiConfig.URL_IMAGE +imgData?.gambar)
                .error(R.drawable.img_not_found)
                .into(image)
            image.contentDescription = imgData!!.kata!!.uppercase()
            image.setOnClickListener{onImageClickCallback?.onImageClicked(image,imgPoint)}
        }
        return listItem!!
    }

    fun setOnImageClickCallback(onImageClickCallback: OnImageClickCallback) {
        this.onImageClickCallback = onImageClickCallback
    }

    interface OnImageClickCallback {
        fun onImageClicked(view: ImageView, imgPoint: ImageView)
    }
}
