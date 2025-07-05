package com.medicalreport.view.auth


import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.medicalreport.R
import com.medicalreport.databinding.ActivityCustomErrorBinding
import com.medicalreport.view.main.MainActivity


class CustomErrorActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityCustomErrorBinding
    var pacakgeName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_custom_error)
        pacakgeName = applicationContext.packageName

        val config = CustomActivityOnCrash.getConfigFromIntent(intent)

        if (config == null) {
            finish()
            return
        }

        if (config.isShowRestartButton && config.restartActivityClass != null) {
            mBinding.restartButton.setText("Restart App")
            mBinding.restartButton.setOnClickListener(View.OnClickListener {
                startActivity(Intent(this@CustomErrorActivity, MainActivity::class.java))
                finish()
            })
        } else {
            mBinding.restartButton.setOnClickListener(View.OnClickListener {
                CustomActivityOnCrash.closeApplication(
                    this@CustomErrorActivity,
                    config
                )
            })
        }


        mBinding.detailButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.detail_button -> showAlert()
        }

    }

    fun showAlert() {
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle(cat.ereza.customactivityoncrash.R.string.customactivityoncrash_error_activity_error_details_title)
            .setMessage(
                CustomActivityOnCrash.getAllErrorDetailsFromIntent(
                    this@CustomErrorActivity,
                    intent
                )
            )
            .setPositiveButton(
                cat.ereza.customactivityoncrash.R.string.customactivityoncrash_error_activity_error_details_close,
                null
            )
            .setNeutralButton(cat.ereza.customactivityoncrash.R.string.customactivityoncrash_error_activity_error_details_copy,
                DialogInterface.OnClickListener { dialog, which -> copyErrorToClipboard() })
            .show()
    }


    private fun copyErrorToClipboard() {
        val errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(
            this@CustomErrorActivity,
            intent
        )
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        //Are there any devices without clipboard...?
        if (clipboard != null) {
            val clip = ClipData.newPlainText(
                getString(cat.ereza.customactivityoncrash.R.string.customactivityoncrash_error_activity_error_details_clipboard_label),
                errorInformation
            )
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                this@CustomErrorActivity,
                cat.ereza.customactivityoncrash.R.string.customactivityoncrash_error_activity_error_details_copied,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}