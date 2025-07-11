package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class PatientReportListResponse(

	@field:SerializedName("data")
	val data: ArrayList<DataItem>? = null,

	@field:SerializedName("status")
	var status: Boolean? = null,

	@field:SerializedName("message")
	var message: String? = null
)

data class DataItem(

	@field:SerializedName("doctor_id")
	val doctorId: Int? = null,

	@field:SerializedName("file_url")
	val fileUrl: String? = null,

	@field:SerializedName("patientName")
	val patientName: String? = null,

	@field:SerializedName("report_images")
	val reportImages: ArrayList<String>? = null,

	@field:SerializedName("file")
	val file: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("patient_id")
	val patientId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: String? = null,

	@field:SerializedName("patientUniqueId")
	val patientUniqueId: String? = null,

	@field:SerializedName("report_uniqe_id")
	val reportUniqeId: String? = null
)

