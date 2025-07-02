package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(

    @field:SerializedName("data")
    val data: FPasswordData? = null,

    @field:SerializedName("success")
    var status: Boolean = false,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("errors")
    val errors: String? = null
)

data class FPasswordData(
    val any: Any? = null
)
