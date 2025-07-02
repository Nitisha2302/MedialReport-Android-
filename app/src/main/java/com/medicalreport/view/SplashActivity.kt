package com.medicalreport.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.medicalreport.R
import com.medicalreport.base.MainApplication
import com.medicalreport.databinding.ActivitySplashBinding
import com.medicalreport.view.auth.AuthActivity
import com.medicalreport.view.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_splash)
        MainApplication.get().setCurrentActivity(this)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }, 4000)

    }
}