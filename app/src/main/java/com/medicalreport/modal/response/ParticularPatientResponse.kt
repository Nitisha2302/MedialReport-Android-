package com.medicalreport.modal.response

import com.google.gson.annotations.SerializedName

data class ParticularPatientResponse(

    @field:SerializedName("data")
    val data: PProfileData? = null,

    @field:SerializedName("status")
    var status: Boolean = false,

    @field:SerializedName("message")
    var message: String? = null
)

data class PProfileData(

    @field:SerializedName("reference_by")
    val referenceBy: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("age")
    val age: String? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("date_of_birth")
    val dateOfBirth: String? = null,

    @field:SerializedName("weight")
    val weight: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("blood_group")
    val bloodGroup: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("height")
    val height: String? = null,

    @field:SerializedName("report")
    val report: ArrayList<ReportItem>? = null,
)

data class ReportItem(

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

