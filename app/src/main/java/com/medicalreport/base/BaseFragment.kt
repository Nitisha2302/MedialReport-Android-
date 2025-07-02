package com.medicalreport.base


import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.medicalreport.R
import com.medicalreport.utils.Util.checkIfHasNetwork
import com.medicalreport.utils.Util.showDarkErrorToast
import com.medicalreport.utils.Util.showDarkInternetToast
import com.medicalreport.utils.Util.showDarkSuccessToast
import com.medicalreport.utils.Util.showErrorToast
import com.medicalreport.utils.Util.showInternetToast
import com.medicalreport.utils.Util.showSuccessToast
import com.medicalreport.utils.isNightMode


abstract class BaseFragment<DataBinding : ViewDataBinding> : Fragment() {

    protected abstract val fragmentLayoutId: Int
    private var layout: View? = null
    private var progressDialog: Dialog? = null
    protected lateinit var binding: DataBinding

    abstract fun setUpUi(binding: DataBinding)
    abstract fun setupObservers()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(inflater, fragmentLayoutId, container, false)
            setUpUi(binding)
        }
        setupObservers()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setGlobalLayoutListener()
    }

    protected open fun setGlobalLayoutListener() {
        layout = requireActivity().findViewById<View>(Window.ID_ANDROID_CONTENT)
        val observer = layout?.viewTreeObserver
        observer?.addOnGlobalLayoutListener(globalLayoutListener)
    }

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        layout?.let {
            onGlobalLayoutCompleted()
            val decorView = activity?.window?.decorView
            val r = Rect()
            decorView?.let {
                //r will be populated with the coordinates of your view that area still visible.
                it.getWindowVisibleDisplayFrame(r)
                //get screen height and calculate the difference with the useable area from the r
                val height = it.context.resources.displayMetrics.heightPixels
                val diff = height - r.bottom

                //if it could be a keyboard add the padding to the view
                if (diff > 0) {
                    //keyboard open
                    onKeyBoardOpen(diff)
                } else {
                    //keyboard hide
                    onKeyBoardClosed()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        layout?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
    }

    /**
     * Give a chance to obtain view layout attributes when the
     * content view layout process is completed.
     * Some layout attributes will be available here but not
     * in onCreate(), like measured width/height.
     * This callback will be called ONLY ONCE before the whole
     * window content is ready to be displayed for first time.
     */
    protected open fun onGlobalLayoutCompleted() {}

    /**
     * It will provide the call back of keyboard when it's open
     */
    protected open fun onKeyBoardOpen(diff: Int) {}

    /**
     * It will provide the call back of keyboard when it's closed
     */
    protected open fun onKeyBoardClosed() {}

    fun showKeyboard(v: View) {
        v.requestFocus()
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED)
    }

    fun hideKeyboard(v: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    protected fun showProgressDialog() {
        if (progressDialog == null) progressDialog =
            Dialog(requireContext(), R.style.progressDialogStyle)

        progressDialog?.let {
            if (!it.isShowing) {
                it.setContentView(R.layout.progress_dialog)
                it.window?.apply {
                    setBackgroundDrawableResource(android.R.color.transparent)
                    clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                }
                it.setCancelable(false)
                it.show()
            }
        }
    }

    protected fun hideProgress() {
        progressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
                it.cancel()
            }
        }
    }

    protected fun showErrorAlert(error: String) {
        if (checkIfHasNetwork()) {
            if (!isNightMode(activity)) {
                activity?.let { showErrorToast(it, error) }
            }else{
                activity?.let { showDarkErrorToast(it,error) }
            }
        } else {
            showNoInternetAlert()

        }
    }

    protected fun showSuccessAlert(message: String) {
        if (!isNightMode(activity)) {
            // Light mode
            activity?.let { showSuccessToast(it,message) }
        } else {
            // Dark mode
            activity?.let { showDarkSuccessToast(it,message) }

        }


    }

    protected fun showNoInternetAlert() {
        if (!isNightMode(activity)) {
            // Light mode
            activity?.let { showInternetToast(it,getString(R.string.connection_lost)) }
        } else {
            // Dark mode
            activity?.let { showDarkInternetToast(it,getString(R.string.connection_lost)) }

        }

    }

    override fun onPause() {
        super.onPause()
        hideProgress()
        layout?.let {
            hideKeyboard(it)
        }
    }
}