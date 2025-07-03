package com.medicalreport.view.main

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentDoctorUpdateProfileBinding
import com.medicalreport.utils.EdTextWatcher
import com.medicalreport.utils.Prefs
import com.medicalreport.utils.Util.checkIfHasNetwork
import com.medicalreport.utils.Util.isValidEmailId
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.viewmodel.AuthViewModel
import com.medicalreport.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import kotlin.toString

class DoctorUpdateProfileFragment : BaseFragment<FragmentDoctorUpdateProfileBinding>() {
    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var mBinding: FragmentDoctorUpdateProfileBinding
    private var gender: String? = null
    private var fullName: String = ""
    private var address: String = ""
    private var username: String = ""
    private var phoneNumber: String = ""
    private var specialized: String = ""
    private var bio: String = ""
    private var hospitalName: String = ""
    private var hospitalAddress: String = ""
    private var hospitalPhoneNumber: String = ""

    override val fragmentLayoutId: Int
        get() = R.layout.fragment_doctor_update_profile

    override fun setUpUi(binding: FragmentDoctorUpdateProfileBinding) {
        mBinding = binding
        initGender()
        initGetDoctorProfile()
        initView()
        initListener()
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
        mBinding.etUserName.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                username = mBinding.etUserName.text.toString()
            }
        })
        mBinding.etPhoneNumber.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                phoneNumber = mBinding.etPhoneNumber.text.toString()
            }
        })
        mBinding.etSpecialized.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                specialized = mBinding.etSpecialized.text.toString()
            }
        })
        mBinding.etBio.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                bio = mBinding.etBio.text.toString()
            }
        })

        mBinding.etHospitalName.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                hospitalName = mBinding.etHospitalName.text.toString()
            }
        })

        mBinding.etHospitalAddress.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                hospitalAddress = mBinding.etHospitalAddress.text.toString()
            }
        })
        mBinding.etHospitalPhoneNumber.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                hospitalPhoneNumber = mBinding.etHospitalPhoneNumber.text.toString()
            }
        })


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
        } else if (TextUtils.isEmpty(hospitalName)) {
            mBinding.etHospitalName.error = resources.getString(R.string.error_enter_hospital_name)
            return false
        } else if (TextUtils.isEmpty(hospitalAddress)) {
            mBinding.etHospitalAddress.error =
                resources.getString(R.string.error_enter_hospital_address)
            return false
        } else if (TextUtils.isEmpty(hospitalPhoneNumber)) {
            mBinding.etHospitalPhoneNumber.error =
                resources.getString(R.string.error_enter_hospital_number)
            return false
        } else if (hospitalPhoneNumber.length < 10) {
            mBinding.etHospitalPhoneNumber.error =
                resources.getString(R.string.error_enter_valid_hospital_number)
            return false
        } else if (TextUtils.isEmpty(gender)) {
            mBinding.etGender.error = resources.getString(R.string.error_select_gender)
            return false
        } else if (TextUtils.isEmpty(specialized)) {
            mBinding.etSpecialized.error = resources.getString(R.string.error_enter_specialized_in)
            return false
        }
        return true
    }

    private fun initListener() {
        mBinding.btnUpdate.setOnClickListener {
            it.disableMultiTap()
            if (checkIfHasNetwork()) {
                if (checkValidations()) {
                    viewModel.updateDoctorProfile(
                        gender.toString(),
                        fullName,
                        address,
                        username,
                        phoneNumber,
                        specialized,
                        bio,
                        hospitalName,
                        hospitalAddress,
                        hospitalPhoneNumber
                    ) {}
                }
            } else {
                showNoInternetAlert()
            }
        }
    }

    private fun initGetDoctorProfile() {
        if (checkIfHasNetwork()) {
            viewModel.getDocProfile() {

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

        viewModel.doctorProfile.observe(viewLifecycleOwner) {
            if (!it?.name.isNullOrEmpty()) {
                mBinding.etFullName.setText(it.name)
            }
            if (!it?.username.isNullOrEmpty()) {
                mBinding.etUserName.setText(it.username)
            }
            if (!it?.phone.isNullOrEmpty()) {
                mBinding.etPhoneNumber.setText(it.phone)
            }
            if (!it?.hospitalName.isNullOrEmpty()) {
                mBinding.etHospitalName.setText(it.hospitalName)
                Prefs.init().hospitalName = it.hospitalName.toString()
            }
            if (!it?.hospitalAddress.isNullOrEmpty()) {
                mBinding.etHospitalAddress.setText(it.hospitalAddress)
                Prefs.init().hospitalAddress = it.hospitalAddress.toString()

            }
            if (!it?.hospitalPhone.isNullOrEmpty()) {
                mBinding.etHospitalPhoneNumber.setText(it.hospitalPhone)
                Prefs.init().hospitalPhoneNumber = it.hospitalPhone.toString()

            }
            if (!it?.address.isNullOrEmpty()) {
                mBinding.etAddress.setText(it.address)
            }
            if (!it?.gender.isNullOrEmpty()) {
                mBinding.etGender.setText(it.gender, false)
            }
            if (!it?.specialized.isNullOrEmpty()) {
                mBinding.etSpecialized.setText(it.specialized)
            }
            if (!it?.bio.isNullOrEmpty()) {
                mBinding.etBio.setText(it.bio)
            }

        }
    }


}