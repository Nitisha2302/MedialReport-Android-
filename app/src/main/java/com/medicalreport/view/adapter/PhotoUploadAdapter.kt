package com.medicalreport.view.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.medicalreport.R
import com.medicalreport.base.clickListener.AdapterClickListener
import com.medicalreport.databinding.PhotoUploadItemViewBinding


class PhotoUploadAdapter(var list: ArrayList<String>, adapterClickListener: AdapterClickListener) :
    RecyclerView.Adapter<PhotoUploadAdapter.CustomViewHolder>() {
    var adapterClickListener: AdapterClickListener
    var context: Context? = null

    init {
        this.adapterClickListener = adapterClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewtype: Int): CustomViewHolder {
        val binding: PhotoUploadItemViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.photo_upload_item_view,
            viewGroup,
            false
        )
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, i: Int) {
        val item = list[i]
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(item))
            .setProgressiveRenderingEnabled(true)
            .build()
        val draweeController: DraweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest)
            .build()
        holder.binding.ivUploadPhoto.controller = draweeController
        holder.bind(i, item, adapterClickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class CustomViewHolder(binding: PhotoUploadItemViewBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        var binding: PhotoUploadItemViewBinding

        init {
            this.binding = binding
        }

        fun bind(
            pos: Int, item: Any?,
            adapterClickListener: AdapterClickListener
        ) {
            binding.ivDeletePhoto.setOnClickListener { v ->
                adapterClickListener.onItemClick(
                    v,
                    pos,
                    item
                )
            }
        }
    }
}