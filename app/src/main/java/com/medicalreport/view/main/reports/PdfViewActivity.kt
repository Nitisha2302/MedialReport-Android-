package com.medicalreport.view.main.reports

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.medicalreport.R
import com.medicalreport.databinding.ActivityPdfViewBinding
import java.io.File

class PdfViewActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityPdfViewBinding
    private var downloadDir: String = ""
    private var fileName: String = ""
    private var pdfPath: String = ""
    private var pdfFile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_view)
        val bundle = intent.extras
        try {
            downloadDir = bundle?.getString("downloadsDir").toString()
            fileName = bundle?.getString("fileName").toString()
            pdfPath = bundle?.getString("pdfPath").toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (pdfPath.isNotEmpty()) {
            pdfFile = pdfPath
        } else {
            pdfFile = downloadDir + fileName
        }

        Log.e("PdfFile", "${pdfFile}")
        if (pdfFile.isEmpty()) {
            mBinding.pdfView.visibility = View.GONE
            mBinding.tvNoDataFound.visibility = View.VISIBLE
        } else {
            mBinding.pdfView.visibility = View.VISIBLE
            mBinding.tvNoDataFound.visibility = View.GONE
            try {
                if (pdfPath.isNotEmpty() && pdfPath.startsWith("http")||pdfPath.startsWith("https")) {
                    downloadAndOpenPdf(pdfPath)
                } else {
                    openPdf(File(pdfFile))
                }
            } catch (e: Exception) {
                mBinding.pdfView.visibility = View.GONE
                mBinding.tvNoDataFound.visibility = View.VISIBLE
                e.printStackTrace()
            }
        }
        initListener()
    }

    private fun downloadAndOpenPdf(url: String) {
        val fileName = url.substringAfterLast("/")
        val file = File(cacheDir, fileName)

        if (file.exists()) {
            openPdf(file)
            return
        }

        Thread {
            try {
                val input = java.net.URL(url).openStream()
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
                runOnUiThread { openPdf(file) }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    mBinding.pdfView.visibility = View.GONE
                    mBinding.tvNoDataFound.visibility = View.VISIBLE
                }
            }
        }.start()
    }
    private fun openPdf(file: File) {
        mBinding.pdfView.visibility = View.VISIBLE
        mBinding.tvNoDataFound.visibility = View.GONE
        mBinding.pdfView.initWithFile(file)
    }
    private fun initListener() {
        mBinding.ivBack.setOnClickListener {
            finish()
        }
        mBinding.ivHome.setOnClickListener {
            finish()
        }
    }
}