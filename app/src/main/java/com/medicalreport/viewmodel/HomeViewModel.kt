package com.medicalreport.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.medicalreport.base.ParentViewModel
import com.medicalreport.modal.request.DoctorProfileRequest
import com.medicalreport.modal.response.DocDetailData
import com.medicalreport.repository.auth.AuthRepository
import com.medicalreport.repository.home.HomeRepository
import com.medicalreport.utils.Prefs
import io.reactivex.Observable
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : ParentViewModel() {
    var doctorUpdateRequest = ObservableField(DoctorProfileRequest())
    var doctorProfile = MutableLiveData<DocDetailData?>()

    fun getDocProfile(
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            showLoading.postValue(true)
            homeRepository.getDoctorProfile { isSuccess, response ->
                showLoading.postValue(false)
                if (response.status == true) {
                    onResult(true)
                    doctorProfile.postValue(response.data)

                } else {
                    errorToastMessage.postValue(response.message)
                    onResult(false)
                }
            }

        }
    }

    fun updateDoctorProfile(
        gender: String,
        fullName: String,
        address: String,
        username: String,
        phoneNumber: String,
        specialized: String,
        bio: String,
        hospitalName: String,
        hospitalAddress: String,
        hospitalPhoneNumber: String,
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            showLoading.postValue(true)
            doctorUpdateRequest.get()?.apply {
                this.username = username
                this.name = fullName
                this.gender = gender
                this.bio = bio
                this.specialized = specialized
                this.address = address
                this.phone = phoneNumber
                this.hospital_address = hospitalAddress
                this.hospital_name = hospitalName
                this.hospital_phone = hospitalPhoneNumber
            }?.let {
                homeRepository.updateDoctor(it) { isSuccess, response ->
                    showLoading.postValue(false)
                    if (response.status == true) {
                        onResult(true)
                        toastMessage.postValue(response.message)
                        doctorProfile.postValue(response.data)

                    } else {
                        errorToastMessage.postValue(response.message)
                        onResult(false)
                    }
                }
            }

        }
    }

    fun logout(
        onResult: (isSuccess: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            showLoading.postValue(true)
            homeRepository.logout() { isSuccess, response ->
                showLoading.postValue(false)
                if (response.status == true) {
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