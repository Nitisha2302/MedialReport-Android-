package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class PatientResponse(

    @field:SerializedName("data")
    val data: ArrayList<PatientData>? = null,

    @field:SerializedName("success")
    var status: Boolean = false,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("per_page")
    val perPage: Int? = null,

    @field:SerializedName("total")
    val total: Int? = null,

    @field:SerializedName("last_page")
    val lastPage: Int? = null,

    @field:SerializedName("current_page")
    val currentPage: Int? = null
)

data class PatientData(

    @field:SerializedName("reference_by")
    val referenceBy: String? = null,

    @field:SerializedName("image")
    var image: String? = null,

    @field:SerializedName("address")
    var address: String? = null,

    @field:SerializedName("gender")
    var gender: String? = null,

    @field:SerializedName("date_of_birth")
    var dateOfBirth: String? = null,

    @field:SerializedName("age")
    var age: String? = null,

    @field:SerializedName("weight")
    var weight: String? = null,

    @field:SerializedName("created_at")
    var createdAt: String? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    var userId: Int? = null,

    @field:SerializedName("phone")
    var phone: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("blood_group")
    var bloodGroup: String? = null,

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("email")
    var email: String? = null,

    @field:SerializedName("height")
    var height: String? = null,

    @field:SerializedName("report")
    val report: ArrayList<PatientReportItem>? = null,
)

data class PatientReportItem(

    @field:SerializedName("pdf_doc")
    val pdfDoc: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("patient_id")
    val patientId: Int? = null,

    @field:SerializedName("doc_image")
    val docImage: ArrayList<String>? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("content")
    val content: String? = null
)