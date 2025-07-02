package com.medicalreport.view.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicalreport.R
import com.medicalreport.databinding.FragmentTermsConditionBinding

class TermsConditionFragment : Fragment() {
    private lateinit var mBinding: FragmentTermsConditionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentTermsConditionBinding.inflate(inflater, container, false)
        return mBinding.root
    }

}