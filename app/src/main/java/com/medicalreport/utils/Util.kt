package com.medicalreport.utils

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.medicalreport.R
import com.medicalreport.base.MainApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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


fun toIntRequestBody(value: Int?): RequestBody {
    return RequestBody.create("text/plain".toMediaTypeOrNull(), value.toString())
}
fun toRequestBody(value: String): RequestBody {
    return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
}

fun toPdfRequestBody(file: File): RequestBody {
    return file.asRequestBody("application/pdf".toMediaTypeOrNull())
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

// getCurrent Date
fun getCurrentDate(dateFormat: String?): String? {
    val format = SimpleDateFormat(dateFormat, Locale.ENGLISH)
    val date = Calendar.getInstance()
    return format.format(date.time)
}

// getCurrent Date
fun getCurrentDate(dateFormat: String?, days: Int): String? {
    val format = SimpleDateFormat(dateFormat, Locale.ENGLISH)
    val date = Calendar.getInstance()
    date.add(Calendar.DAY_OF_MONTH, days)
    return format.format(date.time)
}

fun convertImage(imagePath: String?): Bitmap {
    // Load the original image from a file or any source
    var originalImage = BitmapFactory.decodeFile(imagePath)

    // Get the dimensions of the original image
    val originalWidth = originalImage.width
    val originalHeight = originalImage.height

    // Set the default dimensions
    val defaultWidth = 512
    val defaultHeight = 1024

    // Calculate the aspect ratio of the original image
    val aspectRatio = originalWidth.toFloat() / originalHeight

    // Check if the original image exceeds the default dimensions
    if (originalWidth > defaultWidth || originalHeight > defaultHeight) {
        // Determine the new dimensions to maintain the aspect ratio
        var newWidth = defaultWidth
        var newHeight = (newWidth / aspectRatio).toInt()

        // Check if the new height exceeds the default height
        if (newHeight > defaultHeight) {
            newHeight = defaultHeight
            newWidth = (newHeight * aspectRatio).toInt()
        }

        // Resize the original image to the new dimensions
        originalImage = Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, true)
    }

    // Create a new Bitmap with the default dimensions
    val convertedImage = Bitmap.createBitmap(defaultWidth, defaultHeight, Bitmap.Config.ARGB_8888)

    // Create a Canvas object to draw on the new Bitmap
    val canvas = Canvas(convertedImage)
    canvas.drawColor(Color.BLACK) // Set the extra space to black color

    // Calculate the center position to draw the original image
    val centerX = (defaultWidth - originalImage.width) / 2
    val centerY = (defaultHeight - originalImage.height) / 2

    // Draw the resized original image onto the converted image
    canvas.drawBitmap(originalImage, centerX.toFloat(), centerY.toFloat(), Paint())
    return convertedImage
}

fun uriToBase64(uri: Uri, context: Context?): String {
    val filePath = getRealPathFromURI(uri, context)
    val fileBytes = getFileBytes(filePath)
    return Base64.encodeToString(fileBytes, Base64.DEFAULT)
}

fun getRealPathFromURI(uri: Uri, context: Context?): String {
    val contentResolver = context?.contentResolver
    val cursor = contentResolver?.query(uri, null, null, null, null)
    cursor?.moveToFirst()
    val columnIndex = cursor?.getColumnIndex(MediaStore.MediaColumns.DATA)
    val filePath = columnIndex?.let { cursor.getString(it) }
    cursor?.close()
    return filePath ?: ""
}

fun getFileBytes(filePath: String): ByteArray {
    val file = File(filePath)
    val fileBytes = ByteArray(file.length().toInt())
    val fis = FileInputStream(file)
    fis.read(fileBytes)
    fis.close()
    return fileBytes
}

fun filePathToBase64String(filePath: Uri, context: Context?): String? {
    val inputStream = context?.contentResolver?.openInputStream(filePath)
    val bytes = inputStream?.readBytes()
    return bytes?.let { Base64.encodeToString(it, Base64.NO_WRAP) }

}

fun frescoImageLoad(
    url: String?,
    resource: Int,
    simpleDrawee: SimpleDraweeView,
    isGif: Boolean
): DraweeController {
    var url = url
    if (url == null) {
        url = "null"
    }

    val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
        .build()

    val controller: DraweeController
    simpleDrawee.hierarchy.setPlaceholderImage(resource)
    simpleDrawee.hierarchy.setFailureImage(resource)

    if (isGif) {

        controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setOldController(simpleDrawee.controller)
            .setAutoPlayAnimations(true)
            .build()
    } else {
        controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setOldController(simpleDrawee.controller)
            .build()
    }


    return controller
}

fun getCircleImageFromFresco(view: SimpleDraweeView, imageUrl: String?, gender: String) {
    if (!imageUrl.isNullOrEmpty() || imageUrl == "imageUrl" || imageUrl == null) {
        var imageRequest: com.facebook.imagepipeline.request.ImageRequest? = null
        imageRequest = if (imageUrl == "imageUrl" || imageUrl == null) {
            if (gender == "Male" || gender == "male") {
                ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.dummy_doctor_avatar)
                    .setProgressiveRenderingEnabled(true)
                    .build()
            }else{
                ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.dummy_female_doctor_avatar)
                    .setProgressiveRenderingEnabled(true)
                    .build()
            }
        } else {
            ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(imageUrl))
                .setProgressiveRenderingEnabled(true)
                .build()
        }

        view.controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest)
            .setAutoPlayAnimations(false)
            .build()

        val roundingParameter = RoundingParams()
        roundingParameter.roundAsCircle = true
        view.hierarchy.roundingParams = roundingParameter


    }
}
