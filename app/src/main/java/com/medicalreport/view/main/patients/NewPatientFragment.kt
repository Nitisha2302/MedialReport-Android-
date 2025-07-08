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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentNewPatientBinding
import com.medicalreport.modal.response.SelectedDoctorsResponse
import com.medicalreport.utils.EdTextWatcher
import com.medicalreport.utils.Util
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.view.main.dialog.DoctorsListDialogFragment
import com.medicalreport.view.main.dialog.PatientCreatedSuccessFragment
import com.medicalreport.view.main.usb.UsbCameraActivity
import com.medicalreport.viewmodel.HomeViewModel
import com.medicalreport.viewmodel.PatientViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class NewPatientFragment : BaseFragment<FragmentNewPatientBinding>(),
    PatientCreatedSuccessFragment.ClickListener, DoctorsListDialogFragment.ClickListener {
    private val viewModel by viewModel<PatientViewModel>()
    private lateinit var mBinding: FragmentNewPatientBinding
    private var gender: String? = null
    private var fullName: String = ""
    private var address: String = ""
    private var age: String = ""
    private var phoneNumber: String = ""
    private var bloodGroup: String = ""
    private var successDialogFragment = PatientCreatedSuccessFragment()
    private var doctorsLisDialogFragment = DoctorsListDialogFragment()
    private var bundle = Bundle()
    override val fragmentLayoutId: Int
        get() = R.layout.fragment_new_patient

    override fun setUpUi(binding: FragmentNewPatientBinding) {
        mBinding = binding
        initGender()
        initView()
        initListener()
    }

    private fun checkValidations(): Boolean {
        if (TextUtils.isEmpty(fullName)) {
            mBinding.etFullName.error = resources.getString(R.string.error_enter_fullName)
            return false
        } else if (TextUtils.isEmpty(phoneNumber)) {
            mBinding.etPhoneNumber.error = resources.getString(R.string.error_enter_phone_number)
            return false
        } else if (phoneNumber.length < 10) {
            mBinding.etPhoneNumber.error = resources.getString(R.string.error_enter_valid_number)
            return false
        } else if (TextUtils.isEmpty(age)) {
            mBinding.etAge.error = resources.getString(R.string.error_enter_age)
            return false
        } else if (TextUtils.isEmpty(gender)) {
            mBinding.etGender.error = resources.getString(R.string.error_select_gender)
            return false
        }
        return true
    }

    private fun initListener() {
        mBinding.btnCreateProfile.setOnClickListener {
            it.disableMultiTap()
            if (Util.checkIfHasNetwork()) {
                if (checkValidations()) {
                    viewModel.newPatientProfile(
                        gender.toString(), fullName, address, age, phoneNumber, bloodGroup
                    ) {
                        successDialogFragment.setTargetFragment(this, DIALOG_SUCCESS)
                        successDialogFragment.show(requireFragmentManager(), "SuccessDialog")
                    }
                }
            } else {
                showNoInternetAlert()
            }
        }
    }

    private fun initView() {
        mBinding.etFullName.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                fullName = mBinding.etFullName.text.toString()
            }
        })
        mBinding.etAddress.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                address = mBinding.etAddress.text.toString()
            }
        })

        mBinding.etPhoneNumber.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                phoneNumber = mBinding.etPhoneNumber.text.toString()
            }
        })
        mBinding.etAge.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                age = mBinding.etAge.text.toString()
            }
        })
        mBinding.etBGroup.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                bloodGroup = mBinding.etBGroup.text.toString()
            }
        })

    }

    private fun initGender() {
        val genderList = arrayListOf("Male", "Female")
        val genderAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderList)
        mBinding.etGender.setAdapter(genderAdapter)
        mBinding.etGender.setOnClickListener {
            mBinding.etGender.showDropDown()
        }
        mBinding.etGender.setOnItemClickListener { parent, _, position, _ ->
            gender = parent.getItemAtPosition(position).toString()
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
    }

    override fun onClickHome() {
        findNavController().navigateUp()
        successDialogFragment.dialog?.dismiss()
    }

    override fun onClickCreateReport() {
        successDialogFragment.dialog?.dismiss()
        doctorsLisDialogFragment.setTargetFragment(
            this,
            DIALOG_DOCTORS_SUCCESS
        )
        doctorsLisDialogFragment.show(requireFragmentManager(), "DoctorsListDialog")
    }

    override fun onSelectedDoctorsList(selectedDoctors: ArrayList<SelectedDoctorsResponse>) {
        doctorsLisDialogFragment.dialog?.dismiss()
        getUsbDevicePermission(selectedDoctors)

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
            var gender = mBinding.etGender.getText().toString()
            bundle.putString("patient_name", fullName)
            bundle.putString("patient_age", age)
            bundle.putString("patient_gender", gender)
            bundle.putParcelableArrayList(
                "selectedDoctors",
                selectedDoctors as ArrayList<out Parcelable>
            )
            findNavController().navigate(R.id.nav_deviceCamera, bundle)
        } else {
            var deviceName = ""
            var productId = 0
            var vendorId = 0
            var deviceClass = 0
            var deviceSubclass = 0
            deviceList.forEach {
                deviceName = it.deviceName
                productId = it.productId
                vendorId = it.vendorId
                deviceClass = it.deviceClass
                deviceSubclass = it.deviceSubclass
            }
            Toast.makeText(context, "Device Attached,${productId},${vendorId},${deviceClass},${deviceSubclass}", Toast.LENGTH_SHORT).show()
            startMainActivity(selectedDoctors)
        }
    }

    private fun startMainActivity(selectedDoctors: ArrayList<SelectedDoctorsResponse>) {
        var gender = mBinding.etGender.getText().toString()
        bundle.putString("patient_name", fullName)
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

    companion object {
        const val DIALOG_SUCCESS = 1
        const val DIALOG_DOCTORS_SUCCESS = 1

    }

}