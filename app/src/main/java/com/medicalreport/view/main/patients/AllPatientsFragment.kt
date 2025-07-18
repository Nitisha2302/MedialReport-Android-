package com.medicalreport.view.main.patients

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentAllPatientsBinding
import com.medicalreport.modal.response.PatientData
import com.medicalreport.utils.Util
import com.medicalreport.view.adapter.PatientAdapter
import com.medicalreport.view.main.reports.PdfViewActivity
import com.medicalreport.viewmodel.PatientViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.getValue


class AllPatientsFragment : BaseFragment<FragmentAllPatientsBinding>(),
    PatientAdapter.ClickListener {
    private val viewModel by viewModel<PatientViewModel>()
    private lateinit var mBinding: FragmentAllPatientsBinding
    private lateinit var patientAdapter: PatientAdapter
    private val patientArrayList: ArrayList<PatientData> = arrayListOf()
    private var downloadsDir: String = ""
    private var fromWhere: String = ""
    private var bundle = Bundle()
    private var currentPage = 1
    private var totalPages: Int? = null
    private var isLoading = false
    private var isLastPage = false

    private var searchName: String = ""

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
        initView()
        initListener()

    }

    private fun initListener() {
        mBinding.btnNext.setOnClickListener {
            totalPages?.let { it1 ->
                if (currentPage < it1) {
                    currentPage++
                    requestDataCalls()
                }
            }
        }

        mBinding.btnPrevious.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                requestDataCalls()
            }
        }

        mBinding.searchView.setOnClickListener {
            if (Util.checkIfHasNetwork()) {
                isLoading = true
                viewModel.getSearchedPatientData(searchName, currentPage) {
                    isLoading = false
                }
            } else {
                showNoInternetAlert()
            }
        }

        mBinding.tvCross?.setOnClickListener {
            mBinding.etSearch.text?.clear()
            requestDataCalls()
        }
    }

    private fun initView() {
        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                searchName = mBinding.etSearch.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
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
                    newItems.let {
                        patientArrayList.clear()
                        patientArrayList.addAll(newItems)
                        patientAdapter.setNewItems(newItems)
                        updatePaginationUI()
                    }
                }

            }
            totalPages.observe(viewLifecycleOwner) { page ->
                this@AllPatientsFragment.totalPages = page
                page?.let {
                    isLastPage = currentPage >= it
                }
                updatePaginationUI()
            }

            setPatientAdapter()
        }

        viewModel.searchedPatientList.observe(viewLifecycleOwner) {
            if (it != null) {
                patientAdapter.filterList(it)
                updatePaginationUI()
            }

        }

    }

    private fun updatePaginationUI() {
        mBinding.tvPageIndicator?.text = "$currentPage"
        mBinding.btnPrevious?.isEnabled = currentPage > 1
        totalPages?.let { mBinding.btnNext?.isEnabled = currentPage < it }
    }

    private fun setPatientAdapter() {
        patientAdapter = PatientAdapter(context).apply {
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
        val intent = Intent(context, PdfViewActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onClickShare(view: View, url: String) {
        val fileName = url.substringAfterLast("/")
        val file = File(requireContext().cacheDir, fileName)

        if (file.exists()) {
            sharePdfFile(file)
            return
        }
        Thread {
            try {
                val input = java.net.URL(url).openStream()
                file.outputStream().use { output ->
                    input.copyTo(output)
                }

                requireActivity().runOnUiThread {
                    sharePdfFile(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Download failed", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }


    private fun sharePdfFile(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Share PDF using"))
    }

}