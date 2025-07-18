package com.medicalreport.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.medicalreport.base.BaseRecyclerAdapter
import com.medicalreport.databinding.ItemPatinetProfileBinding
import com.medicalreport.modal.response.PatientData
import com.medicalreport.utils.disableMultiTap

class PatientAdapter(var context: Context?) : RecyclerView.Adapter<PatientAdapter.ItemViewHolder>() {

    private var patientList: ArrayList<PatientData> = ArrayList()
    var clickListener: ClickListener? = null
    inner class ItemViewHolder(var holder: ItemPatinetProfileBinding) :
        RecyclerView.ViewHolder(holder.root) {

        fun bind(item: PatientData) {

            holder.tvPatientId.text = item.id?.toString() ?: ""
            holder.tvPatientName.text = item.name ?: ""
            holder.tvPatientAge.text = item.age?.toString() ?: ""
            holder.tvNumber.text = item.phone ?: ""
            holder.tvBloodGroup.text = item.bloodGroup ?: ""

            val date = item.createdAt
            val parts = date?.split("T")
            holder.tvDate.text = parts?.getOrNull(0) ?: ""

            holder.root.setOnClickListener {
                it.disableMultiTap()
                item.id?.let { id -> clickListener?.onClickView(it, id) }
            }

            holder.mcvViewPatient.setOnClickListener {
                it.disableMultiTap()
                item.id?.let { id -> clickListener?.onClickView(it, id) }
            }

            holder.mcvEditPatient.setOnClickListener {
                it.disableMultiTap()
                item.id?.let { id -> clickListener?.onClickEdit(it, id) }
            }

            holder.mcvPdfPatient.setOnClickListener {
                it.disableMultiTap()
                item.name?.let { name -> clickListener?.onClickPdf(it, name) }
            }

            holder.mcvSharePdf.setOnClickListener {
                it.disableMultiTap()
                item.name?.let { name -> clickListener?.onClickShare(it, name) }
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val mBinding = ItemPatinetProfileBinding.inflate(inflater, parent, false)
        return ItemViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(patientList[position])

    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    fun setNewItems(newItems: List<PatientData>) {
        patientList.clear()
        patientList.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * Filters the list for search results.
     */
    fun filterList(filteredList: List<PatientData>) {
        patientList.clear()
        patientList.addAll(filteredList)
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onClickView(view: View, id: Int)
        fun onClickEdit(view: View, id: Int)
        fun onClickPdf(view: View, name: String)
        fun onClickShare(view: View, name: String)
    }


}
