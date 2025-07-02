package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class DocProfileResponse(

    @field:SerializedName("data")
    val data: DocData? = null,

    @field:SerializedName("success")
    var success: Boolean = false,

    @field:SerializedName("message")
    var message: String? = null
)

data class DocData(

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
    val emailVerifiedAt: Any? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

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
    val username: String? = null,

    @field:SerializedName("hospital_name")
    var hospital_name: String? = null,

    @field:SerializedName("hospital_address")
    var hospital_address: String? = null,

    @field:SerializedName("hospital_phone")
    var hospital_phone: String? = null,

    @field:SerializedName("hospital_logo")
    var hospital_logo: String? = null,
)
