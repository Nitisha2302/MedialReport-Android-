package com.medicalreport.view.main.patients

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
import com.medicalreport.databinding.FragmentUpdatePatientBinding
import com.medicalreport.utils.EdTextWatcher
import com.medicalreport.utils.Util
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.viewmodel.PatientViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class UpdatePatientFragment : BaseFragment<FragmentUpdatePatientBinding>() {
    private val viewModel by viewModel<PatientViewModel>()
    private lateinit var mBinding: FragmentUpdatePatientBinding
    private var gender: String? = null
    private var fullName: String = ""
    private var address: String = ""
    private var age: String = ""
    private var phoneNumber: String = ""
    private var bloodGroup: String = ""
    private var patientId: Int? = 0

    override val fragmentLayoutId: Int
        get() = R.layout.fragment_update_patient

    override fun setUpUi(binding: FragmentUpdatePatientBinding) {
        mBinding = binding
        patientId = arguments?.getInt("patientid")
        initGender()
        callApis()
        initView()
        initListener()
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
        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.btnCreateProfile.setOnClickListener {
            it.disableMultiTap()
            if (Util.checkIfHasNetwork()) {
                if (checkValidations()) {
                    patientId?.let {
                        viewModel.updatePatientProfile(
                            it,
                            gender.toString(), fullName, address, age, phoneNumber, bloodGroup
                        ) { }
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

        viewModel.apply {
            particularPatientProfile.observe(viewLifecycleOwner) { it1 ->
                if (!it1?.name.isNullOrEmpty()) {
                    fullName = it1.name
                    mBinding.etFullName.setText(it1.name)
                }
                if (!it1?.address.isNullOrEmpty()) {
                    address = it1.address
                    mBinding.etAddress.setText(it1.address)
                }
                if (!it1?.age.isNullOrEmpty()) {
                    age = it1.age
                    mBinding.etAge.setText(it1.age)
                }
                if (!it1?.phone.isNullOrEmpty()) {
                    phoneNumber = it1.phone
                    mBinding.etPhoneNumber.setText(it1.phone)
                }
                if (!it1?.gender.isNullOrEmpty()) {
                    gender = it1.gender
                    mBinding.etGender.setText(it1.gender, false)
                }
                if (!it1?.bloodGroup.isNullOrEmpty()) {
                    bloodGroup = it1.bloodGroup
                    mBinding.etBGroup.setText(it1.bloodGroup)
                }

            }
        }
    }


}