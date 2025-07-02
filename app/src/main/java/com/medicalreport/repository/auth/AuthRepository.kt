package com.medicalreport.repository.auth

import com.medicalreport.modal.request.ForgotPasswordRequest
import com.medicalreport.modal.request.LoginRequest
import com.medicalreport.modal.response.ForgotPasswordResponse
import com.medicalreport.modal.response.LoginResponse


interface AuthRepository {

    suspend fun login(
        params: LoginRequest,
        onResult: (isSuccess: Boolean, baseResponse: LoginResponse) -> Unit
    )

    suspend fun passwordReset(
        params: ForgotPasswordRequest,
        onResult: (isSuccess: Boolean, baseResponse: ForgotPasswordResponse) -> Unit
    )

}