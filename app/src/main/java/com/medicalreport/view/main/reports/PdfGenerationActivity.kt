package com.medicalreport.view.main.reports

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medicalreport.R
import com.medicalreport.databinding.ActivityPdfGenerationBinding
import com.medicalreport.modal.response.ImageData
import com.medicalreport.modal.response.SelectedDoctorsResponse
import com.medicalreport.utils.*
import com.medicalreport.view.adapter.ReportCreationAdapter
import com.medicalreport.view.main.MainActivity
import com.medicalreport.view.main.dialog.SignatureDialogFragment
import com.medicalreport.viewmodel.PatientViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerationActivity : AppCompatActivity(), SignatureDialogFragment.ClickListener {
    private val viewModel by viewModel<PatientViewModel>()
    private lateinit var mBinding: ActivityPdfGenerationBinding
    private var imageAdapter: ReportCreationAdapter? = null

    private var fullName = ""
    private var gender = ""
    private var age = ""
    private var patientId: Int? = 0
    private var imageDataList: ArrayList<ImageData>? = arrayListOf()
    private var selectedDoctors: ArrayList<SelectedDoctorsResponse>? = arrayListOf()
    private var bitmap: Bitmap? = null
    private var downloadsDir: String = ""
    private var fileName: String = ""
    private var content: String = ""
    private var fromWhere: String = ""
    private var currentDate: String? = ""
    private var ivSignature: ImageView? = null
    private val signatureDialogFragment = SignatureDialogFragment()
    private val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_generation)

        ivSignature = findViewById(R.id.ivDSignature)
        mBinding.toolbar.visibility = View.VISIBLE
        mBinding.prefs = Prefs()
        currentDate = getCurrentDate("dd-MM-yyyy")
        mBinding.etDate.setText(currentDate)
        initListeners()

        try {
            val bundle = intent.extras
            imageDataList = bundle?.getParcelableArrayList("imagesList")
            fullName = bundle?.getString("patient_name").toString()
            age = bundle?.getString("patient_age").toString()
            gender = bundle?.getString("patient_gender").toString()
            fromWhere = bundle?.getString("fromWhere").toString()
            selectedDoctors = bundle?.getParcelableArrayList("selectedDoctors")
            imageDataList?.let { setImageClickAdapter(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initView()
    }

    private fun initView() {
        mBinding.etPatientName.text = fullName
        mBinding.etPatientAge.text = "$age/$gender"
        selectedDoctors?.forEach {
            mBinding.tvDoctorName.text = it.name
            mBinding.tvDoctorQuali.text = it.specialty
        }

        mBinding.etReport.addTextChangedListener(object : EdTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                content = mBinding.etReport.text.toString()
            }
        })
    }

    private fun initListeners() {
        mBinding.ivSave.setOnClickListener {
            it.disableMultiTap()
            bitmap = createA4BitmapFromView(mBinding.clLayout)
            bitmap?.let { generatePdfReport(it) }
        }

        mBinding.ivBack.setOnClickListener {
            it.disableMultiTap()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        mBinding.clSignature.setOnClickListener {
            it.disableMultiTap()
            signatureDialogFragment.setOnSignatureCallback(this)
            signatureDialogFragment.show(supportFragmentManager, "signature_dialog")
        }
    }

    private fun setImageClickAdapter(imageList: MutableList<ImageData>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        mBinding.rvImagesList.layoutManager = layoutManager
        imageAdapter = ReportCreationAdapter(imageList)
        mBinding.rvImagesList.adapter = imageAdapter
    }

    private fun createA4BitmapFromView(view: View): Bitmap {
        val A4_WIDTH_PX = 2480
        val A4_HEIGHT_PX = 3508

        view.measure(
            View.MeasureSpec.makeMeasureSpec(A4_WIDTH_PX, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, A4_WIDTH_PX, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(A4_WIDTH_PX, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        view.draw(canvas)

        return bitmap
    }

    private fun generatePdfReport(bitmap: Bitmap) {
        try {
            val document = PdfDocument()

            val pageInfo = PdfDocument.PageInfo.Builder(
                bitmap.width,
                bitmap.height,
                1
            ).create()

            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                isAntiAlias = true
                isDither = true
                isFilterBitmap = true
            }

            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            document.finishPage(page)
            savePdfDocument(document)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun savePdfDocument(document: PdfDocument) {
        try {
            val currentTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toString() + "/SIMS/"

            val folder = File(downloadsDir)
            if (!folder.exists()) {
                folder.mkdirs()
            }

            fileName = "${fullName}_PDF_${currentTime}.pdf"
            val file = File(downloadsDir, fileName)

            val fos = FileOutputStream(file)
            document.writeTo(fos)
            fos.close()
            document.close()

            Toast.makeText(this, "A4 PDF Created Successfully...", Toast.LENGTH_SHORT).show()
            viewPdfFile(downloadsDir, fileName)

        } catch (e: IOException) {
            Toast.makeText(this, "IO Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun viewPdfFile(downloadsDir: String, fileName: String) {
        val pdfPath = downloadsDir + fileName
        sendDataToApi(pdfPath)
    }

    private fun sendDataToApi(pdfPath: String) {
        patientId = Prefs.init().patientId
        val file = File(pdfPath)
        if (!file.exists()) {
            Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show()
            return
        }

        if (Util.checkIfHasNetwork()) {
            patientId?.let {
                viewModel.updatePatientReport(it, pdfPath) {
                    if (it) {
                        bundle.putString("pdfPath", pdfPath)
                        val intent = Intent(this, PdfViewActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to upload PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            showToast(this, getString(R.string.no_internet_connection))
        }
    }

    override fun onClickSave(transparentSignatureBitmap: Bitmap) {
        mBinding.ivDSignature.setImageBitmap(transparentSignatureBitmap)
        mBinding.tvSignature.visibility = View.GONE
    }
}
