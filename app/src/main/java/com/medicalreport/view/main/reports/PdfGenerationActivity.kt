package com.medicalreport.view.main.reports

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.LayoutInflater
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
import com.medicalreport.utils.EdTextWatcher
import com.medicalreport.utils.Prefs
import com.medicalreport.utils.Util
import com.medicalreport.utils.disableMultiTap
import com.medicalreport.utils.getCurrentDate
import com.medicalreport.utils.showToast
import com.medicalreport.view.adapter.ReportCreationAdapter
import com.medicalreport.view.main.dialog.SignatureDialogFragment
import com.medicalreport.viewmodel.PatientViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.getValue

class PdfGenerationActivity : AppCompatActivity(), SignatureDialogFragment.ClickListener {
    private val viewModel by viewModel<PatientViewModel>()

    private lateinit var mBinding: ActivityPdfGenerationBinding
    var imageAdapter: ReportCreationAdapter? = null
    var fullName: String = ""
    var gender: String = ""
    var age: String = ""
    var patientId: Int? = 0
    var imageDataList: ArrayList<ImageData>? = arrayListOf()
    private var selectedDoctors: ArrayList<SelectedDoctorsResponse>? = arrayListOf()
    var bundle = Bundle()
    private lateinit var bitmap: Bitmap
    var downloadsDir: String = ""
    var fileName: String = ""
    var content: String = ""
    var fromWhere: String = ""
    var currentDate: String? = ""
    private var ivSignature: ImageView? = null
    private var signatureDialogFragment = SignatureDialogFragment()

    // A4 size constants in points (1 point = 1/72 inch)
    companion object {
        const val A4_WIDTH_POINTS = 595   // 8.27 inches * 72 points/inch
        const val A4_HEIGHT_POINTS = 842  // 11.69 inches * 72 points/inch
        const val PDF_MARGIN = 40         // Margin in points
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_generation)
        ivSignature = findViewById(R.id.ivDSignature)
        mBinding.toolbar.visibility = View.VISIBLE
        mBinding.prefs = Prefs()
        currentDate = getCurrentDate("dd-MM-yyyy")
        mBinding.etDate.setText(currentDate)
        initListener()
        try {
            val bundle = intent.extras
            imageDataList = bundle?.getParcelableArrayList<ImageData>("imagesList")
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

    private fun initListener() {
        mBinding.ivSave.setOnClickListener {
            it.disableMultiTap()
            // Create bitmap with A4 proportions for better PDF quality
            bitmap = loadBitmapFromViewForA4(
                mBinding.clLayout,
                A4_WIDTH_POINTS,
                A4_HEIGHT_POINTS
            )
            generatePdfReport()
        }
        mBinding.ivBack.setOnClickListener {
            it.disableMultiTap()
            finish()
        }
        mBinding.clSignature.setOnClickListener {
            it.disableMultiTap()
            signatureDialogFragment.setOnSignatureCallback(this)
            signatureDialogFragment.show(supportFragmentManager, "signature_dialog")
        }
    }

    private fun sendDataToApi(pdfPath: String) {
        patientId = Prefs.init().patientId
        println("send to pdf view to api ${pdfPath},${patientId}")
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

    private fun loadBitmapFromViewForA4(
        clReportLayout: ConstraintLayout,
        targetWidth: Int,
        targetHeight: Int
    ): Bitmap {
        // Get the current dimensions of the view
        val currentWidth = clReportLayout.width
        val currentHeight = clReportLayout.height

        // Calculate scale factor to fit A4 while maintaining aspect ratio
        val scaleX = targetWidth.toFloat() / currentWidth.toFloat()
        val scaleY = targetHeight.toFloat() / currentHeight.toFloat()
        val scale = minOf(scaleX, scaleY)

        // Calculate actual dimensions after scaling
        val scaledWidth = (currentWidth * scale).toInt()
        val scaledHeight = (currentHeight * scale).toInt()

        // Create bitmap with scaled dimensions
        val bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Scale the canvas
        canvas.scale(scale, scale)

        // Draw the view onto the scaled canvas
        clReportLayout.draw(canvas)

        return bitmap
    }

    private fun setImageClickAdapter(imageList: MutableList<ImageData>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        mBinding.rvImagesList.setLayoutManager(layoutManager)
        imageAdapter = ReportCreationAdapter(imageList)
        mBinding.rvImagesList.setAdapter(imageAdapter)
    }

    private fun generatePdfReport() {
        try {
            // Create a new PdfDocument instance
            val document = PdfDocument()

            // Create PageInfo with A4 dimensions
            val pageInfo = PdfDocument.PageInfo.Builder(
                A4_WIDTH_POINTS,
                A4_HEIGHT_POINTS,
                1
            ).create()

            // Start the page
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // Create paint for drawing
            val paint = Paint().apply {
                isAntiAlias = true
                isDither = true
                isFilterBitmap = true
            }

            // Fill the canvas with white background
            canvas.drawColor(android.graphics.Color.WHITE)

            // Calculate position to center the content on A4 page
            val availableWidth = A4_WIDTH_POINTS - (PDF_MARGIN * 2)
            val availableHeight = A4_HEIGHT_POINTS - (PDF_MARGIN * 2)

            // Scale bitmap to fit within A4 margins if needed
            val scaledBitmap =
                if (bitmap.width > availableWidth || bitmap.height > availableHeight) {
                    val scaleX = availableWidth.toFloat() / bitmap.width.toFloat()
                    val scaleY = availableHeight.toFloat() / bitmap.height.toFloat()
                    val scale = minOf(scaleX, scaleY)

                    val newWidth = (bitmap.width * scale).toInt()
                    val newHeight = (bitmap.height * scale).toInt()

                    Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                } else {
                    bitmap
                }

            // Calculate position to center the content
            val xOffset = (A4_WIDTH_POINTS - scaledBitmap.width) / 2f
            val yOffset = PDF_MARGIN.toFloat()

            // Draw the bitmap on canvas
            canvas.drawBitmap(scaledBitmap, xOffset, yOffset, paint)

            // Finish the page
            document.finishPage(page)

            // Save the PDF
            savePdfDocument(document)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun savePdfDocument(document: PdfDocument) {
        try {
            val currentTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + "SIMS/"

            // Make sure the folder exists
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

        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "File not found: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } catch (e: IOException) {
            Toast.makeText(this, "IO Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun viewPdfFile(downloadsDir: String, fileName: String) {
        println("pdfview")
        val pdfPath = downloadsDir + fileName
        sendDataToApi(pdfPath)
    }

    override fun onClickSave(transparentSignatureBitmap: Bitmap) {
        mBinding.ivDSignature.setImageBitmap(transparentSignatureBitmap)
        mBinding.tvSignature.visibility = View.GONE
    }
}
