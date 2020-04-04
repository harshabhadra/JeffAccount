package com.example.jeffaccount

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.github.pdfviewer.PDFView
import java.io.File

fun createPreviewDialog(message:String,context: Context,activity: Activity){
    val builder = AlertDialog.Builder(context)
    builder.setTitle("A Pdf copy is saved in")
    builder.setMessage(message)
    builder.setPositiveButton("Ok",DialogInterface.OnClickListener{dialog, which ->
        dialog.dismiss()
    })
    builder.setPositiveButton("Share", DialogInterface.OnClickListener{dialog, which ->
        val file = File(message)
        val uri = Uri.fromFile(file)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            setType("application/pdf")
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        activity.startActivity(Intent.createChooser(intent,"Choose one"))
    })
    builder.setNegativeButton("Preview",DialogInterface.OnClickListener { dialog, which ->
        PDFView.with(activity)
            .setfilepath(message)
            .start()
        dialog.dismiss()
    })
    val dialog = builder.create()
    dialog.show()
}