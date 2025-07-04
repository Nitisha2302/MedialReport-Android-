package com.medicalreport.network

import com.medicalreport.R
import com.medicalreport.modal.request.ForgotPasswordRequest
import com.medicalreport.modal.request.LoginRequest
import com.medicalreport.modal.request.PatientReportRequest
import com.medicalreport.modal.request.UpdatePatientProfileRequest
import com.medicalreport.modal.response.DashboardResponse
import com.medicalreport.modal.response.DocProfileResponse
import com.medicalreport.modal.response.DoctorProfileResponse
import com.medicalreport.modal.response.DoctorsDetailResponse
import com.medicalreport.modal.response.ForgotPasswordResponse
import com.medicalreport.modal.response.LoginResponse
import com.medicalreport.modal.response.LogoutResponse
import com.medicalreport.modal.response.NotificationsResponse
import com.medicalreport.modal.response.ParticularPatientResponse
import com.medicalreport.modal.response.PatientProfileResponse
import com.medicalreport.modal.response.PatientReportListResponse
import com.medicalreport.modal.response.PatientReportResponse
import com.medicalreport.modal.response.PatientResponse
import com.medicalreport.modal.response.SearchResponse
import com.medicalreport.modal.response.SearchedPatientDetailResponse
import com.medicalreport.resource.ResourcesProvider
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException


interface ApiService {

    @POST("login")
    suspend fun getLogin(@Body request: LoginRequest): LoginResponse

    @POST("forgot-password")
    suspend fun getPasswordReset(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    @GET("doctor/profile")
    suspend fun getDoctorProfile(): DocProfileResponse

    @Multipart
    @POST("doctor/update")
    @JvmSuppressWildcards
    suspend fun updateDoctor(
        @PartMap params: Map<String?, RequestBody>,
        @Part file: MultipartBody.Part?
    ): DocProfileResponse

    @GET("patient/list")
    suspend fun getPatientList(): PatientResponse

    @Multipart
    @POST("patient/create")
    @JvmSuppressWildcards
    suspend fun patientProfile(
        @PartMap params: Map<String?, RequestBody>,
        @Part file: MultipartBody.Part?
    ): PatientProfileResponse

    @GET("dashboard")
    suspend fun getDashboardData(): DashboardResponse

    @POST("logout")
    suspend fun logout(): LogoutResponse

    @GET("search")
    suspend fun getSearch(@Query("search") search: String): SearchResponse

    @GET("patient/{id}")
    suspend fun getPatientProfile(@Path("id") id: Int): ParticularPatientResponse

    @POST("patient/update/{id}")
    suspend fun updatePatientProfile(
        @Path("id") id: Int,
        @Body request: UpdatePatientProfileRequest
    ): PatientProfileResponse

    @Multipart
    @POST("report/create")
    suspend fun updatePatientReport(
        @PartMap params: Map<String?, RequestBody>,
        @Part file: MultipartBody.Part?
    ): PatientReportResponse

    @GET("notifications")
    suspend fun getNotifications(): NotificationsResponse

    @GET("report/list")
    suspend fun getReportList(): PatientReportListResponse

    @GET("report/list/{id}")
    suspend fun getParticularPatientReportList(@Path("id") id: Int): PatientReportListResponse

    @POST("doctor/listing")
    suspend fun getDoctorsList(): DoctorsDetailResponse

    @GET("patient/list/search")
    suspend fun getSearchedData(@Query("search") search: String): SearchedPatientDetailResponse

    companion object {

        fun getErrorMessageFromGenericResponse(
            exception: Exception,
            resources: ResourcesProvider,
        ): String {
            var error = ""
            try {
                when (exception) {
                    is HttpException -> {
                        error = resources.getString(R.string.apiResponseErr)
                    }

                    is ConnectException -> {
                        error = resources.getString(R.string.connectErr)
                    }

                    is SocketTimeoutException -> {
                        error = resources.getString(R.string.timeoutErr)
                    }

                }
            } catch (e: IOException) {
                e.printStackTrace()
                error = resources.getString(R.string.serverErr)
            } finally {
                if (error.isEmpty()) {
                    error = exception.message.toString()
                }
                return error
            }
        }
    }
}