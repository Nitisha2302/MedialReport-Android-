package com.medicalreport.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.medicalreport.utils.Prefs

class UninstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
            val packageName = intent.data?.encodedSchemeSpecificPart
            Log.d("UninstallReceiver", "Package removed: $packageName")

            // Perform cleanup operations here
            // For example: Clearing SharedPreferences, deleting files, etc.
            Prefs.init().clear()
        }
    }
}