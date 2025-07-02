package com.medicalreport.view.main

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

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
}