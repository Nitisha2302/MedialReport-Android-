package com.medicalreport.view.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.medicalreport.databinding.FragmentTermsConditionBinding
import com.medicalreport.utils.hideProgress
import com.medicalreport.utils.showProgressDialog

class TermsConditionFragment : Fragment() {
    private lateinit var mBinding: FragmentTermsConditionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentTermsConditionBinding.inflate(inflater, container, false)
        initSetWebView()
        return mBinding.root
    }

    private fun initSetWebView() {
        mBinding.webView.settings.javaScriptEnabled = true
        mBinding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                showProgressDialog(requireContext())
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideProgress()
            }
        }

        // Load URL
        mBinding.webView.loadUrl("https://ekamhealthservices.com/term-conditions")
    }


}