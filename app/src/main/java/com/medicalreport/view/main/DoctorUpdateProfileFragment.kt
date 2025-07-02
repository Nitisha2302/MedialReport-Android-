package com.medicalreport.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentDoctorUpdateProfileBinding

class DoctorUpdateProfileFragment : BaseFragment<FragmentDoctorUpdateProfileBinding>() {
    private lateinit var mBinding: FragmentDoctorUpdateProfileBinding
    override val fragmentLayoutId: Int
        get() = R.layout.fragment_doctor_update_profile

    override fun setUpUi(binding: FragmentDoctorUpdateProfileBinding) {
        mBinding = binding
    }

    override fun setupObservers() {

    }


}