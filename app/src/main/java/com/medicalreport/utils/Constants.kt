package com.medicalreport.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Constants {
    //    public static String BASE_URL= "https://doctor.testpentas.in/api/";
    //    public static String BASE_URL = "https://doctors.gwifx.org.uk/api/";
    var BASE_URL: String = "https://ekamhealthservices.com/api/"
    var tag: String = "medicalProvider_"

    const val SUFFIX_PDF: String = ".pdf"

    fun getPermissionStatus(activity: Activity, androidPermissionName: String): String {
        if (ContextCompat.checkSelfPermission(
                activity,
                androidPermissionName
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    androidPermissionName
                )
            ) {
                return "blocked"
            }
            return "denied"
        }
        return "granted"
    }


}
