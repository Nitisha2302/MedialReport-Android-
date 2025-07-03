package com.medicalreport.utils

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.medicalreport.R
import com.medicalreport.base.MainApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.util.regex.Pattern

object Util {

    fun isValidEmailId(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    fun checkIfHasNetwork(): Boolean {
        val connectivityManager =
            MainApplication.get()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun showDarkErrorToast(context: FragmentActivity,error:String) {
        MotionToast.darkToast(
            context,
            "Error",
            error,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.helvetica_regular)
        )
    }

    fun showErrorToast(context: FragmentActivity,error:String) {
        MotionToast.createColorToast(
            context,
            "Error",
            error,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.helvetica_regular)
        )
    }

    fun showSuccessToast(context: FragmentActivity,message:String) {
        MotionToast.createColorToast(
            context,
            "Success",
            message,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.helvetica_regular)
        )
    }

    fun showDarkSuccessToast(context: FragmentActivity,message:String) {
        MotionToast.darkToast(
            context,
            "Success",
            message,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.helvetica_regular)
        )
    }

    fun showInternetToast(context: FragmentActivity,message:String){
        MotionToast.createColorToast(
            context,
            "NO Internet",
            message,
            MotionToastStyle.NO_INTERNET,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.helvetica_regular)
        )
    }

    fun showDarkInternetToast(context: FragmentActivity,message:String){
        MotionToast.darkToast(
            context,
            "NO Internet",
            message,
            MotionToastStyle.NO_INTERNET,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.helvetica_regular)
        )
    }
}

fun View.disableMultiTap() {
    try {
        isEnabled = false
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            isEnabled = true
        }, 500)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun runWithDelay(delay: Long, run: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        run()
    }, delay)
}

fun isNightMode(context: FragmentActivity?): Boolean {
    val mode = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
    return mode == Configuration.UI_MODE_NIGHT_YES
}

fun showToast(context: Context?, msg: String) {
    Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
}

fun createPartFromString(stringData: String): RequestBody {
    return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun toRequestBody(value: String): RequestBody {
    return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
}

fun toImageRequestBody(value: File): RequestBody {
    return RequestBody.create("image/*".toMediaTypeOrNull(), value)
}
private var progressDialog: Dialog? = null

fun showProgressDialog(context: Context) {
    if (progressDialog == null) progressDialog =
        Dialog(context, R.style.progressDialogStyle)

    progressDialog?.let {
        if (!it.isShowing) {
            it.setContentView(R.layout.progress_dialog)
            it.window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            it.setCancelable(false)
            it.show()
        }
    }
}

 fun hideProgress() {
    progressDialog?.let {
        if (it.isShowing) {
            it.dismiss()
            it.cancel()
        }
    }
}