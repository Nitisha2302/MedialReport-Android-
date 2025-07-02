package com.medicalreport.view.main.patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentNewPatientBinding


class NewPatientFragment : BaseFragment<FragmentNewPatientBinding>() {
    private lateinit var mBinding: FragmentNewPatientBinding
    override val fragmentLayoutId: Int
        get() = R.layout.fragment_new_patient

    override fun setUpUi(binding: FragmentNewPatientBinding) {
        mBinding = binding
    }

    override fun setupObservers() {

    }


}