package com.medicalreport.view.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.medicalreport.R
import com.medicalreport.base.BaseFragment
import com.medicalreport.bottomSheet.BottomSheetDialog
import com.medicalreport.bottomSheet.DevicesBottomSheetDialog
import com.medicalreport.databinding.FragmentDoctorUpdateProfileBinding
import com.medicalreport.utils.EdTextWatcher
import com.medicalreport.utils.Prefs
import com.medicalreport.utils.Util.checkIfHasNetwork
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.utils.getCircleImageFromFresco
import com.medicalreport.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DoctorUpdateProfileFragment : BaseFragment<FragmentDoctorUpdateProfileBinding>(),
    BottomSheetDialog.OnBottomItemClickListener,
    DevicesBottomSheetDialog.OnDeviceBottomItemClickListener{
    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var mBinding: FragmentDoctorUpdateProfileBinding
    private val bottomSheetDialog = BottomSheetDialog()
    private val deviceBottomSheetDialog = DevicesBottomSheetDialog()
    private var gender: String? = null
    private var fullName: String = ""
    private var address: String = ""
    private var username: String = ""
    private var phoneNumber: String = ""
    private var specialized: String = ""
    private var bio: String = ""
    private var hospitalName: String = ""
    private var hospitalAddress: String = ""
    private var hospitalPhoneNumber: String = ""
    private var profileImage = ""

    private val requiredPermissions = mutableListOf(
        Manifest.permission.CAMERA
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val denied = permissions.filterValues { !it }.keys
        if (denied.isEmpty() && isAllFilesAccessGranted()) {
            Toast.makeText(activity, "All permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            handleDeniedPermissions(denied)
        }
    }

    override val fragmentLayoutId: Int
        get() = R.layout.fragment_doctor_update_profile

    override fun setUpUi(binding: FragmentDoctorUpdateProfileBinding) {
        mBinding = binding
        initGender()
        initGetDoctorProfile()
        initView()
        initListener()
    }

    private fun initGender() {
        val genderList = arrayListOf("Male", "Female")
        val genderAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderList)
        mBinding.etGender.setAdapter(genderAdapter)
        mBinding.etGender.setOnClickListener {
            mBinding.etGender.showDropDown()
        }
        mBinding.etGender.setOnItemClickListener { parent, _, position, _ ->
            gender = parent.getItemAtPosition(position).toString()
        }
    }

    private fun initView() {
        mBinding.etFullName.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                fullName = mBinding.etFullName.text.toString()
            }
        })
        mBinding.etAddress.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                address = mBinding.etAddress.text.toString()
            }
        })
        mBinding.etUserName.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                username = mBinding.etUserName.text.toString()
            }
        })
        mBinding.etPhoneNumber.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                phoneNumber = mBinding.etPhoneNumber.text.toString()
            }
        })
        mBinding.etSpecialized.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                specialized = mBinding.etSpecialized.text.toString()
            }
        })
        mBinding.etBio.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                bio = mBinding.etBio.text.toString()
            }
        })

        mBinding.etHospitalName.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                hospitalName = mBinding.etHospitalName.text.toString()
            }
        })

        mBinding.etHospitalAddress.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                hospitalAddress = mBinding.etHospitalAddress.text.toString()
            }
        })
        mBinding.etHospitalPhoneNumber.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                hospitalPhoneNumber = mBinding.etHospitalPhoneNumber.text.toString()
            }
        })


    }

    private fun checkValidations(): Boolean {
        if (TextUtils.isEmpty(fullName)) {
            mBinding.etFullName.error = resources.getString(R.string.error_enter_fullName)
            return false
        } else if (TextUtils.isEmpty(phoneNumber)) {
            mBinding.etPhoneNumber.error = resources.getString(R.string.error_enter_phone_number)
            return false
        } else if (phoneNumber.length < 10) {
            mBinding.etPhoneNumber.error = resources.getString(R.string.error_enter_valid_number)
            return false
        } else if (TextUtils.isEmpty(hospitalName)) {
            mBinding.etHospitalName.error = resources.getString(R.string.error_enter_hospital_name)
            return false
        } else if (TextUtils.isEmpty(hospitalAddress)) {
            mBinding.etHospitalAddress.error =
                resources.getString(R.string.error_enter_hospital_address)
            return false
        } else if (TextUtils.isEmpty(hospitalPhoneNumber)) {
            mBinding.etHospitalPhoneNumber.error =
                resources.getString(R.string.error_enter_hospital_number)
            return false
        } else if (hospitalPhoneNumber.length < 10) {
            mBinding.etHospitalPhoneNumber.error =
                resources.getString(R.string.error_enter_valid_hospital_number)
            return false
        } else if (TextUtils.isEmpty(gender)) {
            mBinding.etGender.error = resources.getString(R.string.error_select_gender)
            return false
        } else if (TextUtils.isEmpty(specialized)) {
            mBinding.etSpecialized.error = resources.getString(R.string.error_enter_specialized_in)
            return false
        }
        return true
    }

    private fun initListener() {
        mBinding.ivUserImage.setOnClickListener {
            checkAndRequestPermissions()

        }
        mBinding.btnUpdate.setOnClickListener {
            it.disableMultiTap()
            if (checkIfHasNetwork()) {
                if (checkValidations()) {
                    viewModel.updateDoctorProfile(
                        gender.toString(),
                        fullName,
                        address,
                        username,
                        phoneNumber,
                        specialized,
                        bio,
                        hospitalName,
                        hospitalAddress,
                        hospitalPhoneNumber
                    ) {}
                }
            } else {
                showNoInternetAlert()
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (!hasAllPermissions()) {
            permissionLauncher.launch(requiredPermissions.toTypedArray())
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !isAllFilesAccessGranted()) {
            showManageFilesDialog()
        } else {
            val usbManager = context?.getSystemService(Context.USB_SERVICE) as UsbManager
            val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
            showDevicesDialog(deviceList.values)
        }
    }

    private fun showDevicesDialog(deviceList: MutableCollection<UsbDevice>) {
        if (deviceList.isNullOrEmpty()) {
            Toast.makeText(context, "No USB devices found", Toast.LENGTH_SHORT).show()
            bottomSheetDialog.setActionListener(this@DoctorUpdateProfileFragment)
            bottomSheetDialog.show(childFragmentManager, this::class.java.simpleName)
        } else {
            var deviceName = ""
            deviceList.forEach {
                deviceName = it.deviceName
            }
            deviceBottomSheetDialog.setDeviceName(deviceName)
            deviceBottomSheetDialog.setDeviceActionListener(this)
            deviceBottomSheetDialog.show(childFragmentManager, this::class.java.simpleName)
        }
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isAllFilesAccessGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else true
    }

    private fun requestAllFilesPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:${context?.packageName}")
        startActivity(intent)
    }

    private fun showManageFilesDialog() {
        AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog)
            .setTitle("Allow All Files Access")
            .setMessage("This app needs permission to access all files.")
            .setCancelable(false)
            .setPositiveButton("Grant") { _, _ -> requestAllFilesPermission() }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(context, "Permission required", Toast.LENGTH_SHORT).show()
                showManageFilesDialog()
            }
            .show()
    }

    private fun showAppSettingsDialog() {
        AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setTitle("Permission Required")
            .setMessage("Please enable permissions in App Settings to continue.")
            .setCancelable(false)
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${context?.packageName}")
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(context, "Permission required", Toast.LENGTH_SHORT).show()
                showAppSettingsDialog()
            }
            .show()
    }

    private fun handleDeniedPermissions(deniedPermissions: Set<String>) {
        val permanentlyDenied = deniedPermissions.any {
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }

        if (permanentlyDenied) {
            showAppSettingsDialog()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            checkAndRequestPermissions()
        }
    }
    private fun initGetDoctorProfile() {
        if (checkIfHasNetwork()) {
            viewModel.getDocProfile() {

            }
        } else {
            showNoInternetAlert()
        }
    }

    override fun setupObservers() {
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it == true) {
                showProgressDialog()
            } else {
                hideProgress()
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (!TextUtils.isEmpty(it)) {
                showSuccessAlert(it)
            }
        }
        viewModel.errorToastMessage.observe(viewLifecycleOwner) {
            if (!TextUtils.isEmpty(it)) {
                showErrorAlert(it)
            }
        }

        viewModel.doctorProfile.observe(viewLifecycleOwner) {
            if (!it?.name.isNullOrEmpty()) {
                mBinding.etFullName.setText(it.name)
            }
            if (!it?.username.isNullOrEmpty()) {
                mBinding.etUserName.setText(it.username)
            }
            if (!it?.phone.isNullOrEmpty()) {
                mBinding.etPhoneNumber.setText(it.phone)
            }
            if (!it?.hospitalName.isNullOrEmpty()) {
                mBinding.etHospitalName.setText(it.hospitalName)
                Prefs.init().hospitalName = it.hospitalName.toString()
            }
            if (!it?.hospitalAddress.isNullOrEmpty()) {
                mBinding.etHospitalAddress.setText(it.hospitalAddress)
                Prefs.init().hospitalAddress = it.hospitalAddress.toString()

            }
            if (!it?.hospitalPhone.isNullOrEmpty()) {
                mBinding.etHospitalPhoneNumber.setText(it.hospitalPhone)
                Prefs.init().hospitalPhoneNumber = it.hospitalPhone.toString()

            }
            if (!it?.address.isNullOrEmpty()) {
                mBinding.etAddress.setText(it.address)
            }
            if (!it?.gender.isNullOrEmpty()) {
                mBinding.etGender.setText(it.gender, false)
            }
            if (!it?.specialized.isNullOrEmpty()) {
                mBinding.etSpecialized.setText(it.specialized)
            }
            if (!it?.bio.isNullOrEmpty()) {
                mBinding.etBio.setText(it.bio)
            }

            getCircleImageFromFresco(mBinding.ivUserImage, it?.image, it?.gender.toString())

        }
    }

    override fun onCameraClick() {
        activity?.let { ImagePicker.with(it) }
            ?.cameraOnly()?.compress(1024)?.cropSquare()?.createIntent { intent ->
            startForProfileImageResult.launch(intent)
        }
    }

    override fun onGalleryClick() {
        activity?.let { ImagePicker.with(it) }
            ?.galleryOnly()?.compress(1024)?.cropSquare()?.createIntent { intent ->
            startForProfileImageResult.launch(intent)
        }
    }

    override fun onCancelBottomSheet() {
        bottomSheetDialog.dismiss()

    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                val fileUri = data.data
                fileUri?.let {
                    mBinding.ivUserImage.setImageURI(it)

                    val filePath = FileUriUtils.getRealPath(requireContext(), it)
                    filePath?.let { path ->
                        viewModel.doctorUpdateRequest.get()?.image = File(path).absolutePath
                        profileImage = File(path).absolutePath
                    } ?: run {
                        Toast.makeText(requireContext(), "Unable to get file path", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Image selection failed (code: $resultCode)", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onDeviceClick() {

    }

    override fun onDeviceCameraClick() {
        activity?.let { ImagePicker.with(it) }
            ?.cameraOnly()?.compress(1024)?.cropSquare()?.createIntent { intent ->
            startForProfileImageResult.launch(intent)
        }
    }

    override fun onDeviceCancelBottomSheet() {
        deviceBottomSheetDialog.dismiss()

    }


}