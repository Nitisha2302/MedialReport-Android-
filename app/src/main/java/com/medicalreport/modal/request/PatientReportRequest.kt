package com.medicalreport.modal.request

import com.google.gson.annotations.SerializedName

data class PatientReportRequest(

    @field:SerializedName("pdf_doc")
    var pdfDoc: String? = null,

    @field:SerializedName("doc_image")
    var docImage: ArrayList<DocImageItem>? = null,

    @field:SerializedName("content")
    var content: String? = null
)

data class DocImageItem(

    @field:SerializedName("image")
    val image: String? = null
)
