package com.medicalreport.modal.request

import com.google.gson.annotations.SerializedName

data class DoctorProfileRequest(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("username")
    var username: String? = null,
    @SerializedName("phone")
    var phone: String? = null,
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("specialized")
    var specialized: String? = null,
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("bio")
    var bio: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("hospital_name")
    var hospital_name: String? = null,
    @SerializedName("hospital_address")
    var hospital_address: String? = null,
    @SerializedName("hospital_phone")
    var hospital_phone: String? = null,
    @SerializedName("hospital_logo")
    var hospital_logo: String? = null,
)