package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class DoctorProfileResponse(

    @field:SerializedName("data")
    val data: DoctorProfileData? = null,

    @field:SerializedName("status")
    var status: Boolean = false,

    @field:SerializedName("message")
    var message: String? = null
)

data class DoctorProfileData(

    @field:SerializedName("specialized")
    var specialized: String? = null,

    @field:SerializedName("image")
    var image: String? = null,

    @field:SerializedName("address")
    var address: String? = null,

    @field:SerializedName("gender")
    var gender: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("bio")
    var bio: String? = null,

    @field:SerializedName("email_verified_at")
    val emailVerifiedAt: Any? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("phone")
    var phone: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("email")
    var email: String? = null,

    @field:SerializedName("hospital_phone")
    var hospitalPhone: String? = null,

    @field:SerializedName("hospital_logo")
    var hospitalLogo: String? = null,

    @field:SerializedName("hospital_address")
    var hospitalAddress: String? = null,

    @field:SerializedName("username")
    var username: String? = null,

    @field:SerializedName("hospital_name")
    var hospitalName: String? = null
)
