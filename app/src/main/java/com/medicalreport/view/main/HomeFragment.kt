package com.medicalreport.view.main

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentHomeBinding
import com.medicalreport.utils.Util
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.view.adapter.RecentPatientReportAdapter
import com.medicalreport.viewmodel.PatientViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel by viewModel<PatientViewModel>()
    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var recentPatientReportAdapter: RecentPatientReportAdapter
    private var bundle = Bundle()
    override val fragmentLayoutId: Int
        get() = R.layout.fragment_home

    override fun setUpUi(binding: FragmentHomeBinding) {
        mBinding = binding
        callReportListApi()
        initListener()

    }

    private fun callReportListApi() {
        if (Util.checkIfHasNetwork()) {
            viewModel.getRecentReportList {
            }
        } else {
            showNoInternetAlert()
        }
    }

    private fun initListener() {
        mBinding.clNewPatient.setOnClickListener {
            it.disableMultiTap()
            findNavController().navigate(R.id.newPatientFragment)
        }
        mBinding.mcvPatientHistory.setOnClickListener {
            it.disableMultiTap()
            bundle.putString("fromWhere", "patientHistory")
            findNavController().navigate(R.id.nav_patient)
        }
    }

    override fun setupObservers() {
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it == true) {
                showProgressDialog()
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
        viewModel.patientReportList.observe(viewLifecycleOwner) {
            setRecentReportAdapter()
            if (it?.isEmpty() == true) {
                mBinding.tvNoDataFound.visibility = View.VISIBLE
                mBinding.rvRecentReports.visibility = View.GONE
            } else {
                mBinding.tvNoDataFound.visibility = View.GONE
                mBinding.rvRecentReports.visibility = View.VISIBLE
                it?.let { report ->
                    recentPatientReportAdapter.setNewItems(report)

                }

            }
        }
    }

    private fun setRecentReportAdapter() {
        recentPatientReportAdapter = RecentPatientReportAdapter(R.layout.item_recent_report)
        mBinding.rvRecentReports.adapter = recentPatientReportAdapter
    }


}