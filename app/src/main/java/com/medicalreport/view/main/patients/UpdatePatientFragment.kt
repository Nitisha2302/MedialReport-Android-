package com.medicalreport.view.main.patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentUpdatePatientBinding


class UpdatePatientFragment : BaseFragment<FragmentUpdatePatientBinding>() {
    private lateinit var mBinding: FragmentUpdatePatientBinding
    override val fragmentLayoutId: Int
        get() = R.layout.fragment_update_patient

    override fun setUpUi(binding: FragmentUpdatePatientBinding) {
        mBinding = binding
    }

    override fun setupObservers() {

    }


}