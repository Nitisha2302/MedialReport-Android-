package com.medicalreport.view.adapter

import com.medicalreport.R
import com.medicalreport.base.BaseRecyclerAdapter
import com.medicalreport.databinding.ItemReportImagesBinding
import com.medicalreport.utils.frescoImageLoad

class ReportHistoryAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<ItemReportImagesBinding, String>() {
    override fun bind(holder: ViewHolder, item: String, position: Int) {
        holder.setIsRecyclable(false)
        holder.binding.ivReport.setController(
            frescoImageLoad(
                item,
                R.drawable.image_placeholder,
                holder.binding.ivReport,
                false
            )
        )


    }
}