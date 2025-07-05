package com.medicalreport.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

object FileUriUtils {
    fun getRealPath(context: Context, uri: Uri): String? {
        var path: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, proj, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            path = cursor.getString(columnIndex)
            cursor.close()
        }
        return path
    }
}
