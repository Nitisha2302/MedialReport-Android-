package com.medicalreport.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class ParentViewModel : ViewModel() {
    open var showLoading = MutableLiveData<Boolean>().apply { value = false }
    var isResultSuccess = MutableLiveData<Boolean>().apply { value = false }
    open var toastMessage = MutableLiveData<String>()
    var errorToastMessage = MutableLiveData<String>()
    var showNoData = MutableLiveData<Boolean>().apply { value = false }
    open var executorService: ExecutorService = Executors.newSingleThreadExecutor()

}