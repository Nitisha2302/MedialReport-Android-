package com.medicalreport.modal.request

import com.google.gson.annotations.SerializedName

data class PatientProfileRequest(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("phone")
    var phone: String? = null,
    @SerializedName("date_of_birth")
    var dob: String? = null,
    @SerializedName("age")
    var age: String? = null,
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("blood_group")
    var bloodGroup: String? = null,

)
