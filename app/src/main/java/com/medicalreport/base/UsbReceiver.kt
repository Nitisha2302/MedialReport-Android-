package com.medicalreport.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.widget.Toast
import com.medicalreport.view.main.usb.UsbCameraActivity

class UsbAttachedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            val usbDevice = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            Toast.makeText(context,"USB Attached: $usbDevice", Toast.LENGTH_SHORT).show()
            val launchIntent = Intent(context, UsbCameraActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(UsbManager.EXTRA_DEVICE, usbDevice)
            }
            context?.startActivity(launchIntent)
        }
    }
}

