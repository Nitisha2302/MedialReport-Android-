package com.medicalreport.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.StrictMode
import com.facebook.drawee.backends.pipeline.Fresco
import com.medicalreport.module.apiModule
import com.medicalreport.module.dataSourceModule
import com.medicalreport.module.networkModule
import com.medicalreport.module.providerModule
import com.medicalreport.module.repoModule
import com.medicalreport.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication: Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: MainApplication
        fun get(): MainApplication = instance

    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    private var mCurrentActivity: Activity? = null

    fun getCurrentActivity(): Activity? {
        return mCurrentActivity
    }

    fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.mCurrentActivity = mCurrentActivity
    }

    val DIRECTORY_NAME = "USBCamera"


    override fun onCreate() {
        super.onCreate()
        instance = this
        setUpModule()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        Fresco.initialize(this)
    }


    private fun setUpModule() {
        startKoin {
            androidContext(this@MainApplication)
            modules(
                networkModule,
                repoModule,
                dataSourceModule,
                networkModule,
                apiModule,
                viewModelModule,
                providerModule
            )
        }
    }


}