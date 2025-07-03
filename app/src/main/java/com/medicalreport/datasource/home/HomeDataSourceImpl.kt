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
import com.medicalreport.network.ApiService
import com.medicalreport.resource.ResourcesProvider
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HomeDataSourceImpl(
    private val api: ApiService,
    private val resources: ResourcesProvider
) : HomeDataSource {
    override suspend fun logout(): LogoutResponse {
        var baseResponse = LogoutResponse()
        try {
            baseResponse = api.logout()
        } catch (e: Exception) {
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getDoctorProfile(): DocProfileResponse {
        var baseResponse = DocProfileResponse()
        try {
            baseResponse = api.getDoctorProfile()
        } catch (e: Exception) {
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun updateDoctor(
        params: Map<String?, RequestBody>,
        imagePart: MultipartBody.Part?
    ): DocProfileResponse {
        var baseResponse = DocProfileResponse()
        try {
            baseResponse = api.updateDoctor(params, imagePart)
            baseResponse.status = true
        } catch (e: Exception) {
            baseResponse.status = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getPatientList(): PatientResponse {
        var baseResponse = PatientResponse()
        try {
            baseResponse = api.getPatientList()
            baseResponse.status = true
        } catch (e: Exception) {
            baseResponse.status = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun patientProfile(
        params: Map<String?, RequestBody>,
        imagePart: MultipartBody.Part?
    ): PatientProfileResponse {
        var baseResponse = PatientProfileResponse()
        try {
            baseResponse = api.patientProfile(params, imagePart)
            baseResponse.success = true
        } catch (e: Exception) {
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun updatePatientReport(
        patientId: Int,
        params: PatientReportRequest
    ): PatientReportResponse {
        var baseResponse = PatientReportResponse()
        try {
            baseResponse = api.updatePatientReport(patientId, params)
            baseResponse.success = true
        } catch (e: Exception) {
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun updatePatientProfile(
        patientId: Int,
        params: UpdatePatientProfileRequest
    ): PatientProfileResponse {
        var baseResponse = PatientProfileResponse()
        try {
            baseResponse = api.updatePatientProfile(patientId, params)
            baseResponse.success = true
        } catch (e: Exception) {
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getDashboardData(): DashboardResponse {
        var baseResponse = DashboardResponse()
        try {
            baseResponse = api.getDashboardData()
            baseResponse.success = true
        } catch (e: Exception) {
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getSearch(param: String): SearchResponse {
        var baseResponse = SearchResponse()
        try {
            baseResponse = api.getSearch(param)
            baseResponse.success = true
        } catch (e: Exception) {
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getPatientProfile(param: Int): ParticularPatientResponse {
        var baseResponse = ParticularPatientResponse()
        try {
            baseResponse = api.getPatientProfile(param)
            baseResponse.status = true
        } catch (e: Exception) {
            baseResponse.status = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getNotifications(): NotificationsResponse {
        var baseResponse = NotificationsResponse()
        try {
            baseResponse = api.getNotifications()
            baseResponse.success = true
        } catch (e: Exception) {
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getPatientReportList(): PatientReportListResponse {
        var baseResponse = PatientReportListResponse()
        try {
            baseResponse = api.getReportList()
            baseResponse.success = true
        } catch (e: Exception) {
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getParticularPatientReportList(param: Int): PatientReportListResponse {
        var baseResponse = PatientReportListResponse()
        try {
            baseResponse = api.getParticularPatientReportList(param)
            baseResponse.success = true
        } catch (e: Exception) {
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getDoctorsDetailList(): DoctorsDetailResponse {
        var baseResponse = DoctorsDetailResponse()
        try {
            baseResponse = api.getDoctorsList()
            baseResponse.success = true
        } catch (e: Exception) {
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e, resources)
        } finally {
            return baseResponse
        }
    }

    override suspend fun getSearchedPatientData(param: String): SearchedPatientDetailResponse {
        var baseResponse = SearchedPatientDetailResponse()
        try {
            baseResponse = api.getSearchedData(param)
            baseResponse.success = true
        }catch (e:Exception){
            baseResponse.success = false
            baseResponse.message = ApiService.getErrorMessageFromGenericResponse(e,resources)
        }finally {
            return baseResponse
        }
    }
}