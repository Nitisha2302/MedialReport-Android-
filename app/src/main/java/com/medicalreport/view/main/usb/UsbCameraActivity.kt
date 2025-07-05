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
import kotlin.text.get

class UsbCameraActivity : AppCompatActivity(), CameraDialogParent, CameraViewInterface.Callback {
    private lateinit var mBinding: ActivityUsbCameraBinding

    private lateinit var mCameraHelper: UVCCameraHelper
    private lateinit var mUVCCameraView: CameraViewInterface
    private val mFpsCounter: FpsCounter = FpsCounter()
    private var mDialog: AlertDialog? = null

    private var isRequest = false
    private var isPreview = false
    private var isRecordYuv = false
    private var isBrightness = false
    private var isContrast = false
    private var isWB = false

    private val imageList = mutableListOf<ImageData>()
    private var selectedDoctors: ArrayList<SelectedDoctorsResponse>? = arrayListOf()

    var imageAdapter: ImageAdapter? = null

    var fullName: String = ""
    var gender: String = ""
    var age: String = ""
    var patientId: Int? = 0

    private val listener: OnMyDevConnectListener = object : OnMyDevConnectListener {
        override fun onAttachDev(device: UsbDevice) {
            Log.d("UsbCamera", "USB Device attached: ${device.deviceName}")
            // request open permission
            if (::mCameraHelper.isInitialized && !isRequest) {
                isRequest = true
                mCameraHelper.requestPermission(0)
            }
        }

        override fun onDettachDev(device: UsbDevice) {
            Log.d("UsbCamera", "USB Device detached: ${device.deviceName}")
            // close camera
            if (isRequest) {
                isRequest = false
                mCameraHelper.closeCamera()
                showShortMsg(device.deviceName + " is out")
            }
        }

        override fun onConnectDev(device: UsbDevice, isConnected: Boolean) {
            Log.d("UsbCamera", "USB Device connect status: $isConnected")
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params")
                isPreview = false
            } else {
                isPreview = true
                showShortMsg("connecting")
                // initialize seekbar
                // need to wait UVCCamera initialize over
                Thread {
                    try {
                        Thread.sleep(2500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    Looper.prepare()
                    if (::mCameraHelper.isInitialized && mCameraHelper.isCameraOpened) {
                        mBinding.seekbarBrightness.setProgress(
                            mCameraHelper.getModelValue(
                                UVCCameraHelper.MODE_BRIGHTNESS
                            )
                        )
                        mBinding.seekbarContrast.setProgress(
                            mCameraHelper.getModelValue(
                                UVCCameraHelper.MODE_CONTRAST
                            )
                        )
                    }
                    Looper.loop()
                }.start()
            }
        }

        override fun onDisConnectDev(device: UsbDevice) {
            Log.d("UsbCamera", "USB Device disconnected: ${device.deviceName}")
            showShortMsg("disconnecting")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_usb_camera)

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
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("UsbCamera", "New intent received")
        intent?.let { handleUsbIntent(it) }
    }

    private fun handleUsbIntent(intent: Intent) {
        val action = intent.action
        Log.d("UsbCamera", "Intent action: $action")

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            device?.let {
                Log.d("UsbCamera", "USB device attached via intent: ${it.deviceName}")
                // Handle the USB device attachment here if needed
            }
        }
    }

