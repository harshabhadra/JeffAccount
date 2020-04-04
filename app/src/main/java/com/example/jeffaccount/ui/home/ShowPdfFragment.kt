package com.example.jeffaccount.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jeffaccount.R
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfImportedPage
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfWriter
import timber.log.Timber
import java.io.FileOutputStream

/**
 * A simple [Fragment] subclass.
 */
class ShowPdfFragment : Fragment() {

    private lateinit var inputFile: String
    private lateinit var outputFile: String
    private lateinit var pdfPath: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_pdf, container, false)
        val arguments = ShowPdfFragmentArgs.fromBundle(arguments!!)
        pdfPath = arguments.path
        Toast.makeText(context, pdfPath, Toast.LENGTH_SHORT).show()
        return view
    }

    fun makeImageFromPdf() {

        val document = Document()
        val writer = PdfWriter.getInstance(document, FileOutputStream(outputFile))
        document.open()

        val reader = PdfReader(inputFile)
        val n = reader.numberOfPages
        var page: PdfImportedPage

        for (i in 1..n) {
            page = writer.getImportedPage(reader, i)
            val image = Image.getInstance(page)
            Timber.e("${image.url}")
        }
        document.close()
    }
}
