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
import com.medicalreport.databinding.FragmentLoginBinding
import com.medicalreport.utils.EdTextWatcher
import com.medicalreport.utils.Util.checkIfHasNetwork
import com.medicalreport.utils.Util.isValidEmailId
import com.medicalreport.utils.Util.showDarkInternetToast
import com.medicalreport.utils.Util.showInternetToast
import com.medicalreport.utils.disableMultiTap


class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private lateinit var mBinding: FragmentLoginBinding
    private var email: String = ""
    private var password: String = ""
    var bundle = Bundle()
    override val fragmentLayoutId: Int
        get() = R.layout.fragment_login

    override fun setUpUi(binding: FragmentLoginBinding) {
        mBinding = binding
        initView()
        initListener()
    }

    override fun setupObservers() {
    }

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false)

        return mBinding.root
    }*/

    private fun initView() {
        mBinding.etEmail.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                email = mBinding.etEmail.text.toString()
            }
        })
        mBinding.etPassword.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                password = mBinding.etPassword.text.toString()
            }
        })

    }

    private fun initListener() {

        mBinding.btnStart.setOnClickListener { v ->
            v.disableMultiTap()
            if (checkIfHasNetwork()) {
                if (checkLoginValidations()) {
                    findNavController().navigate(R.id.successDialogFragment)
                }
            } else {
                showNoInternetAlert()
            }
        }
        mBinding.tvForgotPassword.setOnClickListener { v ->
            v.disableMultiTap()
            bundle.putString("emailId", email)
            findNavController().navigate(R.id.forgotPasswordFragment, bundle)
        }
    }

    private fun checkLoginValidations(): Boolean {
        if (TextUtils.isEmpty(email)) {
            mBinding.etEmail.error = resources.getString(R.string.error_enter_email)
            return false
        } else if (!isValidEmailId(email)) {
            mBinding.etEmail.error = resources.getString(R.string.error_enter_valid_email)
            return false
        } else if (TextUtils.isEmpty(password)) {
            mBinding.etPassword.error = resources.getString(R.string.error_enter_password)
            return false
        } else if (password.length < 8) {
            mBinding.etPassword.error = resources.getString(R.string.error_password_length)
            return false
        }
        return true
    }
}