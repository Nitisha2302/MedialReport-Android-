package com.medicalreport.view.adapter

import android.view.View
import com.medicalreport.base.BaseRecyclerAdapter
import com.medicalreport.databinding.ItemRecentReportBinding
import com.medicalreport.modal.response.DataItem

class RecentPatientReportAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<ItemRecentReportBinding, DataItem>() {
    override fun bind(holder: ViewHolder, item: DataItem, position: Int) {
        holder.setIsRecyclable(false)
        holder.binding.tvUserName.text = item.patientName
        if (item.reportUniqeId.isNullOrEmpty()) {
            holder.binding.tvEmailId.visibility = View.GONE
        } else {
            holder.binding.tvEmailId.visibility = View.VISIBLE
            holder.binding.tvEmailId.text = item.reportUniqeId
        }

    }

}


