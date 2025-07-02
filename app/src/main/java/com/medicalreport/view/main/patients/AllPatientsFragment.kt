package com.medicalreport.view.main.patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentAllPatientsBinding


class AllPatientsFragment : BaseFragment<FragmentAllPatientsBinding>() {
    private lateinit var mBinding: FragmentAllPatientsBinding
    override val fragmentLayoutId: Int
        get() = R.layout.fragment_all_patients

    override fun setUpUi(binding: FragmentAllPatientsBinding) {
        mBinding = binding
    }

    override fun setupObservers() {

    }

}