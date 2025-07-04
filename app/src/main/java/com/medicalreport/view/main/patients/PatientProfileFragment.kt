package com.medicalreport.view.main.patients

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentPatientProfileBinding
import com.medicalreport.modal.response.SelectedDoctorsResponse
import com.medicalreport.utils.Prefs
import com.medicalreport.utils.Util
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.view.adapter.ParticularPatientReportAdapter
import com.medicalreport.view.adapter.ReportHistoryAdapter
import com.medicalreport.view.main.dialog.DoctorsListDialogFragment
import com.medicalreport.view.main.reports.PdfViewActivity
import com.medicalreport.view.main.usb.UsbCameraActivity
import com.medicalreport.viewmodel.PatientViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class PatientProfileFragment : BaseFragment<FragmentPatientProfileBinding>(),
    ParticularPatientReportAdapter.ClickListener, DoctorsListDialogFragment.ClickListener {
    private val viewModel by viewModel<PatientViewModel>()

    private lateinit var mBinding: FragmentPatientProfileBinding
    private var patientId: Int? = 0
    private var bundle = Bundle()
    private lateinit var reportHistoryAdapter: ReportHistoryAdapter
    private lateinit var particularPatientReportAdapter: ParticularPatientReportAdapter
    private var name: String = ""
    private var gender: String = ""
    private var age: String = ""
    private var doctorsLisDialogFragment = DoctorsListDialogFragment()

    override val fragmentLayoutId: Int
        get() = R.layout.fragment_patient_profile


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
        viewModel.apply {
            particularPatientProfile.observe(viewLifecycleOwner) { it1 ->

                name = it1?.name.toString()
                gender = it1?.gender.toString()
                age = it1?.age.toString()

                mBinding.tvName.setText(it1?.name)
                mBinding.tvEmail.setText(it1?.email)
                mBinding.tvAddress.setText(it1?.address)
                mBinding.tvAge.setText(it1?.age)
                mBinding.tvPhone.setText(it1?.phone)
                mBinding.tvGender.setText(it1?.gender)

                if (it1?.report.isNullOrEmpty()) {
                    mBinding.tvContent.visibility = View.GONE
                    mBinding.rvReportImagesData.visibility = View.GONE
                    mBinding.llPatientReports.visibility = View.GONE
                    mBinding.rvReportFileData.visibility = View.GONE
                    mBinding.tvNoDataFound.visibility = View.VISIBLE
                } else {
                    mBinding.tvContent.visibility = View.GONE
                    mBinding.rvReportImagesData.visibility = View.GONE
                    mBinding.tvNoDataFound.visibility = View.GONE
                    mBinding.llPatientReports.visibility = View.VISIBLE
                    mBinding.rvReportFileData.visibility = View.VISIBLE
                    it1?.report?.forEach {
                        setReportImageAdapter()
                        it.docImage?.let { it2 -> reportHistoryAdapter.setNewItems(it2) }
                        mBinding.tvContent.setText(it.content)
                    }
                }


            }
        }

        viewModel.patientReportList.observe(viewLifecycleOwner) {
            setParticularPatientAdapter()
            it?.let { it1 ->
                particularPatientReportAdapter.setNewItems(it1)
            }


        }
    }

    private fun setParticularPatientAdapter() {
        particularPatientReportAdapter =
            ParticularPatientReportAdapter(R.layout.item_patient_report_list).apply {
                clickListener = this@PatientProfileFragment
            }
        mBinding.rvReportFileData.adapter = particularPatientReportAdapter

    }

    private fun setReportImageAdapter() {
        reportHistoryAdapter = ReportHistoryAdapter(R.layout.item_report_images)
        mBinding.rvReportImagesData.adapter = reportHistoryAdapter
    }

    override fun setUpUi(binding: FragmentPatientProfileBinding) {
        mBinding = binding
        getBundledData()
        requestDataCalls()
        initListener()
    }

    private fun requestDataCalls() {
        if (Util.checkIfHasNetwork()) {
            callApis()
        } else {
            showNoInternetAlert()
        }
    }

    private fun callApis() {
        if (Util.checkIfHasNetwork()) {
            patientId?.let {
                viewModel.getParticularPatientProfile(it) {

                }
            }
        } else {
            showNoInternetAlert()
        }
    }

    private fun getBundledData() {
        patientId = arguments?.getInt("patientid")
        patientId?.let {
            Prefs.init().patientId = it
        }
        patientId?.let {
            viewModel.getParticularPatientReportList(it) {

            }
        }
    }

    private fun initListener() {
        mBinding.tvEditProfile.setOnClickListener {
            patientId?.let { it1 -> bundle.putInt("patientid", it1) }
            findNavController().navigate(R.id.nav_updatePatient, bundle)
        }


        mBinding.btnExamine.setOnClickListener {
            it.disableMultiTap()
            getSelectedDoctorsList()
        }
    }

    private fun getSelectedDoctorsList() {
        doctorsLisDialogFragment.setTargetFragment(this, DIALOG_DOCTORS_SUCCESS)
        doctorsLisDialogFragment.show(requireFragmentManager(), "DoctorsListDialog")
    }

    private fun getUsbDevicePermission(selectedDoctors: ArrayList<SelectedDoctorsResponse>) {
        val usbManager = context?.getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        showDevicesDialog(deviceList.values, selectedDoctors)
    }

    private fun showDevicesDialog(
        deviceList: MutableCollection<UsbDevice>,
        selectedDoctors: ArrayList<SelectedDoctorsResponse>
    ) {
        if (deviceList.isNullOrEmpty()) {
            Toast.makeText(context, "No USB devices found", Toast.LENGTH_SHORT).show()
            bundle.putString("patient_name", name)
            bundle.putString("patient_age", age)
            bundle.putString("patient_gender", gender)
            bundle.putParcelableArrayList(
                "selectedDoctors",
                selectedDoctors as ArrayList<out Parcelable>
            )
            findNavController().navigate(R.id.nav_deviceCamera, bundle)
        } else {
            startMainActivity(selectedDoctors)
        }
    }

    private fun startMainActivity(selectedDoctors: ArrayList<SelectedDoctorsResponse>) {
        bundle.putString("patient_name", name)
        bundle.putString("patient_age", age)
        bundle.putString("patient_gender", gender)
        bundle.putParcelableArrayList(
            "selectedDoctors",
            selectedDoctors as ArrayList<out Parcelable>
        )
        Handler().postDelayed({
            val intent = Intent(context, UsbCameraActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }, 3000)

    }

    override fun onClickPdF(path: String) {
        bundle.putString("pdfPath", path)
        val intent = Intent(context, PdfViewActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onClickShare(path: String) {
        /* val bottomSheetDialog = ShareReportBottomSheetDialog()
         val bundle = Bundle()
         bundle.putString("pdfPath", path)
         bottomSheetDialog.arguments = bundle
         bottomSheetDialog.show(this.childFragmentManager, "ShareReportBottomSheetDialog")*/
    }

    companion object {
        const val DIALOG_DOCTORS_SUCCESS = 1
    }

    override fun onSelectedDoctorsList(selectedDoctors: ArrayList<SelectedDoctorsResponse>) {
        doctorsLisDialogFragment.dialog?.dismiss()
        getUsbDevicePermission(selectedDoctors)

    }

}