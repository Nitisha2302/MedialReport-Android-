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
import com.medicalreport.utils.Prefs
import com.medicalreport.view.auth.AuthActivity
import com.medicalreport.view.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_splash)
        MainApplication.get().setCurrentActivity(this)
    }

    override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).postDelayed({
            if (!Prefs.init().email.isEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }

        }, 4000)
    }
}
