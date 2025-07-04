package com.medicalreport.view.main.dialog

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.gcacace.signaturepad.views.SignaturePad
import com.medicalreport.R
import com.medicalreport.base.BaseDialogFragment
import com.medicalreport.databinding.FragmentSignatureDialogBinding
import kotlin.text.clear


class SignatureDialogFragment : DialogFragment() {
    private lateinit var mBinding: FragmentSignatureDialogBinding
    private lateinit var onSignatureCallback: ClickListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSignatureDialogBinding.inflate(inflater, container, false)
        initView()
        initListener()
        return mBinding.root
    }

    private fun initListener() {
        mBinding.btnComplete.setOnClickListener {
            //Set the captured signature in Imageview
            mBinding.ivSignature.setImageBitmap(mBinding.signaturePad.transparentSignatureBitmap)
        }
        mBinding.btnClear.setOnClickListener {
            //Clear captured signature
            mBinding.signaturePad.clear()
        }
        mBinding.btnAddSign.setOnClickListener {
            onSignatureCallback.onClickSave(mBinding.signaturePad.transparentSignatureBitmap)
            dialog?.dismiss()

        }
        mBinding.ivBack.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun initView() {
        mBinding.signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
            }

            override fun onSigned() {

            }

            override fun onClear() {
            }
        })
    }

    fun setOnSignatureCallback(callback: ClickListener) {
        onSignatureCallback = callback
    }

    interface ClickListener {
        fun onClickSave(image: Bitmap)
    }

}