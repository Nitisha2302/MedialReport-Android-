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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.medicalreport.utils.filePathToBase64String
import com.medicalreport.utils.getCurrentDate
import com.medicalreport.utils.showToast
import com.medicalreport.utils.uriToBase64
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
//    var imageUpdatedDataList: ArrayList<DocImageItem> = arrayListOf()
    private lateinit var bitmap: Bitmap
    var downloadsDir: String = ""
    var fileName: String = ""
    var content: String = ""
    var fromWhere: String = ""
    var currentDate: String? = ""
    private var ivSignature: ImageView? = null
    private var signatureDialogFragment = SignatureDialogFragment()

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
//            convertBase64String(imageDataList)

        } catch (e: Exception) {
            e.printStackTrace()
        }


        initView()


    }

   /* private fun convertBase64String(imageDataList: ArrayList<ImageData>?) {
        if (fromWhere == "deviceCamera") {
            imageDataList?.forEach {
                imageUpdatedDataList.add(DocImageItem(uriToBase64(it.image, this)))

            }
        } else {
            imageDataList?.forEach {
                imageUpdatedDataList.add(DocImageItem(filePathToBase64String(it.image, this)))
            }
        }

    }*/


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
            bitmap = loadBitmapFromView(
                mBinding.clLayout,
                mBinding.clLayout.width,
                mBinding.clLayout.height
            )
            generatePdfReport()
        }
        mBinding.ivBack.setOnClickListener {
            it.disableMultiTap()
            finish()
            finish()
        }
        mBinding.clSignature.setOnClickListener {
            it.disableMultiTap()
            signatureDialogFragment.setOnSignatureCallback(this) // Pass the interface instance
            signatureDialogFragment.show(supportFragmentManager, "signature_dialog")
            /* signatureDialogFragment.setTargetFragment(null, DIALOG_SUCCESS)
             */
        }
    }

    private fun sendDataToApi() {
        patientId = Prefs.init().patientId
        val pdfFile = downloadsDir + fileName
        /* var pdfFile = "file:///${downloadsDir}/${fileName}"
         var newPdfFile = pdfFilePathToBase64String(pdfFile)*/

        if (Util.checkIfHasNetwork()) {
            patientId?.let {
                viewModel.updatePatientReport(it, pdfFile) {
                }

            }
        } else {
            showToast(this, getString(R.string.no_internet_connection))
        }
    }


    private fun loadBitmapFromView(
        clReportLayout: ConstraintLayout,
        width: Int,
        height: Int
    ): Bitmap {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
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
        var view = LayoutInflater.from(this).inflate(R.layout.activity_pdf_generation, null)
        // Get the screen dimensions
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display?.getRealMetrics(displayMetrics)
        } else this.windowManager.defaultDisplay.getMetrics(displayMetrics)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

        // Create a new PdfDocument instance
        val document = PdfDocument()


        // Obtain the width and height of the view
        val viewWidth = view.measuredWidth
        val viewHeight = view.measuredHeight

        // Create a PageInfo object specifying the page attributes
        val pageInfo = PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create()
        var page = document.startPage(pageInfo)

        //Canvas
        var canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        bitmap = Bitmap.createScaledBitmap(bitmap, viewWidth, viewHeight, true)
        canvas.drawBitmap(bitmap, 0.0F, 0.0F, null)
        //Finish the page
        document.finishPage(page)
        val currentTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .toString() + "/" + "SIMS/"
        // Make sure the folder exists
        val folder = File(downloadsDir)
        if (!folder.exists()) {
            folder.mkdirs() // <-- Create folder if it doesn't exist
        }
        fileName = "${fullName}_PDF_${currentTime}.pdf"
        val file = File(downloadsDir, fileName)


        try {
            var fos = FileOutputStream(file)
            document.writeTo(fos)
            document.close()
            Toast.makeText(this, "Pdf Created Successfully...", Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        sendDataToApi()
        viewPdfFile(downloadsDir, fileName)

    }

    private fun viewPdfFile(downloadsDir: String, fileName: String) {
        val pdfPath = downloadsDir + fileName
        bundle.putString("pdfPath", pdfPath)
        val intent = Intent(this, PdfViewActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    override fun onClickSave(transparentSignatureBitmap: Bitmap) {
        mBinding.ivDSignature.setImageBitmap(transparentSignatureBitmap)
        mBinding.tvSignature.visibility = View.GONE

    }
}
