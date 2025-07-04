package com.medicalreport.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.medicalreport.R
import com.medicalreport.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private var drawerLayout: DrawerLayout? = null
    private lateinit var navView: NavigationView
    private var navController: NavController? = null
    private var configuration: Configuration? = null

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
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            handleDeniedPermissions(denied)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        checkAndRequestPermissions()
        setSupportActionBar(mBinding.toolbar)
        configuration = getResources().configuration
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment?
        navController = navHostFragment?.navController
        drawerLayout = mBinding.drawerLayout
        navView = mBinding.navView

        if (drawerLayout != null) {
            val toggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                mBinding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout?.addDrawerListener(toggle)
            toggle.syncState()
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            handleNavigation(menuItem)
            drawerLayout?.closeDrawers()
            true
        }
    }

    private fun handleNavigation(menuItem: MenuItem) {

        when (menuItem.itemId) {
            R.id.nav_home -> {
                navController?.navigate(R.id.nav_home)
                supportActionBar?.title = resources.getString(R.string.menu_home)
            }

            R.id.nav_patient -> {
                navController?.navigate(R.id.nav_patient)
                supportActionBar?.title = "Patient List"
            }

            R.id.nav_notification -> {
                navController?.navigate(R.id.nav_notification)
                supportActionBar?.title = resources.getString(R.string.menu_notifications)
            }

            R.id.nav_terms_and_condition -> {
                navController?.navigate(R.id.nav_termsCondition)
                supportActionBar?.title = resources.getString(R.string.menu_terms_and_condition)
            }

            R.id.nav_privacy_policy -> {
                navController?.navigate(R.id.nav_privacyPolicy)
                supportActionBar?.title = resources.getString(R.string.menu_privacy_policy)
            }

            R.id.nav_doctorProfile -> {
                navController?.navigate(R.id.nav_doctorProfile)
                supportActionBar?.title = resources.getString(R.string.menu_update_account)
            }

            R.id.nav_logout -> {
                navController?.navigate(R.id.nav_logout)
                supportActionBar?.title = resources.getString(R.string.menu_logout)
            }

            else -> "Unknown"
        }
    }

    private fun checkAndRequestPermissions() {
        if (!hasAllPermissions()) {
            permissionLauncher.launch(requiredPermissions.toTypedArray())
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !isAllFilesAccessGranted()) {
            showManageFilesDialog()
        } else {
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isAllFilesAccessGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else true
    }

    private fun requestAllFilesPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    private fun showManageFilesDialog() {
        AlertDialog.Builder(this,R.style.CustomAlertDialog)
            .setTitle("Allow All Files Access")
            .setMessage("This app needs permission to access all files.")
            .setCancelable(false)
            .setPositiveButton("Grant") { _, _ -> requestAllFilesPermission() }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT).show()
                showManageFilesDialog()
            }
            .show()
    }

    private fun showAppSettingsDialog() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Permission Required")
            .setMessage("Please enable permissions in App Settings to continue.")
            .setCancelable(false)
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT).show()
                showAppSettingsDialog()
            }
            .show()
    }

    private fun handleDeniedPermissions(deniedPermissions: Set<String>) {
        val permanentlyDenied = deniedPermissions.any {
            !ActivityCompat.shouldShowRequestPermissionRationale(this, it)
        }

        if (permanentlyDenied) {
            showAppSettingsDialog()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            checkAndRequestPermissions()
        }
    }
}