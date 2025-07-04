package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class PatientReportResponse(

	@field:SerializedName("data")
	val data: PatientReportData? = null,

	@field:SerializedName("status")
    var success: Boolean? = null,

	@field:SerializedName("message")
	var message: String? = null
)

data class PatientReportData(

	@field:SerializedName("pdf_doc")
	val pdfDoc: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("patient_id")
	val patientId: String? = null,

	@field:SerializedName("doc_image")
	val docImage: ArrayList<String>? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
