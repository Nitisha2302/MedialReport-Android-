package com.medicalreport.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.medicalreport.R
import com.medicalreport.modal.response.DoctorsDataItem
import com.medicalreport.modal.response.SelectedDoctorsResponse

class DoctorAdapter(private val doctors: List<DoctorsDataItem>) :
RecyclerView.Adapter<DoctorAdapter.ViewHolder>() {
    private val filterDoctorsItem = mutableListOf<SelectedDoctorsResponse>()
    private val selectedDoctors = arrayListOf<SelectedDoctorsResponse>()
    var clickListener: ClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        doctors.forEach {
            it.id?.let { it1 ->
                it.specialized?.let { it2 ->
                    it.name?.let { it3 ->
                        SelectedDoctorsResponse(
                            it1, it3,
                            it2
                        )
                    }
                }
            }?.let { it2 -> filterDoctorsItem.add(it2) }
        }
        val doctor = filterDoctorsItem[position]

        holder.nameTextView.text = doctor.name

        holder.itemView.setOnClickListener {
            if (selectedDoctors.contains(doctor)) {
                selectedDoctors.remove(doctor)
                holder.itemView.setBackgroundColor(Color.WHITE)
            } else {
                if (selectedDoctors.size < 1) {
                    selectedDoctors.add(doctor)
                    holder.itemView.setBackgroundColor(Color.LTGRAY)
                    clickListener?.onClickSelectedDoctors(selectedDoctors)
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "You can select up to 1 doctors",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return doctors.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    }

    interface ClickListener {
        fun onClickSelectedDoctors(selectedDoctors: ArrayList<SelectedDoctorsResponse>)
    }
}