    private fun initCamera() {
        try {
            // Camera View and Helper Setup
            mUVCCameraView = mBinding.cameraView
            mUVCCameraView.setCallback(this)

            mCameraHelper = UVCCameraHelper.getInstance()
            mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener)
            mCameraHelper.setOnPreviewFrameListener(OnPreViewResultListener { nv21Yuv ->
                mFpsCounter.count()
                if (isRecordYuv) FileUtils.putFileStream(nv21Yuv, 0, nv21Yuv.size)
            })

            mCameraHelper.registerUSB() // Safe to do after full init
        } catch (e: Exception) {
            Log.e("UsbCamera", "Error initializing camera", e)
            showShortMsg("Error initializing camera: ${e.message}")
        }
    }

    private fun initListener() {
        mBinding.ivSettings.setOnClickListener {
            it.disableMultiTap()
            mBinding.llayoutContrast.visibility = View.GONE
            mBinding.llayoutBrightness.visibility = View.GONE

            if (!::mCameraHelper.isInitialized || !mCameraHelper.isCameraOpened) {
                showShortMsg("sorry,camera open failed")
                return@setOnClickListener
            }
            showResolutionListDialog()
        }

        mBinding.ivBrightness.setOnClickListener {
            it.disableMultiTap()
            mBinding.llayoutContrast.visibility = View.GONE
            if (!isBrightness) {
                isBrightness = true
                mBinding.llayoutBrightness.visibility = View.VISIBLE
            } else {
                isBrightness = false
                mBinding.llayoutBrightness.visibility = View.GONE
            }
        }

        mBinding.ivWhiteBalance.setOnClickListener {
            it.disableMultiTap()
            if (!::mCameraHelper.isInitialized || !mCameraHelper.isCameraOpened) {
                showShortMsg("sorry,camera open failed")
                return@setOnClickListener
            }
            mCameraHelper.startAutoWhiteBalance()
        }

        mBinding.ivContrast.setOnClickListener {
            it.disableMultiTap()
            mBinding.llayoutBrightness.visibility = View.GONE
            if (!isContrast) {
                isContrast = true
                mBinding.llayoutContrast.visibility = View.VISIBLE
            } else {
                isContrast = false
                mBinding.llayoutContrast.visibility = View.GONE
            }
        }

        mBinding.ivVideo.setOnClickListener {
            it.disableMultiTap()
            isRecordYuv = false
            if (!::mCameraHelper.isInitialized || !mCameraHelper.isCameraOpened) {
                showShortMsg("sorry,camera open failed")
                return@setOnClickListener
            }

            if (!mCameraHelper.isPushing) {
                val params = RecordParams()
                params.recordPath = getRecordFileName()
                params.recordDuration = 0
                params.isVoiceClose = mBinding.switchRecVoice.isChecked
                params.isSupportOverlay = true

                mCameraHelper.startPusher(params, object : OnEncodeResultListener {
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
                        if (TextUtils.isEmpty(videoPath)) {
                            return
                        }
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Video.Media.TITLE, "My Video")
                            put(MediaStore.Video.Media.DESCRIPTION, "My video description")
                            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                        }
                        val uri = contentResolver.insert(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                        if (uri != null) {
                            val outputStream = contentResolver.openOutputStream(uri)
                            outputStream?.close()
                            Toast.makeText(
                                this@UsbCameraActivity,
                                "Video saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("SavedPath", "Video saved successfully:$videoPath")
                        } else {
                            Toast.makeText(
                                this@UsbCameraActivity,
                                "Video not saved",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("SavedPath", "Video not saved")
                        }
                    }
                })
                showShortMsg("start record...")
                mBinding.switchRecVoice.isEnabled = false
            } else {
                FileUtils.releaseFile()
                mCameraHelper.stopPusher()
                showShortMsg("stop record...")
                mBinding.switchRecVoice.isEnabled = true
            }
        }

        mBinding.ivCamera.setOnClickListener {
            it.disableMultiTap()
            if (!::mCameraHelper.isInitialized || !mCameraHelper.isCameraOpened) {
                showShortMsg("sorry,camera open failed")
                return@setOnClickListener
            }

            val picPath = getImageFileName()
            mCameraHelper.capturePicture(picPath, OnCaptureListener { path ->
                if (TextUtils.isEmpty(path)) {
                    return@OnCaptureListener
                }
                Handler(mainLooper).post {
                    Toast.makeText(
                        this@UsbCameraActivity,
                        "save path:$path",
                        Toast.LENGTH_SHORT
                    ).show()
                    imageList.add(ImageData(Uri.fromFile(File(path))))
                    setImageClickAdapter(imageList)
                }
            })
        }

        mBinding.ivBack.setOnClickListener {
            it.disableMultiTap()
            finish()
        }

        mBinding.ivSave.setOnClickListener {
            it.disableMultiTap()
            val bundle = Bundle()
            bundle.putString("patient_name", fullName)
            bundle.putString("patient_age", age)
            bundle.putString("patient_gender", gender)
            bundle.putString("fromWhere", "usbCamera")
            bundle.putParcelableArrayList("imagesList", imageList as ArrayList<out Parcelable>)
            bundle.putParcelableArrayList(
                "selectedDoctors",
                selectedDoctors as ArrayList<out Parcelable>
            )
            val intent = Intent(this, PdfGenerationActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }
    }

    private fun setImageClickAdapter(imageList: MutableList<ImageData>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        mBinding.photosRecyclerview.layoutManager = layoutManager

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
            Log.e("UsbCamera", "Error setting up ItemTouchHelper", e)
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
        mBinding.seekbarBrightness.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (::mCameraHelper.isInitialized && mCameraHelper.isCameraOpened) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS, progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        mBinding.seekbarContrast.max = 100
        mBinding.seekbarContrast.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (::mCameraHelper.isInitialized && mCameraHelper.isCameraOpened) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_CONTRAST, progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onStart() {
        super.onStart()
        if (::mCameraHelper.isInitialized) {
            mCameraHelper.registerUSB()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::mCameraHelper.isInitialized) {
            mCameraHelper.unregisterUSB()
        }
    }

    private fun getRecordFileName(): String {
        return "${FileUtils.ROOT_PATH}/${MainApplication.get().DIRECTORY_NAME}/videos/${System.currentTimeMillis()}"
    }

    private fun getImageFileName(): String {
        return "${FileUtils.ROOT_PATH}/${MainApplication.get().DIRECTORY_NAME}/images/${System.currentTimeMillis()}${UVCCameraHelper.SUFFIX_JPEG}"
    }

    private fun showResolutionListDialog() {
        val builder = AlertDialog.Builder(this@UsbCameraActivity)
        val rootView: View = LayoutInflater.from(this@UsbCameraActivity)
            .inflate(R.layout.layout_dialog_list, null)
        val listView = rootView.findViewById<ListView>(R.id.listview_dialog)
        val adapter = ArrayAdapter(
            this@UsbCameraActivity,
            android.R.layout.simple_list_item_1,
            getResolutionList()
        )
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, position, _ ->
            if (!::mCameraHelper.isInitialized || !mCameraHelper.isCameraOpened) return@OnItemClickListener
            val resolution = adapterView.getItemAtPosition(position) as String
            val tmp = resolution.split("x")
            if (tmp.size >= 2) {
                val width = tmp[0].toIntOrNull() ?: 0
                val height = tmp[1].toIntOrNull() ?: 0
                if (width > 0 && height > 0) {
                    mCameraHelper.updateResolution(width, height)
                }
            }
            mDialog?.dismiss()
        }
        builder.setView(rootView)
        mDialog = builder.create()
        mDialog?.show()
    }

    private fun getResolutionList(): MutableList<String> {
        val resolutions = mutableListOf<String>()
        if (::mCameraHelper.isInitialized) {
            val list = mCameraHelper.supportedPreviewSizes
            if (list != null && list.isNotEmpty()) {
                for (size in list) {
                    size?.let {
                        resolutions.add("${it.width}x${it.height}")
                    }
                }
            }
        }
        return resolutions
    }

    override fun onDestroy() {
        super.onDestroy()
        FileUtils.releaseFile()
        if (::mCameraHelper.isInitialized) {
            mCameraHelper.release()
        }
    }

    private fun showShortMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun getUSBMonitor(): USBMonitor {
        return if (::mCameraHelper.isInitialized) {
            mCameraHelper.usbMonitor
        } else {
            throw IllegalStateException("Camera helper not initialized")
        }
    }

    override fun onDialogResult(canceled: Boolean) {
        if (canceled) {
            showShortMsg("Cancel operation")
        }
    }

    override fun onSurfaceCreated(view: CameraViewInterface?, surface: Surface?) {
        if (!isPreview && ::mCameraHelper.isInitialized && mCameraHelper.isCameraOpened) {
            mCameraHelper.startPreview(mUVCCameraView)
            isPreview = true
        }
    }

    override fun onSurfaceChanged(
        view: CameraViewInterface?,
        surface: Surface?,
        width: Int,
        height: Int
    ) {
        // Handle surface changes if needed
    }

    override fun onSurfaceDestroy(view: CameraViewInterface?, surface: Surface?) {
        if (isPreview && ::mCameraHelper.isInitialized && mCameraHelper.isCameraOpened) {
            mCameraHelper.stopPreview()
            isPreview = false
        }
    }
}