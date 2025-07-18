package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class SearchedPatientDetailResponse(
	@field:SerializedName("data")
	val data: ArrayList<PatientData>? = null,

	@field:SerializedName("status")
	var status: Boolean = false,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null


)
