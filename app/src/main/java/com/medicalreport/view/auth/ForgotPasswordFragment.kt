package com.medicalreport.view.auth

import android.text.TextUtils
import androidx.navigation.fragment.findNavController
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentForgotPasswordBinding
import com.medicalreport.utils.EdTextWatcher
import com.medicalreport.utils.Util
import com.medicalreport.utils.Util.checkIfHasNetwork
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {
    private val viewModel by viewModel<AuthViewModel>()
    private lateinit var mBinding: FragmentForgotPasswordBinding
    private var email: String = ""

    override val fragmentLayoutId: Int
        get() = R.layout.fragment_forgot_password

    override fun setUpUi(binding: FragmentForgotPasswordBinding) {
        mBinding = binding
        email = arguments?.getString("emailId").toString()
        mBinding.etEmail.setText(email)
        initView()
        initListener()
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


    private fun initView() {
        mBinding.etEmail.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                email = mBinding.etEmail.text.toString()
            }
        })
    }

    private fun initListener() {
        mBinding.ivBack.setOnClickListener {
            it.disableMultiTap()
            findNavController().navigateUp()
        }
        mBinding.btnStart.setOnClickListener {
            if (checkIfHasNetwork()) {
                if (checkEmailValidations()) {
                    viewModel.resetPassword(email) {
                        if (it) {
                            findNavController().navigateUp()
                        }
                    }
                }
            } else {
                showNoInternetAlert()
            }

        }
    }

    private fun checkEmailValidations(): Boolean {
        if (TextUtils.isEmpty(email)) {
            mBinding.etEmail.error = resources.getString(R.string.error_enter_email)
            return false
        } else if (!Util.isValidEmailId(email)) {
            mBinding.etEmail.error = resources.getString(R.string.error_enter_valid_email)
            return false
        }
        return true
    }

}