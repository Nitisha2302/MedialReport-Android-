package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    var data: LoginData? = null,

    @SerializedName("status")
    var status: Boolean = false,

    @field:SerializedName("errors")
    val errors: String? = null,

    @SerializedName("message")
    var message: String? = null

)

data class LoginData(
    @SerializedName("specialized")
    var specialized: String? = null,

    @SerializedName("image")
    var image: String? = null,

    @SerializedName("address")
    var address: String? = null,

    @SerializedName("role")
    var role: String? = null,

    @SerializedName("gender")
    var gender: String? = null,

    @SerializedName("created_at")
    var createdAt: String? = null,

    @SerializedName("bio")
    var bio: String? = null,

    @SerializedName("email_verified_at")
    var emailVerifiedAt: Any? = null,

    @SerializedName("deleted_at")
    var deletedAt: Any? = null,

    @JvmField
    @SerializedName("api_token")
    var token: String? = null,

    @SerializedName("updated_at")
    var updatedAt: String? = null,

    @SerializedName("phone")
    var phone: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("username")
    var username: String? = null,

    @field:SerializedName("hospital_phone")
    var hospitalPhone: String? = null,

    @field:SerializedName("hospital_logo")
    var hospitalLogo: String? = null,

    @field:SerializedName("hospital_address")
    var hospitalAddress: String? = null,

    @field:SerializedName("hospital_name")
    var hospitalName: String? = null,

    @field:SerializedName("current_plan")
    val currentPlan: UserCurrentPlan? = null
)

data class UserCurrentPlan(

    @field:SerializedName("doctor_id")
    val doctorId: Int? = null,

    @field:SerializedName("end_date_time")
    val endDateTime: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("start_date_time")
    val startDateTime: String? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("plan")
    val plan: UserPlan? = null,

    @field:SerializedName("plan_id")
    val planId: Int? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class UserPlan(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("validity")
    val validity: String? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("status")
    val status: String? = null
)
