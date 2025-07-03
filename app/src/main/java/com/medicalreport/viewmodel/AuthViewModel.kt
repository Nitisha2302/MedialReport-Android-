package com.medicalreport.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.medicalreport.base.ParentViewModel
import com.medicalreport.modal.request.ForgotPasswordRequest
import com.medicalreport.modal.request.LoginRequest
import com.medicalreport.repository.auth.AuthRepository
import com.medicalreport.utils.Prefs
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ParentViewModel() {
    var loginRequest = ObservableField(LoginRequest())
    var passwordResetRequest = ObservableField(ForgotPasswordRequest())

    fun login(email: String, password: String, onResult: (isSuccess: Boolean) -> Unit) {
        viewModelScope.launch {
            showLoading.postValue(true)
            loginRequest.get()?.apply {
                this.email = email
                this.password = password
            }?.let {
                authRepository.login(it) { isSuccess, response ->
                    showLoading.postValue(false)
                    if (response.status) {
                        onResult(true)
                        toastMessage.postValue(response.message)
                        Prefs.init().email = response.data?.email.toString()
                        Prefs.init().currentToken = response.data?.token.toString()
                        Prefs.init().doctorId = response.data?.id!!
                        Prefs.init().name = response.data?.name.toString()
                        Prefs.init().specializedIn = response.data?.specialized.toString()
                        Prefs.init().hospitalName = response.data?.hospitalName.toString()
                        Prefs.init().hospitalAddress = response.data?.hospitalAddress.toString()
                        Prefs.init().hospitalPhoneNumber = response.data?.hospitalAddress.toString()
                    } else {
                        errorToastMessage.postValue(response.message)
                        onResult(false)
                    }
                }
            }
        }
    }

    fun resetPassword(email: String, onResult: (isSuccess: Boolean) -> Unit) {
        viewModelScope.launch {
            showLoading.postValue(true)
            passwordResetRequest.get()?.apply {
                this.email = email
            }?.let {
                authRepository.passwordReset(it) { isSuccess, response ->
                    showLoading.postValue(false)
                    if (response.status) {
                        onResult(true)
                        toastMessage.postValue(response.message)
                    } else {
                        toastMessage.postValue(response.message)
                        onResult(false)
                    }
                }
            }
        }
    }
}