package com.medicalreport.repository.home

import com.medicalreport.modal.request.DoctorProfileRequest
import com.medicalreport.modal.request.PatientProfileRequest
import com.medicalreport.modal.response.DocProfileResponse
import com.medicalreport.modal.response.DoctorsDetailResponse
import com.medicalreport.modal.response.LogoutResponse
import com.medicalreport.modal.response.PatientProfileResponse

interface HomeRepository {

    suspend fun getDoctorProfile(
        onResult: (isSuccess: Boolean, baseResponse: DocProfileResponse) -> Unit
    )

    suspend fun updateDoctor(
        params: DoctorProfileRequest,
        onResult: (isSuccess: Boolean, baseResponse: DocProfileResponse) -> Unit
    )

    suspend fun createPatientProfile(
        params: PatientProfileRequest,
        onResult: (isSuccess: Boolean, baseResponse: PatientProfileResponse) -> Unit
    )

    suspend fun logout(
        onResult: (isSuccess: Boolean, baseResponse: LogoutResponse) -> Unit
    )
}