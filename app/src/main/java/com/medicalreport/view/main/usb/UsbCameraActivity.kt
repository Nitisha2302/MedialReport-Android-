package com.medicalreport.view.main.usb

import android.content.ContentValues
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SeekBar
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jiangdg.usbcamera.UVCCameraHelper
import com.jiangdg.usbcamera.UVCCameraHelper.OnMyDevConnectListener
import com.jiangdg.usbcamera.utils.FileUtils
import com.medicalreport.R
import com.medicalreport.base.MainApplication
import com.medicalreport.base.clickListener.AdapterClickListener
import com.medicalreport.databinding.ActivityUsbCameraBinding
import com.medicalreport.fps.FpsCounter
import com.medicalreport.modal.response.ImageData
import com.medicalreport.modal.response.SelectedDoctorsResponse
import com.medicalreport.utils.Prefs
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.view.adapter.ImageAdapter
import com.medicalreport.view.main.reports.PdfGenerationActivity
import com.serenegiant.usb.CameraDialog.CameraDialogParent
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usb.common.AbstractUVCCameraHandler
import com.serenegiant.usb.common.AbstractUVCCameraHandler.OnCaptureListener
import com.serenegiant.usb.common.AbstractUVCCameraHandler.OnEncodeResultListener
import com.serenegiant.usb.common.AbstractUVCCameraHandler.OnPreViewResultListener
import com.serenegiant.usb.encoder.RecordParams
import com.serenegiant.usb.widget.CameraViewInterface
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class UsbCameraActivity : AppCompatActivity(), CameraDialogParent, CameraViewInterface.Callback {
    private lateinit var mBinding: ActivityUsbCameraBinding

    private var mCameraHelper: UVCCameraHelper? = null
    private lateinit var mUVCCameraView: CameraViewInterface
    private val mFpsCounter: FpsCounter = FpsCounter()
    private var mDialog: AlertDialog? = null

    // Use AtomicBoolean for thread-safe state management
    private val isRequest = AtomicBoolean(false)
    private val isPreview = AtomicBoolean(false)
    private val isRecordYuv = AtomicBoolean(false)
    private val isBrightness = AtomicBoolean(false)
    private val isContrast = AtomicBoolean(false)
    private val isWB = AtomicBoolean(false)
    private val isSurfaceCreated = AtomicBoolean(false)
    private val isCameraConnected = AtomicBoolean(false)
    private val isHelperInitialized = AtomicBoolean(false)
    private val isDestroyed = AtomicBoolean(false)

    private val imageList = mutableListOf<ImageData>()
    private var selectedDoctors: ArrayList<SelectedDoctorsResponse>? = arrayListOf()

    var imageAdapter: ImageAdapter? = null

    var fullName: String = ""
    var gender: String = ""
    var age: String = ""
    var patientId: Int? = 0

    private val mainHandler = Handler(Looper.getMainLooper())
    private val cameraHandler = Handler(Looper.getMainLooper())

    // Retry mechanism
    private var previewRetryCount = 0
    private val maxRetryCount = 5
    private val retryDelayMs = 1000L

    private val listener: OnMyDevConnectListener = object : OnMyDevConnectListener {
        override fun onAttachDev(device: UsbDevice) {
            Log.d(TAG, "USB Device attached: ${device.deviceName}")
            if (isDestroyed.get()) return

            mainHandler.post {
                val helper = mCameraHelper
                if (helper != null && !isRequest.get()) {
                    isRequest.set(true)
                    Log.d(TAG, "Requesting permission for device")
                    helper.requestPermission(0)
                }
            }
        }

        override fun onDettachDev(device: UsbDevice) {
            Log.d(TAG, "USB Device detached: ${device.deviceName}")
            if (isDestroyed.get()) return

            mainHandler.post {
                handleDeviceDisconnection(device)
            }
        }

        override fun onConnectDev(device: UsbDevice, isConnected: Boolean) {
            Log.d(TAG, "USB Device connect status: $isConnected for ${device.deviceName}")
            if (isDestroyed.get()) return

            if (!isConnected) {
                mainHandler.post {
                    showShortMsg("Failed to connect. Please check camera or try different USB port.")
                    handleConnectionFailure()
                }
                return
            }

            isCameraConnected.set(true)
            previewRetryCount = 0

            mainHandler.post {
                showShortMsg("Camera connected successfully")
                // Start preview with proper delay
                cameraHandler.postDelayed({
                    startPreviewWithRetry()
                }, 2000) // Increased delay for better stability
            }
        }

        override fun onDisConnectDev(device: UsbDevice) {
            Log.d(TAG, "USB Device disconnected: ${device.deviceName}")
            if (isDestroyed.get()) return

            mainHandler.post {
                handleDeviceDisconnection(device)
            }
        }
    }

    companion object {
        private const val TAG = "UsbCameraActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_usb_camera)

        Log.d(TAG, "onCreate started")

        // Handle intent data
        intent.extras?.let { bundle ->
            fullName = bundle.getString("patient_name").orEmpty()
            age = bundle.getString("patient_age").orEmpty()
            gender = bundle.getString("patient_gender").orEmpty()
            selectedDoctors = bundle.getParcelableArrayList("selectedDoctors")
        }

        // Handle USB device attached intent
        handleUsbIntent(intent)

        initView()
        initCamera()
        initListener()

        Log.d(TAG, "onCreate completed")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "New intent received")
        if (!isDestroyed.get()) {
            handleUsbIntent(intent)
        }
    }

    private fun handleUsbIntent(intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Intent action: $action")

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            device?.let {
                Log.d(TAG, "USB device attached via intent: ${it.deviceName}")
                // Handle device attachment if needed
            }
        }
    }

    private fun initCamera() {
        try {
            Log.d(TAG, "Initializing camera")

            // Camera View and Helper Setup
            mUVCCameraView = mBinding.cameraView
            mUVCCameraView.setCallback(this)

            // Initialize camera helper
            mCameraHelper = UVCCameraHelper.getInstance()
            val helper = mCameraHelper ?: return

            helper.initUSBMonitor(this, mUVCCameraView, listener)

            // Set preview frame listener
            helper.setOnPreviewFrameListener(OnPreViewResultListener { nv21Yuv ->
                if (!isDestroyed.get()) {
                    mFpsCounter.count()
                    if (isRecordYuv.get()) {
                        FileUtils.putFileStream(nv21Yuv, 0, nv21Yuv.size)
                    }
                }
            })

            isHelperInitialized.set(true)
            Log.d(TAG, "Camera helper initialized successfully")

            // Register USB monitoring with proper delay
            cameraHandler.postDelayed({
                if (!isDestroyed.get() && isHelperInitialized.get()) {
                    helper.registerUSB()
                    Log.d(TAG, "USB monitoring registered")
                }
            }, 1000) // Increased delay

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing camera", e)
            showShortMsg("Error initializing camera: ${e.message}")
        }
    }

    private fun startPreviewWithRetry() {
        if (isDestroyed.get()) return

        Log.d(TAG, "Starting preview with retry - attempt ${previewRetryCount + 1}")

        if (previewRetryCount >= maxRetryCount) {
            Log.e(TAG, "Max retry attempts reached for preview")
            showShortMsg("Failed to start camera preview after multiple attempts")
            return
        }

        if (attemptStartPreview()) {
            Log.d(TAG, "Preview started successfully")
            previewRetryCount = 0
        } else {
            previewRetryCount++
            Log.d(TAG, "Preview start failed, retrying in ${retryDelayMs}ms")
            cameraHandler.postDelayed({
                startPreviewWithRetry()
            }, retryDelayMs)
        }
    }

    private fun attemptStartPreview(): Boolean {
        if (isDestroyed.get()) return false

        Log.d(TAG, "Attempting to start preview - Connected: ${isCameraConnected.get()}, Surface: ${isSurfaceCreated.get()}, Preview: ${isPreview.get()}, Helper: ${isHelperInitialized.get()}")

        if (!isCameraConnected.get() || !isSurfaceCreated.get() || isPreview.get() || !isHelperInitialized.get()) {
            Log.d(TAG, "Not ready to start preview")
            return false
        }

        val helper = mCameraHelper ?: return false

        if (!helper.isCameraOpened) {
            Log.d(TAG, "Camera not opened yet")
            return false
        }

        return try {
            Log.d(TAG, "Starting camera preview...")
            helper.startPreview(mUVCCameraView)
            isPreview.set(true)
            Log.d(TAG, "Preview started successfully")

            // Apply camera settings after preview starts
            applyCameraSettings()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start preview", e)
            showShortMsg("Failed to start preview: ${e.message}")
            false
        }
    }

    private fun applyCameraSettings() {
        if (isDestroyed.get()) return

        cameraHandler.postDelayed({
            if (isDestroyed.get()) return@postDelayed

            try {
                val helper = mCameraHelper
                if (helper != null && helper.isCameraOpened && isPreview.get()) {
                    Log.d(TAG, "Applying camera settings")

                    // Apply brightness and contrast
                    val brightness = Prefs.init().keyBrightness
                    val contrast = Prefs.init().keyContrast

                    helper.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS, brightness)
                    helper.setModelValue(UVCCameraHelper.MODE_CONTRAST, contrast)

                    mainHandler.post {
                        if (!isDestroyed.get()) {
                            mBinding.seekbarBrightness.progress = brightness
                            mBinding.seekbarContrast.progress = contrast
                        }
                    }

                    // Apply white balance
                    Prefs.init().keyWhiteBalance?.let {
                        if (it == "auto") {
                            helper.startAutoWhiteBalance()
                        }
                    }

                    // Set resolution if supported
                    applyResolutionSettings(helper)

                    Log.d(TAG, "Camera settings applied successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error applying camera settings", e)
            }
        }, 1500) // Increased delay for settings application
    }

    private fun applyResolutionSettings(helper: UVCCameraHelper) {
        try {
            val supportedSizes = helper.supportedPreviewSizes?.map { "${it.width}x${it.height}" } ?: emptyList()
            Log.d(TAG, "Supported resolutions: $supportedSizes")

            Prefs.init().keyResolution?.let { res ->
                if (res in supportedSizes) {
                    val parts = res.split("x")
                    if (parts.size == 2) {
                        val width = parts[0].toIntOrNull() ?: 0
                        val height = parts[1].toIntOrNull() ?: 0
                        if (width > 0 && height > 0) {
                            helper.updateResolution(width, height)
                            Log.d(TAG, "Resolution updated to ${width}x${height}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error applying resolution settings", e)
        }
    }

    private fun handleDeviceDisconnection(device: UsbDevice) {
        val helper = mCameraHelper
        if (helper != null && helper.isCameraOpened) {
            try {
                if (isPreview.get()) {
                    helper.stopPreview()
                    isPreview.set(false)
                }
                helper.closeCamera()
                showShortMsg("${device.deviceName} disconnected")
            } catch (e: Exception) {
                Log.e(TAG, "Error during device disconnection", e)
            }
        }

        isRequest.set(false)
        isCameraConnected.set(false)
        isPreview.set(false)
        previewRetryCount = 0
    }

    private fun handleConnectionFailure() {
        isPreview.set(false)
        isCameraConnected.set(false)
        isRequest.set(false)
        previewRetryCount = 0
    }

    private fun initListener() {
        mBinding.ivSettings.setOnClickListener {
            it.disableMultiTap()
            mBinding.llayoutContrast.visibility = View.GONE
            mBinding.llayoutBrightness.visibility = View.GONE

            val helper = mCameraHelper
            if (helper == null || !helper.isCameraOpened) {
                showShortMsg("Camera not available")
                return@setOnClickListener
            }
            showResolutionListDialog()
        }

        mBinding.ivBrightness.setOnClickListener {
            it.disableMultiTap()
            mBinding.llayoutContrast.visibility = View.GONE
            if (!isBrightness.get()) {
                isBrightness.set(true)
                mBinding.llayoutBrightness.visibility = View.VISIBLE
            } else {
                isBrightness.set(false)
                mBinding.llayoutBrightness.visibility = View.GONE
            }
        }

        mBinding.ivWhiteBalance.setOnClickListener {
            it.disableMultiTap()
            val helper = mCameraHelper
            if (helper == null || !helper.isCameraOpened) {
                showShortMsg("Camera not available")
                return@setOnClickListener
            }
            try {
                helper.startAutoWhiteBalance()
                Prefs.init().keyWhiteBalance = "auto"
                showShortMsg("Auto white balance applied")
            } catch (e: Exception) {
                Log.e(TAG, "Error applying white balance", e)
                showShortMsg("Failed to apply white balance")
            }
        }

        mBinding.ivContrast.setOnClickListener {
            it.disableMultiTap()
            mBinding.llayoutBrightness.visibility = View.GONE
            if (!isContrast.get()) {
                isContrast.set(true)
                mBinding.llayoutContrast.visibility = View.VISIBLE
            } else {
                isContrast.set(false)
                mBinding.llayoutContrast.visibility = View.GONE
            }
        }

        mBinding.ivVideo.setOnClickListener {
            it.disableMultiTap()
            handleVideoRecording()
        }

        mBinding.ivCamera.setOnClickListener {
            it.disableMultiTap()
            handleImageCapture()
        }

        mBinding.ivBack.setOnClickListener {
            it.disableMultiTap()
            finish()
        }

        mBinding.ivSave.setOnClickListener {
            it.disableMultiTap()
            handleSaveAction()
        }
    }

    private fun handleVideoRecording() {
        isRecordYuv.set(false)
        val helper = mCameraHelper
        if (helper == null || !helper.isCameraOpened || !isPreview.get()) {
            showShortMsg("Camera not ready for recording")
            return
        }

        try {
            if (!helper.isPushing) {
                val params = RecordParams()
                params.recordPath = getRecordFileName()
                params.recordDuration = 0
                params.isVoiceClose = mBinding.switchRecVoice.isChecked
                params.isSupportOverlay = true

                helper.startPusher(params, object : OnEncodeResultListener {
                    override fun onEncodeResult(
                        data: ByteArray,
                        offset: Int,
                        length: Int,
                        timestamp: Long,
                        type: Int
                    ) {
                        if (type == 1) {
                            FileUtils.putFileStream(data, offset, length)
                        }
                    }

                    override fun onRecordResult(videoPath: String) {
                        if (TextUtils.isEmpty(videoPath)) return

                        mainHandler.post {
                            saveVideoToMediaStore(videoPath)
                        }
                    }
                })
                showShortMsg("Recording started...")
                mBinding.switchRecVoice.isEnabled = false
            } else {
                FileUtils.releaseFile()
                helper.stopPusher()
                showShortMsg("Recording stopped...")
                mBinding.switchRecVoice.isEnabled = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling video recording", e)
            showShortMsg("Error during video recording: ${e.message}")
        }
    }

    private fun saveVideoToMediaStore(videoPath: String) {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.TITLE, "Medical Report Video")
                put(MediaStore.Video.Media.DESCRIPTION, "Video captured from USB camera")
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            }

            val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                contentResolver.openOutputStream(uri)?.close()
                Toast.makeText(this, "Video saved successfully", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Video saved successfully: $videoPath")
            } else {
                Toast.makeText(this, "Failed to save video", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Failed to save video to media store")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving video to media store", e)
            Toast.makeText(this, "Error saving video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleImageCapture() {
        val helper = mCameraHelper
        if (helper == null || !helper.isCameraOpened || !isPreview.get()) {
            showShortMsg("Camera not ready for capture")
            return
        }

        try {
            val picPath = getImageFileName()
            helper.capturePicture(picPath, OnCaptureListener { path ->
                if (TextUtils.isEmpty(path)) return@OnCaptureListener

                mainHandler.post {
                    if (!isDestroyed.get()) {
                        Toast.makeText(this@UsbCameraActivity, "Image saved: $path", Toast.LENGTH_SHORT).show()
                        imageList.add(ImageData(Uri.fromFile(File(path))))
                        setImageClickAdapter(imageList)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error capturing image", e)
            showShortMsg("Error capturing image: ${e.message}")
        }
    }

    private fun handleSaveAction() {
        val bundle = Bundle()
        bundle.putString("patient_name", fullName)
        bundle.putString("patient_age", age)
        bundle.putString("patient_gender", gender)
        bundle.putString("fromWhere", "usbCamera")
        bundle.putParcelableArrayList("imagesList", imageList as ArrayList<out Parcelable>)
        bundle.putParcelableArrayList("selectedDoctors", selectedDoctors as ArrayList<out Parcelable>)

        val intent = Intent(this, PdfGenerationActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    // Rest of the methods remain the same...
    private fun setImageClickAdapter(imageList: MutableList<ImageData>) {
        if (isDestroyed.get()) return

        try {
            val itemDecor = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    val fromItem = imageList[fromPos]
                    val toItem = imageList[toPos]
                    imageList[fromPos] = toItem
                    imageList[toPos] = fromItem
                    imageAdapter?.notifyItemMoved(fromPos, toPos)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            })
            itemDecor.attachToRecyclerView(mBinding.photosRecyclerview)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up ItemTouchHelper", e)
        }

        imageAdapter = ImageAdapter(imageList, object : AdapterClickListener {
            override fun onItemClick(view: View?, pos: Int, `object`: Any?) {
                val itemUpdated = imageList[pos]
                if (view?.id == R.id.ivDeletePhoto) {
                    imageList.remove(itemUpdated)
                    imageAdapter?.notifyDataSetChanged()
                }
            }
        })
        mBinding.photosRecyclerview.adapter = imageAdapter
    }

    private fun initView() {
        mBinding.seekbarBrightness.max = 100
        mBinding.seekbarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser || isDestroyed.get()) return

                try {
                    val helper = mCameraHelper
                    if (helper != null && helper.isCameraOpened && isPreview.get()) {
                        helper.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS, progress)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting brightness", e)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Prefs.init().keyBrightness = seekBar.progress
            }
        })

        mBinding.seekbarContrast.max = 100
        mBinding.seekbarContrast.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser || isDestroyed.get()) return

                try {
                    val helper = mCameraHelper
                    if (helper != null && helper.isCameraOpened && isPreview.get()) {
                        helper.setModelValue(UVCCameraHelper.MODE_CONTRAST, progress)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting contrast", e)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Prefs.init().keyContrast = seekBar.progress
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        if (!isDestroyed.get()) {
            mCameraHelper?.let { helper ->
                if (!helper.isCameraOpened) {
                    helper.registerUSB()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        try {
            mCameraHelper?.unregisterUSB()
        } catch (e: Exception) {
            Log.e(TAG, "Error during onStop", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        isDestroyed.set(true)

        // Remove all pending callbacks
        mainHandler.removeCallbacksAndMessages(null)
        cameraHandler.removeCallbacksAndMessages(null)

        try {
            FileUtils.releaseFile()
            mCameraHelper?.let { helper ->
                if (isPreview.get()) {
                    helper.stopPreview()
                }
                if (helper.isCameraOpened) {
                    helper.closeCamera()
                }
                helper.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup", e)
        }
    }

    private fun getRecordFileName(): String {
        return "${FileUtils.ROOT_PATH}/${MainApplication.get().DIRECTORY_NAME}/videos/${System.currentTimeMillis()}"
    }

    private fun getImageFileName(): String {
        return "${FileUtils.ROOT_PATH}/${MainApplication.get().DIRECTORY_NAME}/images/${System.currentTimeMillis()}${UVCCameraHelper.SUFFIX_JPEG}"
    }

    private fun showResolutionListDialog() {
        if (isDestroyed.get()) return

        val builder = AlertDialog.Builder(this@UsbCameraActivity)
        val rootView: View = LayoutInflater.from(this@UsbCameraActivity)
            .inflate(R.layout.layout_dialog_list, null)
        val listView = rootView.findViewById<ListView>(R.id.listview_dialog)
        val resolutionList = getResolutionList()

        if (resolutionList.isEmpty()) {
            showShortMsg("No supported resolutions found")
            return
        }

        val adapter = ArrayAdapter(this@UsbCameraActivity, android.R.layout.simple_list_item_1, resolutionList)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, position, _ ->
            val helper = mCameraHelper
            if (helper == null || !helper.isCameraOpened) return@OnItemClickListener

            try {
                val resolution = adapterView.getItemAtPosition(position) as String
                val tmp = resolution.split("x")
                if (tmp.size >= 2) {
                    val width = tmp[0].toIntOrNull() ?: 0
                    val height = tmp[1].toIntOrNull() ?: 0
                    if (width > 0 && height > 0) {
                        helper.updateResolution(width, height)
                        Prefs.init().keyResolution = resolution
                        showShortMsg("Resolution changed to $resolution")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating resolution", e)
                showShortMsg("Error updating resolution")
            }
            mDialog?.dismiss()
        }
        builder.setView(rootView)
        mDialog = builder.create()
        mDialog?.show()
    }

    private fun getResolutionList(): MutableList<String> {
        val resolutions = mutableListOf<String>()
        try {
            mCameraHelper?.let { helper ->
                val list = helper.supportedPreviewSizes
                if (list != null && list.isNotEmpty()) {
                    for (size in list) {
                        size?.let {
                            resolutions.add("${it.width}x${it.height}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting resolution list", e)
        }
        return resolutions
    }

    private fun showShortMsg(msg: String) {
        if (isDestroyed.get()) return

        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Toast: $msg")
        }
    }

    override fun getUSBMonitor(): USBMonitor {
        return mCameraHelper?.usbMonitor ?: throw IllegalStateException("Camera helper not initialized")
    }

    override fun onDialogResult(canceled: Boolean) {
        if (canceled) {
            showShortMsg("Operation cancelled")
        }
    }

    override fun onSurfaceCreated(view: CameraViewInterface?, surface: Surface?) {
        Log.d(TAG, "Surface created")
        if (isDestroyed.get()) return

        isSurfaceCreated.set(true)

        // Attempt to start preview with proper delay
        cameraHandler.postDelayed({
            if (!isDestroyed.get()) {
                startPreviewWithRetry()
            }
        }, 1000)
    }

    override fun onSurfaceChanged(view: CameraViewInterface?, surface: Surface?, width: Int, height: Int) {
        Log.d(TAG, "Surface changed: ${width}x${height}")
    }

    override fun onSurfaceDestroy(view: CameraViewInterface?, surface: Surface?) {
        Log.d(TAG, "Surface destroyed")
        if (isDestroyed.get()) return

        isSurfaceCreated.set(false)

        if (isPreview.get()) {
            try {
                mCameraHelper?.stopPreview()
                isPreview.set(false)
                Log.d(TAG, "Preview stopped due to surface destroy")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping preview during surface destroy", e)
            }
        }
    }
}
