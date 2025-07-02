package com.medicalreport.view.main.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicalreport.R
import com.medicalreport.base.BaseDialogFragment
import com.medicalreport.databinding.FragmentLogoutBinding
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.view.auth.AuthActivity
import com.medicalreport.view.main.MainActivity

class LogoutFragment : BaseDialogFragment() {
    private lateinit var mBinding: FragmentLogoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentLogoutBinding.inflate(inflater, container, false)
        initListener()
        return mBinding.root
    }

    private fun initListener() {
        mBinding.btCancel.setOnClickListener {
            it.disableMultiTap()
            dialog?.dismiss()
        }
        mBinding.btLogout.setOnClickListener {
            it.disableMultiTap()
            startActivity(Intent(context, AuthActivity::class.java))
            (requireActivity() as MainActivity).finish()
            dialog?.dismiss()
        }
    }

}