package com.medicalreport.datasource.home


import com.medicalreport.modal.request.PatientReportRequest
import com.medicalreport.modal.request.UpdatePatientProfileRequest
import com.medicalreport.modal.response.DashboardResponse
import com.medicalreport.modal.response.DocProfileResponse
import com.medicalreport.modal.response.DoctorProfileResponse
import com.medicalreport.modal.response.DoctorsDetailResponse
import com.medicalreport.modal.response.LogoutResponse
import com.medicalreport.modal.response.NotificationsResponse
import com.medicalreport.modal.response.ParticularPatientResponse
import com.medicalreport.modal.response.PatientProfileResponse
import com.medicalreport.modal.response.PatientReportListResponse
import com.medicalreport.modal.response.PatientReportResponse
import com.medicalreport.modal.response.PatientResponse
import com.medicalreport.modal.response.SearchResponse
import com.medicalreport.modal.response.SearchedPatientDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface HomeDataSource {
    suspend fun logout(): LogoutResponse

    suspend fun getDoctorProfile(): DoctorProfileResponse

    suspend fun updateDoctor(
        params: Map<String?, RequestBody>,
        imagePart: MultipartBody.Part?
    ): DocProfileResponse

    suspend fun getPatientList(): PatientResponse

    suspend fun patientProfile(
        params: Map<String?, RequestBody>,
        imagePart: MultipartBody.Part?
    ): PatientProfileResponse

    suspend fun updatePatientReport(
        patientId: Int,
        params: PatientReportRequest
    ): PatientReportResponse

    suspend fun updatePatientProfile(
        patientId: Int,
        params: UpdatePatientProfileRequest
    ): PatientProfileResponse

    suspend fun getDashboardData(): DashboardResponse

    suspend fun getSearch(param: String): SearchResponse

    suspend fun getPatientProfile(param: Int): ParticularPatientResponse

    suspend fun getNotifications(): NotificationsResponse

    suspend fun getPatientReportList(): PatientReportListResponse

    suspend fun getParticularPatientReportList(param: Int): PatientReportListResponse

    suspend fun getDoctorsDetailList(): DoctorsDetailResponse

    suspend fun getSearchedPatientData(param: String): SearchedPatientDetailResponse
}