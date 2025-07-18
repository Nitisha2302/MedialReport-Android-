package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class DoctorsDetailResponse(

	@field:SerializedName("data")
	val data: ArrayList<DoctorsDataItem>? = null,

	@field:SerializedName("status")
	var success: Boolean? = null,

	@field:SerializedName("message")
	var message: String? = null
)

data class DoctorsDataItem(

	@field:SerializedName("specialized")
	val specialized: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("bio")
	val bio: String? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
