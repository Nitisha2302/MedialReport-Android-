package com.medicalreport.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.medicalreport.R;


public class Constants {
    //    public static String BASE_URL= "https://doctor.testpentas.in/api/";
    //    public static String BASE_URL = "https://doctors.gwifx.org.uk/api/";
    public static String BASE_URL = "https://ekamhealthservices.com/api/";
    public static String tag = "medicalProvider_";

    public static final String SUFFIX_PDF = ".pdf";

    public static String getPermissionStatus(Activity activity, String androidPermissionName) {
        if (ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)) {
                return "blocked";
            }
            return "denied";
        }
        return "granted";
    }

}
