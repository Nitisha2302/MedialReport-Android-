package com.medicalreport.view.main.patients

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentAllPatientsBinding
import com.medicalreport.modal.response.PatientData
import com.medicalreport.utils.Util
import com.medicalreport.view.adapter.PatientAdapter
import com.medicalreport.viewmodel.PatientViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class AllPatientsFragment : BaseFragment<FragmentAllPatientsBinding>(),
    PatientAdapter.ClickListener {
    private val viewModel by viewModel<PatientViewModel>()
    private lateinit var mBinding: FragmentAllPatientsBinding
    private lateinit var patientAdapter: PatientAdapter
    private val patientArrayList: ArrayList<PatientData> = arrayListOf()
    private var downloadsDir: String = ""

    private var search: String = ""
    private var fromWhere: String = ""
    private var bundle = Bundle()
    private var currentPage = 1
    private var totalPages = 1
    private var isLoading = false
    private var isLastPage = false

    override val fragmentLayoutId: Int
        get() = R.layout.fragment_all_patients

    override fun setUpUi(binding: FragmentAllPatientsBinding) {
        mBinding = binding
        downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toString() + "/" + "SIMS/"
        try {
            fromWhere = arguments?.getString("fromWhere").toString()

        } catch (e: Exception) {
            e.printStackTrace()
            fromWhere = "patientProfile"
        }
        requestDataCalls()
    }

    private fun requestDataCalls() {
        if (Util.checkIfHasNetwork()) {
            isLoading = true
            viewModel.getPatientList(currentPage) {
                isLoading = false
            }
        } else {
            showNoInternetAlert()
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
            }
        }
        viewModel.errorToastMessage.observe(viewLifecycleOwner) {
            if (!TextUtils.isEmpty(it)) {
                showErrorAlert(it)
            }
        }
        viewModel.apply {
            patientList.observe(viewLifecycleOwner) {
                it?.let { newItems ->
                    if (currentPage == 1) {
                        patientAdapter.setNewItems(newItems)
                    } else {
                        patientAdapter.addItems(newItems)
                    }
                    patientArrayList.addAll(newItems)
                }

                /*// Assuming your ViewModel has a field for total pages
                totalPages = viewModel.totalPages.value ?: 1
                isLastPage = currentPage >= totalPages*/
            }


            setPatientAdapter()
        }
    }

    private fun setPatientAdapter() {
        patientAdapter = PatientAdapter(R.layout.item_patinet_profile).apply {
            clickListener = this@AllPatientsFragment
        }
        mBinding.rvPatientHistory.adapter = patientAdapter
    }

    override fun onClickView(view: View, id: Int) {
        bundle.putInt("patientid", id)
        bundle.putString("fromWhere", fromWhere)
        findNavController().navigate(R.id.nav_patientProfile, bundle)
    }

    override fun onClickEdit(view: View, id: Int) {
        bundle.putInt("patientid", id)
        findNavController().navigate(R.id.nav_updatePatient, bundle)
    }

    override fun onClickPdf(view: View, name: String) {
        var fileName = "${name}.pdf"
        bundle.putString("downloadsDir", downloadsDir)
        bundle.putString("fileName", fileName)
        /*val intent = Intent(context, PdfViewActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)*/
    }

    override fun onClickShare(view: View, name: String) {
        var fileName = "${name}.pdf"
        /* val bottomSheetDialog = ShareReportBottomSheetDialog()
         val bundle = Bundle()
         bundle.putString("downloadsDir", downloadsDir)
         bundle.putString("fileName", fileName)
         bottomSheetDialog.arguments = bundle
         bottomSheetDialog.show(this.childFragmentManager, "ShareReportBottomSheetDialog")*/
    }

}