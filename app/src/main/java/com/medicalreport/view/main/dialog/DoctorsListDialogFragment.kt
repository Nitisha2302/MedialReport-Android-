package com.medicalreport.view.main.dialog

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicalreport.R
import com.medicalreport.base.BaseDialogFragment
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentDoctorsListDialogBinding
import com.medicalreport.modal.response.DoctorsDataItem
import com.medicalreport.modal.response.SelectedDoctorsResponse
import com.medicalreport.utils.Util
import com.medicalreport.utils.hideProgress
import com.medicalreport.utils.showProgressDialog
import com.medicalreport.view.adapter.DoctorAdapter
import com.medicalreport.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class DoctorsListDialogFragment : BaseDialogFragment(), DoctorAdapter.ClickListener {
    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var mBinding: FragmentDoctorsListDialogBinding
    private lateinit var doctorAdapter: DoctorAdapter
    var clickListener: ClickListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentDoctorsListDialogBinding.inflate(inflater, container, false)
        callApi()
        initObserver()
        initListener()
        return mBinding.root
    }

    private fun initObserver() {
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it == true) {
                showProgressDialog(requireContext())
            } else {
                hideProgress()
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (!TextUtils.isEmpty(it)) {
                showSuccessAlert(it)
                viewModel.toastMessage.postValue(null)
            }
        }

        viewModel.errorToastMessage.observe(viewLifecycleOwner) {
            if (!TextUtils.isEmpty(it)) {
                showErrorAlert(it)
                viewModel.errorToastMessage.postValue(null)
            }
        }

        viewModel.doctorsListProfile.observe(viewLifecycleOwner) {
            it?.let {
                setDoctorsAdapter(it)
            }
        }
    }

    private fun setDoctorsAdapter(it: List<DoctorsDataItem>) {
        doctorAdapter = DoctorAdapter(it).apply {
            clickListener = this@DoctorsListDialogFragment
        }
        mBinding.rvSelectedDoctors.adapter = doctorAdapter

    }

    private fun initListener() {
        mBinding.ivCross.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun callApi() {
        if (Util.checkIfHasNetwork()) {
            viewModel.getDoctorsList() {}
        } else {
            showNoInternetAlert()
        }
    }

    override fun onClickSelectedDoctors(selectedDoctors: ArrayList<SelectedDoctorsResponse>) {
        Log.e("SelectedDoctors", "${selectedDoctors}")
        callSaveButton(selectedDoctors)
    }

    private fun callSaveButton(selectedDoctors: ArrayList<SelectedDoctorsResponse>) {
        val callback = targetFragment as? ClickListener
        callback?.onSelectedDoctorsList(selectedDoctors)
    }

    interface ClickListener {
        fun onSelectedDoctorsList(selectedDoctors: ArrayList<SelectedDoctorsResponse>)

    }

}