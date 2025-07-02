package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class NotificationsResponse(

	@field:SerializedName("data")
	val data: ArrayList<NotificationsDataItem>? = null,

	@field:SerializedName("success")
	var success: Boolean? = null,

	@field:SerializedName("message")
	var message: String? = null
)

data class NotificationsDataItem(

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("nid")
	val nid: Int? = null,

	@field:SerializedName("read_at")
	val readAt: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("notifiable_id")
	val notifiableId: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("notifiable_type")
	val notifiableType: String? = null
)
