package com.medicalreport.repository.auth

import com.medicalreport.datasource.auth.AuthDataSource
import com.medicalreport.modal.request.ForgotPasswordRequest
import com.medicalreport.modal.request.LoginRequest
import com.medicalreport.modal.response.ForgotPasswordResponse
import com.medicalreport.modal.response.LoginResponse


class AuthRepositoryImpl(private val authDataSource: AuthDataSource) : AuthRepository {
    override suspend fun login(
        params: LoginRequest,
        onResult: (isSuccess: Boolean, baseResponse: LoginResponse) -> Unit
    ) {
        val response = authDataSource.getLogin(params)
        onResult(response.status, response)
    }

    override suspend fun passwordReset(
        params: ForgotPasswordRequest,
        onResult: (isSuccess: Boolean, baseResponse: ForgotPasswordResponse) -> Unit
    ) {
        val response = authDataSource.getForgotPassword(params)
        onResult(response.status, response)
    }

}