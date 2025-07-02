package com.medicalreport.datasource.auth

import com.medicalreport.modal.request.ForgotPasswordRequest
import com.medicalreport.modal.request.LoginRequest
import com.medicalreport.modal.response.ForgotPasswordResponse
import com.medicalreport.modal.response.LoginResponse
import com.medicalreport.network.ApiService
import com.medicalreport.resource.ResourcesProvider


class AuthDataSourceImpl(
    private val api: ApiService,
    private val resources: ResourcesProvider
) : AuthDataSource {
    override suspend fun getLogin(loginRequest: LoginRequest): LoginResponse {
        var baseResponse = LoginResponse()
        try {
            baseResponse = api.getLogin(loginRequest)
        } catch (e: Exception) {
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getForgotPassword(forgotPasswordRequest: ForgotPasswordRequest): ForgotPasswordResponse {
        var baseResponse = ForgotPasswordResponse()
        try {
            baseResponse = api.getPasswordReset(forgotPasswordRequest)
        } catch (e: Exception) {
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

}