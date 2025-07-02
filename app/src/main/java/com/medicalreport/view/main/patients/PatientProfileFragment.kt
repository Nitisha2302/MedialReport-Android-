package com.medicalreport.view.main.patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentPatientProfileBinding


class PatientProfileFragment : BaseFragment<FragmentPatientProfileBinding>() {
    private lateinit var mBinding: FragmentPatientProfileBinding
    override val fragmentLayoutId: Int
        get() = R.layout.fragment_patient_profile

    override fun setUpUi(binding: FragmentPatientProfileBinding) {
        mBinding = binding
    }

    override fun setupObservers() {

    }

}