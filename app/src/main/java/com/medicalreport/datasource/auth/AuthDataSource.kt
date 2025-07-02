package com.medicalreport.datasource.auth

import com.medicalreport.modal.request.ForgotPasswordRequest
import com.medicalreport.modal.request.LoginRequest
import com.medicalreport.modal.response.ForgotPasswordResponse
import com.medicalreport.modal.response.LoginResponse


interface AuthDataSource {
    suspend fun getLogin(loginRequest: LoginRequest): LoginResponse
    suspend fun getForgotPassword(forgotPasswordRequest: ForgotPasswordRequest): ForgotPasswordResponse

}