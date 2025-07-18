package com.medicalreport.repository.home

import android.text.TextUtils
import com.medicalreport.datasource.home.HomeDataSource
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
import com.medicalreport.utils.createPartFromString
import com.medicalreport.utils.toImageRequestBody
import com.medicalreport.utils.toIntRequestBody
import com.medicalreport.utils.toPdfRequestBody
import com.medicalreport.utils.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class HomeRepositoryImpl(private val homeDataSource: HomeDataSource) : HomeRepository {
    override suspend fun getDoctorProfile(onResult: (Boolean, DocProfileResponse) -> Unit) {
        val response = homeDataSource.getDoctorProfile()
        response.status?.let { onResult(it, response) }

    }

    override suspend fun updateDoctor(
        params: DoctorProfileRequest,
        onResult: (Boolean, DocProfileResponse) -> Unit
    ) {
        val response =
            homeDataSource.updateDoctor(getDoctorProfileParam(params), getImagePart(params))
        response.status?.let { onResult(it, response) }
    }

    override suspend fun getPatientList(
        param: Int,
        onResult: (Boolean, PatientResponse) -> Unit
    ) {
        val response = homeDataSource.getPatientList(param)
        response.status.let { onResult(it, response) }
    }

    override suspend fun createPatientProfile(
        params: PatientProfileRequest,
        onResult: (isSuccess: Boolean, baseResponse: PatientProfileResponse) -> Unit
    ) {
        val response = homeDataSource.patientProfile(
            getPatientProfileParam(params),
            getPatientImagePart(params)
        )
        onResult(response.success, response)
    }

    override suspend fun updatePatientProfile(
        patientId: Int,
        params: UpdatePatientProfileRequest,
        onResult: (Boolean, PatientProfileResponse) -> Unit
    ) {
        val response = homeDataSource.updatePatientProfile(patientId, params)
        response.success?.let { onResult(it, response) }
    }

    override suspend fun getPatientProfile(
        param: Int,
        onResult: (Boolean, ParticularPatientResponse) -> Unit
    ) {
        val response = homeDataSource.getPatientProfile(param)
        onResult(response.status, response)
    }

    override suspend fun logout(onResult: (Boolean, LogoutResponse) -> Unit) {
        val response = homeDataSource.logout()
        onResult(response.status, response)
    }

    override suspend fun getParticularPatientReportList(
        params: Int,
        onResult: (isSuccess: Boolean, baseResponse: PatientReportListResponse) -> Unit
    ) {
        val response = homeDataSource.getParticularPatientReportList(params)
        response.status?.let { onResult(it, response) }

    }

    override suspend fun updatePatientReport(
        params: PatientReportRequest,
        onResult: (isSuccess: Boolean, baseResponse: PatientReportResponse) -> Unit
    ) {
        val response = homeDataSource.updatePatientReport(
            getPatientReportParam(params),
            getPatientReportFilePart(params)
        )
        response.success?.let { onResult(it, response) }
    }

    private fun getPatientReportParam(params: PatientReportRequest): Map<String, RequestBody> {
        val map: MutableMap<String, RequestBody> = HashMap()
        if (params.patientId != 0) {
            map["patient_id"] = params.patientId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        }
        return map
    }


    override suspend fun getDoctorsDetailList(onResult: (isSuccess: Boolean, baseResponse: DoctorsDetailResponse) -> Unit) {
        val response = homeDataSource.getDoctorsDetailList()
        response.success?.let { onResult(it, response) }
    }

    override suspend fun getPatientReportList(onResult: (Boolean, PatientReportListResponse) -> Unit) {
        val response = homeDataSource.getPatientReportList()
        response.status?.let { onResult(it, response) }
    }

    private fun getDoctorProfileParam(params: DoctorProfileRequest): Map<String?, RequestBody> {
        val map: MutableMap<String?, RequestBody> = HashMap()
        if (!params.name.isNullOrEmpty()) {
            map["name"] = toRequestBody(params.name.toString())
        }
        if (!params.email.isNullOrEmpty()) {
            map["email"] = toRequestBody(params.email.toString())
        }
        if (!params.username.isNullOrEmpty()) {
            map["username"] = toRequestBody(params.username.toString())
        }
        if (!params.phone.isNullOrEmpty()) {
            map["phone"] = toRequestBody(params.phone.toString())
        }
        if (!params.address.isNullOrEmpty()) {
            map["address"] = toRequestBody(params.address.toString())
        }

        if (!params.specialized.isNullOrEmpty()) {
            map["specialized"] = toRequestBody(params.specialized.toString())
        }

        if (!params.gender.isNullOrEmpty()) {
            map["gender"] = toRequestBody(params.gender.toString())
        }
        if (!params.bio.isNullOrEmpty()) {
            map["bio"] = toRequestBody(params.bio.toString())
        }
        if (!params.image.isNullOrEmpty()) {
            map["image"] = toRequestBody(params.image.toString())
        }
        if (!params.hospital_logo.isNullOrEmpty()) {
            map["hospital_logo"] = toRequestBody(params.hospital_logo.toString())
        }
        if (!params.hospital_name.isNullOrEmpty()) {
            map["hospital_name"] = toRequestBody(params.hospital_name.toString())
        }
        if (!params.hospital_address.isNullOrEmpty()) {
            map["hospital_address"] = toRequestBody(params.hospital_address.toString())
        }
        if (!params.hospital_phone.isNullOrEmpty()) {
            map["hospital_phone"] = toRequestBody(params.hospital_phone.toString())
        }

        return map
    }

    private fun getImagePart(params: DoctorProfileRequest): MultipartBody.Part? {
        return if (!TextUtils.isEmpty(params.image)) {
            val file = File(params.image!!)
            val imageBody = toImageRequestBody(file)
            MultipartBody.Part.createFormData("image", file.name, imageBody)
        } else {
            null
        }
    }

    private fun getPatientProfileParam(params: PatientProfileRequest): Map<String?, RequestBody> {
        val map: MutableMap<String?, RequestBody> = HashMap()
        val boundary = createPartFromString(params.name.toString())
        map.put("boundary", boundary)
        if (!params.name.isNullOrEmpty()) {
            val name = createPartFromString(params.name.toString())
            map.put("name", name)
        }
        if (!params.email.isNullOrEmpty()) {
            val email = createPartFromString(params.email.toString())
            map.put("email", email)
        }
        if (!params.phone.isNullOrEmpty()) {
            val phone = createPartFromString(params.phone.toString())
            map.put("phone", phone)
        }
        if (!params.address.isNullOrEmpty()) {
            val address = createPartFromString(params.address.toString())
            map.put("address", address)
        }
        if (!params.gender.isNullOrEmpty()) {
            val gender = createPartFromString(params.gender.toString())
            map.put("gender", gender)
        }
        if (!params.bloodGroup.isNullOrEmpty()) {
            val bloodGroup = createPartFromString(params.bloodGroup.toString())
            map.put("blood_group", bloodGroup)
        }
        if (!params.dob.isNullOrEmpty()) {
            val dob = createPartFromString(params.dob.toString())
            map.put("date_of_birth", dob)
        }
        if (!params.age.isNullOrEmpty()) {
            val age = createPartFromString(params.age.toString())
            map.put("age", age)
        }

        return map
    }

    private fun getPatientImagePart(params: PatientProfileRequest): MultipartBody.Part? {
        return if (!TextUtils.isEmpty(params.image)) {
            val file = params.image?.let { File(it) }
            val imageBody = file?.let { toImageRequestBody(it) }
            imageBody?.let { MultipartBody.Part.createFormData("image", file.name, it) }
        } else {
            null
        }
    }

    private fun getPatientReportFilePart(params: PatientReportRequest): MultipartBody.Part? {
        return if (!TextUtils.isEmpty(params.pdfFile)) {
            val file = params.pdfFile?.let { File(it) }
            if (file?.exists() == true) {
                val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", file.name, requestBody)
            } else {
                null
            }
        } else {
            null
        }
    }


}