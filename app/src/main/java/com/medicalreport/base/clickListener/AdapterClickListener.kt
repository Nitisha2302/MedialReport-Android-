package com.medicalreport.base.clickListener

import android.view.View

interface AdapterClickListener {
    fun onItemClick(view: View?, pos: Int, `object`: Any?)

}