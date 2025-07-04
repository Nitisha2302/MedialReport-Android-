package com.medicalreport.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.medicalreport.R
import com.medicalreport.databinding.ItemReportCreationBinding
import com.medicalreport.modal.response.ImageData

class ReportCreationAdapter(private val imageList: MutableList<ImageData>) :
    RecyclerView.Adapter<ReportCreationAdapter.ReportImageViewHolder>() {

    inner class ReportImageViewHolder(binding: ItemReportCreationBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        var binding: ItemReportCreationBinding

        init {
            this.binding = binding
        }

    }

    override fun onCreateViewHolder(holder: ViewGroup, position: Int): ReportImageViewHolder {
        val binding: ItemReportCreationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(holder.context),
            R.layout.item_report_creation,
            holder,
            false
        )
        return ReportImageViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ReportImageViewHolder, position: Int) {
        val image = imageList[position]
        holder.binding.ivReport.setImageURI(image.image)

    }
}