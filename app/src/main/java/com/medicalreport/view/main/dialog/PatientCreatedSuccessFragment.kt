package com.medicalreport.view.main.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicalreport.R
import com.medicalreport.base.BaseDialogFragment
import com.medicalreport.databinding.FragmentPatientCreatedSuccessBinding
import com.medicalreport.utils.disableMultiTap


class PatientCreatedSuccessFragment : BaseDialogFragment() {
    private lateinit var mBinding: FragmentPatientCreatedSuccessBinding
    var clickListener: ClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentPatientCreatedSuccessBinding.inflate(inflater, container, false)
        initListener()
        return mBinding.root
    }

    private fun initListener() {
        mBinding.btHome.setOnClickListener {
            it.disableMultiTap()
            val callback = targetFragment as? ClickListener
            callback?.onClickHome()
        }
        mBinding.btCreatePdf.setOnClickListener {
            it.disableMultiTap()
            val callback = targetFragment as? ClickListener
            callback?.onClickCreateReport()
        }
    }

    interface ClickListener {
        fun onClickHome()
        fun onClickCreateReport()
    }

}