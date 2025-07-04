package com.medicalreport.view.main.dialog

import android.graphics.Bitmap

interface SignatureCallBack {
    fun onClickSave(transparentSignatureBitmap: Bitmap)
}