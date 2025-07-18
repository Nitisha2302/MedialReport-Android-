package com.medicalreport.repository.home

import com.medicalreport.modal.request.DoctorProfileRequest
import com.medicalreport.modal.request.PatientProfileRequest
import com.medicalreport.modal.request.PatientReportRequest
import com.medicalreport.modal.request.UpdatePatientProfileRequest
import com.medicalreport.modal.response.DocProfileResponse
import com.medicalreport.modal.response.DoctorsDetailResponse
import com.medicalreport.modal.response.LogoutResponse
import com.medicalreport.modal.response.ParticularPatientResponse
import com.medicalreport.modal.response.PatientProfileResponse
import com.medicalreport.modal.response.PatientReportListResponse
import com.medicalreport.modal.response.PatientReportResponse
import com.medicalreport.modal.response.PatientResponse
import com.medicalreport.modal.response.SearchedPatientDetailResponse

interface HomeRepository {

    suspend fun getDoctorProfile(
        onResult: (isSuccess: Boolean, baseResponse: DocProfileResponse) -> Unit
    )

    suspend fun updateDoctor(
        params: DoctorProfileRequest,
        onResult: (isSuccess: Boolean, baseResponse: DocProfileResponse) -> Unit
    )

    suspend fun getPatientList(
        param: Int,
        onResult: (isSuccess: Boolean, baseResponse: PatientResponse) -> Unit
    )

    suspend fun createPatientProfile(
        params: PatientProfileRequest,
        onResult: (isSuccess: Boolean, baseResponse: PatientProfileResponse) -> Unit
    )

    suspend fun updatePatientProfile(
        patientId: Int,
        params: UpdatePatientProfileRequest,
        onResult: (isSuccess: Boolean, baseResponse: PatientProfileResponse) -> Unit
    )

    suspend fun getPatientProfile(
        param: Int,
        onResult: (isSuccess: Boolean, baseResponse: ParticularPatientResponse) -> Unit
    )

    suspend fun getPatientReportList(
        onResult: (isSuccess: Boolean, baseResponse: PatientReportListResponse) -> Unit
    )

    suspend fun getDoctorsDetailList(
        onResult: (isSuccess: Boolean, baseResponse: DoctorsDetailResponse) -> Unit
    )

    suspend fun updatePatientReport(
        params: PatientReportRequest,
        onResult: (isSuccess: Boolean, baseResponse: PatientReportResponse) -> Unit
    )

    suspend fun getParticularPatientReportList(
        params: Int,
        onResult: (isSuccess: Boolean, baseResponse: PatientReportListResponse) -> Unit
    )

    suspend fun getSearchedPatientData(
        patientName:String,
        page: Int,
        onResult: (isSuccess: Boolean, baseResponse: SearchedPatientDetailResponse) -> Unit

    )

    suspend fun logout(
        onResult: (isSuccess: Boolean, baseResponse: LogoutResponse) -> Unit
    )
}