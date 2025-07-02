package com.medicalreport.view.auth

import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentForgotPasswordBinding
import com.medicalreport.utils.EdTextWatcher
import com.medicalreport.utils.Util
import com.medicalreport.utils.Util.checkIfHasNetwork
import com.medicalreport.utils.Util.showDarkInternetToast
import com.medicalreport.utils.Util.showInternetToast
import com.medicalreport.utils.disableMultiTap


class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {
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
                    findNavController().navigateUp()
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