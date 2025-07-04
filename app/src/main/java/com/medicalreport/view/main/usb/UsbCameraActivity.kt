package com.medicalreport.view.main.usb

import android.content.ContentValues
import android.content.Intent
import android.hardware.usb.UsbDevice
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
import com.serenegiant.usb.encoder.RecordParams
import com.serenegiant.usb.widget.CameraViewInterface
import java.io.File

class UsbCameraActivity : AppCompatActivity(),CameraDialogParent, CameraViewInterface.Callback{
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


    private val listener: UVCCameraHelper.OnMyDevConnectListener = object :
        UVCCameraHelper.OnMyDevConnectListener {
        override fun onAttachDev(device: UsbDevice) {
            // request open permission
            if (!isRequest) {
                isRequest = true
                mCameraHelper.requestPermission(0)
            }
        }

        override fun onDettachDev(device: UsbDevice) {
            // close camera
            if (isRequest) {
                isRequest = false
                mCameraHelper.closeCamera()
                showShortMsg(device.deviceName + " is out")
            }
        }

        override fun onConnectDev(device: UsbDevice, isConnected: Boolean) {
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
                    if (mCameraHelper != null && mCameraHelper.isCameraOpened) {
                        mBinding.seekbarBrightness?.setProgress(
                            mCameraHelper.getModelValue(
                                UVCCameraHelper.MODE_BRIGHTNESS
                            )
                        )
                        mBinding.seekbarContrast?.setProgress(
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
            showShortMsg("disconnecting")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_usb_camera)
        val bundle = intent.extras
        fullName = bundle?.getString("patient_name").toString()
        age = bundle?.getString("patient_age").toString()
        gender = bundle?.getString("patient_gender").toString()
        selectedDoctors = bundle?.getParcelableArrayList("selectedDoctors")
        initView()
        // step.1 initialize UVCCameraHelper

        mUVCCameraView = mBinding.cameraView as CameraViewInterface
        mUVCCameraView.setCallback(this)
        mCameraHelper = UVCCameraHelper.getInstance()
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener)

        mCameraHelper.setOnPreviewFrameListener(AbstractUVCCameraHandler.OnPreViewResultListener { nv21Yuv ->
            Log.d(
                "CameraPreviewResult",
                "onPreviewResult: " + nv21Yuv.size
            )
            mFpsCounter.count()
            if (isRecordYuv) {
                FileUtils.putFileStream(nv21Yuv, 0, nv21Yuv.size)
            }

        })

        initListener()

    }

    private fun initListener() {
        mBinding.ivSettings?.setOnClickListener {
            it.disableMultiTap()
            mBinding.llayoutContrast?.visibility = View.GONE
            mBinding.llayoutBrightness?.visibility = View.GONE

            if (mCameraHelper == null || !mCameraHelper.isCameraOpened) {
                showShortMsg("sorry,camera open failed")
            }
            showResolutionListDialog()
        }
        mBinding.ivBrightness?.setOnClickListener {
            it.disableMultiTap()
            mBinding.llayoutContrast?.visibility = View.GONE
            if (!isBrightness) {
                isBrightness = true
                mBinding.llayoutBrightness?.visibility = View.VISIBLE
            } else {
                isBrightness = false
                mBinding.llayoutBrightness?.visibility = View.GONE
            }
        }

        mBinding.ivWhiteBalance?.setOnClickListener {
            it.disableMultiTap()
            if (mCameraHelper == null || !mCameraHelper.isCameraOpened) {
                showShortMsg("sorry,camera open failed")
            }
            mCameraHelper.startAutoWhiteBalance()


        }
        mBinding.ivContrast?.setOnClickListener {
            it.disableMultiTap()
            mBinding.llayoutBrightness?.visibility = View.GONE
            if (!isContrast) {
                isContrast = true
                mBinding.llayoutContrast?.visibility = View.VISIBLE
            } else {
                isContrast = false
                mBinding.llayoutContrast?.visibility = View.GONE

            }
        }

        mBinding.ivVideo?.setOnClickListener {
            it.disableMultiTap()
            isRecordYuv = false
            if (mCameraHelper == null || !mCameraHelper.isCameraOpened) {
                showShortMsg("sorry,camera open failed")
            }

            if (!mCameraHelper.isPushing) {
                val params = RecordParams()
                params.recordPath = getRecordFileName()
                params.recordDuration = 0 // auto divide saved,default 0 means not divided
                params.isVoiceClose = mBinding.switchRecVoice?.isChecked == true // is close voice
                params.isSupportOverlay = true // overlay only support armeabi-v7a & arm64-v8a
                mCameraHelper.startPusher(params, object :
                    AbstractUVCCameraHandler.OnEncodeResultListener {
                    override fun onEncodeResult(
                        data: ByteArray,
                        offset: Int,
                        length: Int,
                        timestamp: Long,
                        type: Int
                    ) {
                        // type = 1,h264 video stream
                        if (type == 1) {
                            FileUtils.putFileStream(data, offset, length)
                        }
                        // type = 0,aac audio stream
                        if (type == 0) {
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
                            // Write the video data to the output stream
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
                // if you only want to push stream,please call like this
//                 mCameraHelper.startPusher(listener)
                showShortMsg("start record...")
                mBinding.switchRecVoice?.setEnabled(false)
            } else {
                FileUtils.releaseFile()
                mCameraHelper.stopPusher()
                showShortMsg("stop record...")
                mBinding.switchRecVoice?.setEnabled(true)
            }
        }

        mBinding.ivCamera?.setOnClickListener {
            it.disableMultiTap()
            if (mCameraHelper == null || !mCameraHelper.isCameraOpened) {
                showShortMsg("sorry,camera open failed")
            }

            val picPath = getImageFileName()

            mCameraHelper.capturePicture(picPath,
                AbstractUVCCameraHandler.OnCaptureListener { path ->
                    if (TextUtils.isEmpty(path)) {
                        return@OnCaptureListener
                    }
                    Handler(mainLooper).post(Runnable {
                        Toast.makeText(
                            this@UsbCameraActivity,
                            "save path:$path",
                            Toast.LENGTH_SHORT
                        ).show()
                        imageList.add(ImageData(Uri.fromFile(File(path))))
                        setImageClickAdapter(imageList)

                    })

                })
        }

        mBinding.ivBack?.setOnClickListener {
            it.disableMultiTap()
            finish()
        }

        mBinding.ivSave?.setOnClickListener {
            it.disableMultiTap()
            var bundle = Bundle()
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
        mBinding.photosRecyclerview?.setLayoutManager(layoutManager)
        try {
            val itemDecor =
                ItemTouchHelper((object : ItemTouchHelper.SimpleCallback(imageList.size, 0) {
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
                } as ItemTouchHelper.Callback))
            itemDecor.attachToRecyclerView(mBinding.photosRecyclerview)

        } catch (e: Exception) {
            e.printStackTrace()
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
        mBinding.photosRecyclerview?.setAdapter(imageAdapter)

    }

    private fun initView() {
        mBinding.seekbarBrightness?.max = 100
        mBinding.seekbarBrightness?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mCameraHelper != null && mCameraHelper.isCameraOpened) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS, progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        mBinding.seekbarContrast?.max = 100
        mBinding.seekbarContrast?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mCameraHelper != null && mCameraHelper.isCameraOpened) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_CONTRAST, progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

    }

    override fun onStart() {
        super.onStart()
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB()
        }
    }

    override fun onStop() {
        super.onStop()
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB()
        }
    }


    override fun onOptionsItemSelected(item: android.view.MenuItem): kotlin.Boolean {
        when (item.itemId) {
            R.id.menu_takepic -> {
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened) {
                    showShortMsg("sorry,camera open failed")
                    return super.onOptionsItemSelected(item)
                }
                val picPath =
                    ((FileUtils.ROOT_PATH + "/" + MainApplication.get().DIRECTORY_NAME).toString() + "/images/"
                            + System.currentTimeMillis() + UVCCameraHelper.SUFFIX_JPEG)
                mCameraHelper.capturePicture(picPath, object :
                    AbstractUVCCameraHandler.OnCaptureListener {
                    override fun onCaptureResult(path: String) {
                        if (TextUtils.isEmpty(path)) {
                            return
                        }
                        android.os.Handler(mainLooper).post {
                            Toast.makeText(
                                this@UsbCameraActivity,
                                "save path:$path", android.widget.Toast.LENGTH_SHORT
                            ).show()
                            Log.e("SavedPath", "save path:$path")
                        }
                    }
                })

            }

            R.id.menu_recording -> {
                isRecordYuv = false
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened) {
                    showShortMsg("sorry,camera open failed")
                    return super.onOptionsItemSelected(item)
                }

                if (!mCameraHelper.isPushing) {
                    // FileUtils.createfile(FileUtils.ROOT_PATH + "test666.h264");
                    // if you want to record,please create RecordParams like this
                    val params = RecordParams()
                    params.recordPath = getRecordFileName()
                    params.recordDuration = 0 // auto divide saved,default 0 means not divided
                    params.isVoiceClose =
                        mBinding.switchRecVoice?.isChecked == true // is close voice
                    params.isSupportOverlay = true // overlay only support armeabi-v7a & arm64-v8a
                    /* mCameraHelper.startPusher(params, object : OnEncodeResultListener {
                         override fun onEncodeResult(
                             data: ByteArray,
                             offset: Int,
                             length: Int,
                             timestamp: Long,
                             type: Int
                         ) {
                             // type = 1,h264 video stream
                             if (type == 1) {
                                 FileUtils.putFileStream(data, offset, length)
                             }
                             // type = 0,aac audio stream
                             if (type == 0) {
                             }
                         }

                         override fun onRecordResult(videoPath: String) {
                             if (TextUtils.isEmpty(videoPath)) {
                                 return
                             }
                             android.os.Handler(mainLooper).post {
                                 Toast.makeText(
                                     this@UsbCameraActivity,
                                     "save videoPath:$videoPath",
                                     Toast.LENGTH_SHORT
                                 ).show()
                                 Log.e("SavedPath", "save path:$videoPath")

                             }
                         }
                     })
                     // if you only want to push stream,please call like this
                     // mCameraHelper.startPusher(listener);
                     showShortMsg("start record...")
                     mBinding.switchRecVoice.setEnabled(false)
                 } else {
                     FileUtils.releaseFile()
                     mCameraHelper.stopPusher()
                     showShortMsg("stop record...")
                     mBinding.switchRecVoice.setEnabled(true)
                 }*/
                    mCameraHelper.startPusher(params, object :
                        AbstractUVCCameraHandler.OnEncodeResultListener {
                        override fun onEncodeResult(
                            data: ByteArray?,
                            offset: Int,
                            length: Int,
                            timestamp: Long,
                            type: Int
                        ) {
                            // type = 1,h264 video stream
                            if (type == 1) {
                                FileUtils.putFileStream(data, offset, length);
                            }
                            // type = 0,aac audio stream
                            if (type == 0) {

                            }
                        }

                        override fun onRecordResult(videoPath: String?) {
                            if (TextUtils.isEmpty(videoPath)) {
                                return;
                            }
                            android.os.Handler(mainLooper).post {
                                Toast.makeText(
                                    this@UsbCameraActivity,
                                    "save videoPath:$videoPath",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("SavedPath", "save path:$videoPath")

                            }
                        }
                    });
                    // if you only want to push stream,please call like this
                    // mCameraHelper.startPusher(listener);
                    showShortMsg("start record...");
                    mBinding.switchRecVoice?.setEnabled(false);
                } else {
                    FileUtils.releaseFile();
                    mCameraHelper.stopPusher();
                    showShortMsg("stop record...");
                    mBinding.switchRecVoice?.setEnabled(true);
                }


            }

            R.id.menu_resolution -> {
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened) {
                    showShortMsg("sorry,camera open failed")
                    return super.onOptionsItemSelected(item)
                }
                showResolutionListDialog()
            }

            R.id.menu_focus -> {
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened) {
                    showShortMsg("sorry,camera open failed")
                    return super.onOptionsItemSelected(item)
                }
                mCameraHelper.startCameraFoucs()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getRecordFileName(): String? {
        return (FileUtils.ROOT_PATH + "/" + MainApplication.get().DIRECTORY_NAME + "/videos/"
                + System.currentTimeMillis())
//        + UVCCameraHelper.SUFFIX_MP4
    }

    private fun getImageFileName(): String {
        return (FileUtils.ROOT_PATH + "/" + MainApplication.get().DIRECTORY_NAME + "/images/"
                + System.currentTimeMillis() + UVCCameraHelper.SUFFIX_JPEG)
    }


    private fun showResolutionListDialog() {
        val builder = AlertDialog.Builder(this@UsbCameraActivity)
        val rootView: View =
            LayoutInflater.from(this@UsbCameraActivity).inflate(R.layout.layout_dialog_list, null)
        val listView = rootView.findViewById<View>(R.id.listview_dialog) as ListView
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this@UsbCameraActivity,
            android.R.layout.simple_list_item_1,
            getResolutionList()
        )
        if (adapter != null) {
            listView.adapter = adapter
        }
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, id ->
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened) return@OnItemClickListener
                val resolution = adapterView.getItemAtPosition(position) as String
                val tmp = resolution.split("x".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (tmp != null && tmp.size >= 2) {
                    val widht = Integer.valueOf(tmp[0])
                    val height = Integer.valueOf(tmp[1])
                    mCameraHelper.updateResolution(widht, height)
                }
                mDialog!!.dismiss()
            }
        builder.setView(rootView)
        mDialog = builder.create()
        mDialog?.show()
    }

    private fun getResolutionList(): MutableList<String> {
        val list = mCameraHelper.supportedPreviewSizes
        var resolutions: MutableList<String> = mutableListOf()
        if (list != null && list.size != 0) {
            resolutions = ArrayList()
            for (size in list) {
                if (size != null) {
                    resolutions.add(size.width.toString() + "x" + size.height)
                }
            }
        }
        return resolutions
    }

    override fun onDestroy() {
        super.onDestroy()
        // step.4 release uvc camera resources
        FileUtils.releaseFile()
        if (mCameraHelper != null) {
            mCameraHelper.release()
        }

    }

    private fun showShortMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun getUSBMonitor(): USBMonitor {
        return mCameraHelper.usbMonitor
    }

    override fun onDialogResult(canceled: Boolean) {
        if (canceled) {
            showShortMsg("Cancel operation")
        }
    }


    override fun onSurfaceCreated(
        view: CameraViewInterface?,
        surface: Surface?
    ) {
        if (!isPreview && mCameraHelper.isCameraOpened) {
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
    }

    override fun onSurfaceDestroy(
        view: CameraViewInterface?,
        surface: Surface?
    ) {
        if (isPreview && mCameraHelper.isCameraOpened) {
            mCameraHelper.stopPreview()
            isPreview = false
        }
    }
}
