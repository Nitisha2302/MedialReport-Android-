package com.medicalreport.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.databinding.FragmentHomeBinding
import com.medicalreport.utils.disableMultiTap


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private lateinit var mBinding: FragmentHomeBinding
    private var bundle = Bundle()
    override val fragmentLayoutId: Int
        get() = R.layout.fragment_home

    override fun setUpUi(binding: FragmentHomeBinding) {
        mBinding = binding
        initListener()
    }

    private fun initListener() {
        mBinding.clNewPatient.setOnClickListener {
            it.disableMultiTap()
            findNavController().navigate(R.id.newPatientFragment)
        }
        mBinding.mcvPatientHistory.setOnClickListener {
            it.disableMultiTap()
            bundle.putString("fromWhere", "patientHistory")
            findNavController().navigate(R.id.nav_patient)
        }
    }

    override fun setupObservers() {

    }


}