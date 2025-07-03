package com.medicalreport.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.medicalreport.base.ParentViewModel
import com.medicalreport.modal.request.DoctorProfileRequest
import com.medicalreport.modal.request.PatientProfileRequest
import com.medicalreport.repository.home.HomeRepository
import kotlinx.coroutines.launch

class PatientViewModel(private val homeRepository: HomeRepository) : ParentViewModel() {
    var patientProfileRequest = ObservableField(PatientProfileRequest())

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
}