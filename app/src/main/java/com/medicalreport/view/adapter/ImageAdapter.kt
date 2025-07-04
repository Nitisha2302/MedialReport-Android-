package com.medicalreport.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.medicalreport.R
import com.medicalreport.base.clickListener.AdapterClickListener
import com.medicalreport.databinding.PhotoUploadItemViewBinding
import com.medicalreport.modal.response.ImageData


class ImageAdapter(
    private val imageList: MutableList<ImageData>,
    adapterClickListener: AdapterClickListener
) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    var adapterClickListener: AdapterClickListener

    init {
        this.adapterClickListener = adapterClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding: PhotoUploadItemViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.photo_upload_item_view,
            parent,
            false
        )
        return ImageViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = imageList[position]
        holder.binding.ivUploadPhoto.setImageURI(image.image)
        holder.bind(position, image, adapterClickListener)

    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ImageViewHolder(binding: PhotoUploadItemViewBinding) :
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