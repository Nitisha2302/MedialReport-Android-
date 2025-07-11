package com.medicalreport.view.adapter

import com.medicalreport.base.BaseRecyclerAdapter
import com.medicalreport.databinding.ItemPatientReportListBinding
import com.medicalreport.modal.response.DataItem
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ParticularPatientReportAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<ItemPatientReportListBinding, DataItem>() {
    var clickListener: ClickListener? = null
    override fun bind(holder: ViewHolder, item: DataItem, position: Int) {
        val dateString = item.createdAt
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

        val date: Date
        try {
            date = inputFormat.parse(dateString)
            val formattedDate = outputFormat.format(date)
            holder.binding.tvReportDate.text = formattedDate

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        holder.binding.ivPdf.setOnClickListener {
            clickListener?.onClickPdF(item.fileUrl.toString())

        }
        holder.binding.ivSharePdf.setOnClickListener {
            clickListener?.onClickShare(item.fileUrl.toString())
        }
    }

    interface ClickListener {
        fun onClickPdF(path: String)
        fun onClickShare(path: String)
    }
}