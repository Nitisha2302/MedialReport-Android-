package com.medicalreport.base

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.medicalreport.R
import com.medicalreport.utils.Util.checkIfHasNetwork
import com.medicalreport.utils.Util.showDarkErrorToast
import com.medicalreport.utils.Util.showDarkInternetToast
import com.medicalreport.utils.Util.showDarkSuccessToast
import com.medicalreport.utils.Util.showErrorToast
import com.medicalreport.utils.Util.showInternetToast
import com.medicalreport.utils.Util.showSuccessToast
import com.medicalreport.utils.isNightMode


open class BaseDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            dialog.setCanceledOnTouchOutside(true)
            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val metrics = context.resources.displayMetrics
                val orientation = context.resources.configuration.orientation

                // Adjust width based on orientation
                val dialogWidth = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    (metrics.widthPixels * 0.7f).toInt() // smaller width in landscape
                } else {
                    (metrics.widthPixels * 0.9f).toInt() // wider in portrait
                }

                setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    protected fun showErrorAlert(error: String) {
        if (checkIfHasNetwork()) {
            if (!isNightMode(activity)) {
                activity?.let { showErrorToast(it, error) }
            }else{
                activity?.let { showDarkErrorToast(it,error) }
            }
        } else {
            showNoInternetAlert()

        }
    }

    protected fun showSuccessAlert(message: String) {
        if (!isNightMode(activity)) {
            // Light mode
            activity?.let { showSuccessToast(it,message) }
        } else {
            // Dark mode
            activity?.let { showDarkSuccessToast(it,message) }

        }


    }

    private fun showNoInternetAlert() {
        if (!isNightMode(activity)) {
            // Light mode
            activity?.let { showInternetToast(it,getString(R.string.connection_lost)) }
        } else {
            // Dark mode
            activity?.let { showDarkInternetToast(it,getString(R.string.connection_lost)) }

        }

    }

}