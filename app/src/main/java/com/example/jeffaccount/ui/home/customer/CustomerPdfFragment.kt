package com.example.jeffaccount.ui.home.customer

import android.Manifest
import android.content.Context
import android.content.DialogInterface
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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentCustomerPdfBinding
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
class CustomerPdfFragment : Fragment() {

    private var bitmap: Bitmap? = null
    private lateinit var folder: File
    var targetPdf:String = ""
    private lateinit var customerPdfBinding: FragmentCustomerPdfBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        customerPdfBinding = FragmentCustomerPdfBinding.inflate(inflater,container,false)
        requestReadPermissions()
        val arguments = CustomerPdfFragmentArgs.fromBundle(arguments!!)

        val customer = arguments.customerPdf
        customer?.let {
            customerPdfBinding.customerPdf = customer
        }
        val pdflayout = customerPdfBinding.pdfLayout

        customerPdfBinding.convertToPdfButton.setOnClickListener {
            folder = File(Environment.getExternalStorageDirectory(), getString(R.string.app_name))
            if (!folder.exists()) {
                folder.mkdirs()
            }

            targetPdf = folder.absolutePath + "/" + "pdf" + System.currentTimeMillis() + ".pdf"
            bitmap = loadBitmapFromView(pdflayout, pdflayout.width, pdflayout.height)
            createPdf()
        }
        return customerPdfBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
        val snackbar = Snackbar.make(customerPdfBinding.customerPdfCoordinator,"Pdf Created",Snackbar.LENGTH_INDEFINITE)
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
                        createPermissionDialog()
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

    fun createPermissionDialog(){

        val builder:AlertDialog.Builder = context.let { AlertDialog.Builder(it!!) }
        builder.setTitle("Need Storage Permission")
        builder.setMessage("We need to Access You Storage in order to generate and save Pdf")
        builder.setCancelable(false)
        builder.setPositiveButton("Proceed",DialogInterface.OnClickListener{dialog, id ->
            requestReadPermissions()
        })
        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener{
            dialog, which ->
            dialog.dismiss()
        })

        val alerdialog = builder.create()
        alerdialog.show()
    }
}


