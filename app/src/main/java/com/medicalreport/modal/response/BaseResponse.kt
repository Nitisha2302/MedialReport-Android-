package com.pentasoft.docportal.modal.response

import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @SerializedName("success")
    var isSuccess: Boolean = false,

    @SerializedName("message")
    var message: String? = null,

    @field:SerializedName("errors")
    val errors: String? = null
)
