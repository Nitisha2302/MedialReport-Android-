package com.medicalreport.view.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.medicalreport.R
import com.medicalreport.base.BaseDialogFragment
import com.medicalreport.databinding.FragmentSuccessDialogBinding
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.view.main.MainActivity

class SuccessDialogFragment : BaseDialogFragment() {
    private lateinit var mBinding: FragmentSuccessDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentSuccessDialogBinding.inflate(inflater, container, false)
        initListener()
        return mBinding.root
    }


    private fun initListener() {
        mBinding.btLogoutLogout.setOnClickListener {
            it.disableMultiTap()
            startActivity(Intent(context, MainActivity::class.java))
            (requireActivity() as AuthActivity).finish()
            dialog?.dismiss()
        }
    }

}