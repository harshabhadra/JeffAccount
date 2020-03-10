package com.example.jeffaccount.ui.home.supplier

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.CustomerListItemBinding
import com.example.jeffaccount.databinding.FragmentSupplierPdfBinding
import com.example.jeffaccount.ui.home.customer.CustomerPdfFragment
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class SupplierPdfFragment : Fragment() {

    private var bitmap: Bitmap? = null
    private lateinit var folder: File
    var targetPdf:String = ""
    private lateinit var fragmentSupplierBinding: FragmentSupplierPdfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentSupplierBinding = FragmentSupplierPdfBinding.inflate(inflater,container,false)

        val arguments = SupplierPdfFragmentArgs.fromBundle(arguments!!)
        val supplier = arguments.supplierPdf

        fragmentSupplierBinding.supplierPdf = supplier

        requestReadPermissions()

        val pdflayout = fragmentSupplierBinding.pdfLayout
        fragmentSupplierBinding.convertToPdfButton.setOnClickListener {
            folder = File(Environment.getExternalStorageDirectory(), getString(R.string.app_name))
            if (!folder.exists()) {
                folder.mkdirs()
            }

            targetPdf = folder.absolutePath + "/" + "pdf" + System.currentTimeMillis() + ".pdf"
            bitmap =
                CustomerPdfFragment.loadBitmapFromView(pdflayout, pdflayout.width, pdflayout.height)
            createPdf()
        }
        return fragmentSupplierBinding.root
    }

    private fun createPdf(){
        val wm = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        //  Display display = wm.getDefaultDisplay();
        val displaymetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displaymetrics)
        val hight = displaymetrics.heightPixels.toFloat()
        val width = displaymetrics.widthPixels.toFloat()

        val convertHighet = hight.toInt()
        val convertWidth = width.toInt()

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo
            .Builder(convertWidth, convertHighet, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas

        val paint = Paint()
        canvas.drawPaint(paint)

        bitmap = Bitmap.createScaledBitmap(bitmap!!, convertWidth, convertHighet, true)

        paint.color = Color.BLUE
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        document.finishPage(page)

        // write the document content

        Timber.e("target: $targetPdf")
        val filePath: File
        filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Something wrong: $e", Toast.LENGTH_LONG).show()
        }

        // close the document
        document.close()
        Toast.makeText(context, "PDF is created!!!", Toast.LENGTH_SHORT).show()
        val snackbar = Snackbar.make(fragmentSupplierBinding.supplierPdfCoordinator,"Pdf Created",
            Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("Open",View.OnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse("file://" +targetPdf)
            intent.setDataAndType(data,"application/pdf")
            startActivity(Intent.createChooser(intent, "Open Pdf"))
        })
        snackbar.show()

    }

    companion object{
        fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
            val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.draw(c)

            return b
        }
    }

    private fun requestReadPermissions() {

        Dexter.withActivity(this.activity)
            .withPermissions( Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(context, "All permissions are granted by user!", Toast.LENGTH_SHORT)
                            .show()
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        //openSettingsDialog();
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError) {
                    Toast.makeText(context, "Some Error! ", Toast.LENGTH_SHORT).show()
                }
            })
            .onSameThread()
            .check()
    }
}
