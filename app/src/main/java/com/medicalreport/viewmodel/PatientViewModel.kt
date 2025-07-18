package com.medicalreport.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medicalreport.base.ParentViewModel
import com.medicalreport.modal.request.DoctorProfileRequest
import com.medicalreport.modal.request.PatientProfileRequest
import com.medicalreport.modal.request.PatientReportRequest
import com.medicalreport.modal.request.UpdatePatientProfileRequest
import com.medicalreport.modal.response.DataItem
import com.medicalreport.modal.response.PProfileData
import com.medicalreport.modal.response.PatientData
import com.medicalreport.modal.response.PatientProfileData
import com.medicalreport.repository.home.HomeRepository
import com.pentasoft.docportal.utils.Scope
import kotlinx.coroutines.launch
import java.io.File

class PatientViewModel(private val homeRepository: HomeRepository) : ParentViewModel() {
    var patientProfileRequest = ObservableField(PatientProfileRequest())
    var updatePatientProfileRequest = ObservableField(UpdatePatientProfileRequest())
    var updatePatientReportRequest = ObservableField(PatientReportRequest())
    var patientList = MutableLiveData<ArrayList<PatientData>?>()
    var particularPatientProfile = MutableLiveData<PProfileData?>()
    val patientProfile = MutableLiveData<PatientProfileData?>()
    val patientReportList = MutableLiveData<ArrayList<DataItem>?>()

    var totalPages = MutableLiveData<Int>()

    var searchedPatientList = MutableLiveData<ArrayList<PatientData>?>()


    fun newPatientProfile(
        gender: String,
        fullName: String,
        address: String,
        age: String,
        phoneNumber: String,
        bloodGroup: String,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            showLoading.postValue(true)
            patientProfileRequest.get()?.apply {
                this.name = fullName
                this.gender = gender
                this.address = address
                this.phone = phoneNumber
                this.age = age
                this.bloodGroup = bloodGroup
            }?.let {
                homeRepository.createPatientProfile(it) { isSuccess, response ->
                    showLoading.postValue(false)
                    if (response.success == true) {
                        onResult(true)
                        toastMessage.postValue(response.message)

                    } else {
                        errorToastMessage.postValue(response.message)
                        onResult(false)
                    }
                }
            }

        }
    }


    fun getPatientList(
        page: Int,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            showLoading.postValue(true)
            homeRepository.getPatientList(page) { isSuccess, response ->
                showLoading.postValue(false)
                if (response.status == true) {
                    onResult(true)
                    patientList.postValue(response.data)
                    totalPages.postValue(response.lastPage ?: 1)
                } else {
                    errorToastMessage.postValue(response.message)
                    onResult(false)
                }
            }
        }
        viewModelScope.launch {
            showLoading.postValue(true)
            homeRepository.getPatientList(page) { isSuccess, response ->
                showLoading.postValue(false)
                if (response.status == true) {
                    onResult(true)
                    patientList.postValue(response.data)

                } else {
                    errorToastMessage.postValue(response.message)
                    onResult(false)
                }
            }

        }
    }

    fun updatePatientProfile(
        patientId: Int,
        gender: String,
        name: String,
        address: String,
        age: String,
        phoneNumber: String,
        bloodGroup: String,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            showLoading.postValue(true)
            updatePatientProfileRequest.get()?.apply {
                this.gender = gender
                this.name = name
                this.address = address
                this.age = age
                this.phone = phoneNumber
                this.bloodGroup = bloodGroup
            }?.let {
                homeRepository.updatePatientProfile(patientId, it) { isSuccess, response ->
                    showLoading.postValue(false)
                    if (response.success) {
                        onResult(true)
                        toastMessage.postValue(response.message)
                        patientProfile.postValue(response.data)

                    } else {
                        errorToastMessage.postValue(response.message)
                        onResult(false)
                    }
                }
            }

        }
    }

    fun getParticularPatientProfile(patientId: Int, onResult: (isSuccess: Boolean) -> Unit) {
        viewModelScope.launch {
            showLoading.postValue(true)
            homeRepository.getPatientProfile(patientId) { isSuccess, response ->
                showLoading.postValue(false)
                if (response.status) {
                    onResult(true)
                    toastMessage.postValue(response.message)
                    particularPatientProfile.postValue(response.data)

                } else {
                    errorToastMessage.postValue(response.message)
                    onResult(false)


                }
            }

        }
    }


    fun getRecentReportList(
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            showLoading.postValue(true)
            homeRepository.getPatientReportList() { isSuccess, response ->
                showLoading.postValue(false)
                if (response.status == true) {
                    onResult(true)
                    patientReportList.postValue(response.data)

                } else {
                    errorToastMessage.postValue(response.message)
                    onResult(false)
                }
            }

        }
    }

    fun updatePatientReport(
        patientId: Int,
        pdfFile: String,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                showLoading.postValue(true)

                // Check if file exists
                val file = File(pdfFile)
                if (!file.exists()) {
                    showLoading.postValue(false)
                    errorToastMessage.postValue("PDF file not found")
                    onResult(false)
                    return@launch
                }

                updatePatientReportRequest.get()?.apply {
                    this.patientId = patientId
                    this.pdfFile = pdfFile
                }?.let { request ->
                    homeRepository.updatePatientReport(request) { isSuccess, response ->
                        showLoading.postValue(false)
                        if (response.success == true) {
                            onResult(true)
                            toastMessage.postValue(response.message ?: "PDF uploaded successfully")
                        } else {
                            errorToastMessage.postValue(response.message ?: "Failed to upload PDF")
                            onResult(false)
                        }
                    }
                }
            } catch (e: Exception) {
                showLoading.postValue(false)
                errorToastMessage.postValue("Error uploading PDF: ${e.message}")
                onResult(false)
            }
        }
    }


    fun getParticularPatientReportList(
        id: Int,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            showLoading.postValue(true)
            homeRepository.getParticularPatientReportList(id) { isSuccess, response ->
                showLoading.postValue(false)
                if (response.status == true) {
                    onResult(true)
                    toastMessage.postValue(response.message)
                    response.data?.let { reportResponse ->
                        patientReportList.postValue(reportResponse)
                    }
                } else {
                    errorToastMessage.postValue(response.message)
                    onResult(false)
                }
            }
        }
    }

    fun getSearchedPatientData(
        patientName: String,
        page: Int,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            showLoading.postValue(true)
            homeRepository.getSearchedPatientData(patientName, page) { isSuccess, response ->
                showLoading.postValue(false)
                if (response.status == true) {
                    onResult(true)
                    toastMessage.postValue(response.message)
                    searchedPatientList.postValue(response.data)
                } else {
                    errorToastMessage.postValue(response.message)
                    onResult(false)
                }
            }
        }
    }

}