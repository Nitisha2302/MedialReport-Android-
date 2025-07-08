package com.medicalreport.view.main.reports

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.util.concurrent.ListenableFuture
import com.medicalreport.R
import com.medicalreport.base.clickListener.AdapterClickListener
import com.medicalreport.databinding.FragmentDeviceCameraBinding
import com.medicalreport.modal.response.ImageData
import com.medicalreport.modal.response.SelectedDoctorsResponse
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.view.adapter.ImageAdapter
import com.medicalreport.view.adapter.PhotoUploadAdapter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class DeviceCameraFragment : Fragment() {
    private lateinit var mBinding: FragmentDeviceCameraBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null
    private lateinit var currentCameraLensFacing: CameraSelector
    private val imageList = mutableListOf<ImageData>()
    private var selectedDoctors: ArrayList<SelectedDoctorsResponse>? = arrayListOf()
    var uploadPhotoPath = ArrayList<String>()
    private lateinit var imageCapture: ImageCapture
    var imageAdapter: ImageAdapter? = null
    var photoUploadAdapter: PhotoUploadAdapter? = null
    var fullName: String = ""
    var gender: String = ""
    var age: String = ""
    var patientId: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentDeviceCameraBinding.inflate(inflater, container, false)
        cameraProviderFuture = context?.let { ProcessCameraProvider.getInstance(it) }!!
        context?.let { ContextCompat.getMainExecutor(it) }?.let {
            currentCameraLensFacing = CameraSelector.DEFAULT_FRONT_CAMERA
            cameraProviderFuture.addListener(Runnable {
                cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider, currentCameraLensFacing)
            }, it)
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        fullName = arguments?.getString("patient_name").toString()
        age = arguments?.getString("patient_age").toString()
        gender = arguments?.getString("patient_gender").toString()
        selectedDoctors = arguments?.getParcelableArrayList("selectedDoctors")

        initListener()
        setupPhotoSlideAdapter()
        return mBinding.root
    }

    private fun setImageClickAdapter(imageList: MutableList<ImageData>) {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        mBinding.photosRecyclerview.setLayoutManager(layoutManager)
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
        mBinding.photosRecyclerview.setAdapter(imageAdapter)
    }

    private fun setupPhotoSlideAdapter() {
        try {
            val itemDecor =
                ItemTouchHelper((object : ItemTouchHelper.SimpleCallback(uploadPhotoPath.size, 0) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val fromPos = viewHolder.adapterPosition
                        val toPos = target.adapterPosition
                        val fromItem = uploadPhotoPath[fromPos]
                        val toItem = uploadPhotoPath[toPos]
                        uploadPhotoPath[fromPos] = toItem
                        uploadPhotoPath[toPos] = fromItem
                        photoUploadAdapter?.notifyItemMoved(fromPos, toPos)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
                } as ItemTouchHelper.Callback))
            itemDecor.attachToRecyclerView(mBinding.photosRecyclerview)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        photoUploadAdapter = PhotoUploadAdapter(uploadPhotoPath, object : AdapterClickListener {

            override fun onItemClick(view: View?, pos: Int, `object`: Any?) {
                val itemUpdated = uploadPhotoPath[pos]
                if (view?.id == R.id.ivDeletePhoto) {
                    uploadPhotoPath.remove(itemUpdated)
                    photoUploadAdapter?.notifyDataSetChanged()
                }
            }
        })
        mBinding.photosRecyclerview.setAdapter(photoUploadAdapter)
    }

    private fun initListener() {
        mBinding.recordImage.setOnClickListener {
            it.disableMultiTap()
            captureImage()
        }
        mBinding.uploadLayout.setOnClickListener {
            pickPhotoFromGallery()
        }

        mBinding.previewView.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                switchCamera()
            }
        })

        mBinding.ivCheck.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("patient_name", fullName)
            bundle.putString("patient_age", age)
            bundle.putString("patient_gender", gender)
            bundle.putString("fromWhere","deviceCamera")
            bundle.putParcelableArrayList("imagesList", imageList as ArrayList<out Parcelable>)
            bundle.putParcelableArrayList(
                "selectedDoctors",
                selectedDoctors as ArrayList<out Parcelable>
            )
            val intent = Intent(context, PdfGenerationActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    private fun switchCamera() {
        if (currentCameraLensFacing == CameraSelector.DEFAULT_FRONT_CAMERA) {
            currentCameraLensFacing = CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            currentCameraLensFacing = CameraSelector.DEFAULT_FRONT_CAMERA
        }

        cameraProviderFuture = context?.let { ProcessCameraProvider.getInstance(it) }!!
        context?.let { ContextCompat.getMainExecutor(it) }?.let {
            cameraProviderFuture.addListener(Runnable {
                cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
                bindPreview(cameraProvider, currentCameraLensFacing)
            }, it)
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    abstract class DoubleClickListener : View.OnClickListener {
        var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
            }
            lastClickTime = clickTime
        }

        abstract fun onDoubleClick(v: View?)

        companion object {
            private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent()
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setAction(Intent.ACTION_GET_CONTENT)
        resultCallbackForGallery.launch(intent)
    }


    var resultCallbackForGallery: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                result.data?.clipData?.let { clipData ->
                    for (i in 0 until clipData.itemCount) {
                        clipData.getItemAt(i).uri.let { uri ->
                            uploadPhotoPath.add(uri.toString())
                            photoUploadAdapter?.notifyDataSetChanged()

                        }
                    }
                }
            }
            val uris = uploadPhotoPath.distinctBy { it }

            handlePhotoUris(uris)
        }
    }

    private fun handlePhotoUris(it: List<String>) {

        Log.d("photoPicking", "handle uris size: ${it.size},${it}")

    }

    private fun captureImage() {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }


        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        requireContext(),
                        "Image saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    outputFileResults.savedUri?.let { ImageData(it) }?.let { imageList.add(it) }
                    setImageClickAdapter(imageList)

                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Image capture failed", "${exception.message}")
                    Toast.makeText(
                        requireContext(),
                        "Unable to capture Image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }


    private fun bindPreview(
        cameraProvider: ProcessCameraProvider?, cameraSelector: CameraSelector
    ) {
        val preview = Preview.Builder().build()
        val previewView = mBinding.previewView
        preview.setSurfaceProvider(previewView.surfaceProvider)
        imageCapture = ImageCapture.Builder().build()
        camera = cameraProvider?.bindToLifecycle(
            this as LifecycleOwner, cameraSelector, imageCapture, preview
        )

    }

}