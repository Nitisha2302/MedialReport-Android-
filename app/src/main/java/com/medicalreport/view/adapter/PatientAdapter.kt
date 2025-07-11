package com.medicalreport.view.adapter

import android.util.Log
import android.view.View
import com.medicalreport.base.BaseRecyclerAdapter
import com.medicalreport.databinding.ItemPatinetProfileBinding
import com.medicalreport.modal.response.PatientData
import com.medicalreport.utils.disableMultiTap

class PatientAdapter(override val layoutId: Int) :
    BaseRecyclerAdapter<ItemPatinetProfileBinding, PatientData>() {
    var patientList: ArrayList<PatientData> = ArrayList()
    var clickListener: ClickListener? = null
    override fun bind(holder: ViewHolder, item: PatientData, position: Int) {
        holder.setIsRecyclable(false)
        holder.binding.tvPatientId.text = item.id.toString()
        holder.binding.tvPatientName.text = item.name.toString()
        holder.binding.tvPatientAge.text = item.age.toString()
        holder.binding.tvNumber.text = item.phone.toString()
        holder.binding.tvBloodGroup.text = item.bloodGroup.toString()
        val date = item.createdAt
        val parts = date?.split("T".toRegex())?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray()
        holder.binding.tvDate.text = parts?.get(0).toString()

        holder.itemView.setOnClickListener {
            it.disableMultiTap()
            item.id?.let { it1 -> clickListener?.onClickView(it, it1) }
        }
        holder.binding.mcvViewPatient.setOnClickListener {
            it.disableMultiTap()
            item.id?.let { it1 -> clickListener?.onClickView(it, it1) }
        }

        holder.binding.mcvEditPatient.setOnClickListener {
            it.disableMultiTap()
            item.id?.let { it1 -> clickListener?.onClickEdit(it, it1) }
        }

        holder.binding.mcvPdfPatient.setOnClickListener {
            it.disableMultiTap()
            item.name?.let { it1 -> clickListener?.onClickPdf(it, it1) }
        }

        holder.binding.mcvSharePdf.setOnClickListener {
            it.disableMultiTap()
            item.name?.let { it1 -> clickListener?.onClickShare(it, it1) }
        }

    }

    fun filterList(filterlist: ArrayList<PatientData>) {
        patientList.clear()
        patientList = filterlist
        Log.e("PatientList", "${patientList}")
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<PatientData>) {
        val start = patientList.size
        patientList.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    interface ClickListener {
        fun onClickView(view: View, id: Int)
        fun onClickEdit(view: View, id: Int)
        fun onClickPdf(view: View, name: String)
        fun onClickShare(view: View, name: String)
    }
}