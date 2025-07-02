package com.medicalreport.view.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.medicalreport.R
import com.medicalreport.base.MainApplication
import com.medicalreport.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAuthBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        initView()
    }

    private fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.authHostFragment) as NavHostFragment?
        navController = navHostFragment?.navController
        try {
            val value = intent.extras?.getString("navigate")
            if (value == "navigate") {
                navController?.navigate(R.id.loginFragment)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}