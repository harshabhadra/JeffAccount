package com.example.jeffaccount.ui.home.quotation

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddQuotationFragmentBinding
import com.example.jeffaccount.databinding.FragmentQuotationBinding
import com.example.jeffaccount.model.QuotationPost
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
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
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Phaser


class AddQuotationFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddQuotationFragment()
    }

    private lateinit var viewModel: AddQuotationViewModel
    private lateinit var quotationBinding: AddQuotationFragmentBinding
    private var vat: Double? = null
    private var unitAmount: Double? = null
    private var advanceAmount: Double? = null
    private var discountAmount: Double? = null
    private var totalAmount: Double? = null
    private var qty = 0
    private lateinit var quotationItem:QuotationPost
    private lateinit var action:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        quotationBinding = AddQuotationFragmentBinding.inflate(inflater, container, false)

        quotationBinding.saveQuotationButton.setOnClickListener {
            findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
        }

        //Taking arguments from Quotation Fragment
        val arguments = AddQuotationFragmentArgs.fromBundle(arguments!!)
        action = arguments.updateQuotation

        if (action.equals(getString(R.string.update))){
            quotationItem = arguments.quotationItem!!
            quotationBinding.quotation = quotationItem
            quotationBinding.supplierUpdateButton.visibility = View.VISIBLE
            quotationBinding.saveQuotationButton.visibility = View.GONE
        }

        setHasOptionsMenu(true)

        //Set on click listener to add button
        quotationBinding.quotationPlusButton.setOnClickListener {

            if (qty >= 0) {
                qty++
                viewModel.addQuantity(qty)
            }
        }

        //Set on click listener to minus button
        quotationBinding.quotationMinusButton.setOnClickListener {
            if (qty > 0)
                qty--
            viewModel.removeQuantity(qty)
        }

        //Set on click listener to the quotation save button
        quotationBinding.saveQuotationButton.setOnClickListener {
            val jobNo = quotationBinding.quotationJobTextInput.text.toString()
            val quotationNo = quotationBinding.quotationQuotationoTextInput.text.toString()

            if (quotationBinding.quotationVatTextInput.text.toString().isNotEmpty()) {
                vat = quotationBinding.quotationVatTextInput.text.toString().toDouble()
            }
            val date = quotationBinding.quotationDateTextInput.text.toString()
            val customerName = quotationBinding.quotationCustomerNameTextInput.text.toString()
            val comment = quotationBinding.quotationCommentTextInput.text.toString()
            val itemDes = quotationBinding.quotationItemdesTextInput.text.toString()
            val paymentMethod = quotationBinding.quotationPayementMethodTextInput.text.toString()
            if (quotationBinding.quotationUnitAmountTextInput.text.toString().isNotEmpty()) {
                unitAmount =
                    quotationBinding.quotationUnitAmountTextInput.text.toString().toDouble()
            }
            if (quotationBinding.quotationAdvanceAmountTextInput.text.toString().isNotEmpty()) {
                advanceAmount =
                    quotationBinding.quotationAdvanceAmountTextInput.text.toString().toDouble()
            }

            if (quotationBinding.quotationDiscountAmountTextInput.text.toString().isNotEmpty()) {
                discountAmount =
                    quotationBinding.quotationDiscountAmountTextInput.text.toString().toDouble()
            }

            if (quotationBinding.quotationTotalAmountTextinput.text.toString().isNotEmpty()) {
                totalAmount =
                    quotationBinding.quotationTotalAmountTextinput.text.toString().toDouble()
            }

            when {
                jobNo.isEmpty() -> {
                    quotationBinding.quotationJobTextInputLayout.error =
                        getString(R.string.enter_job_no)
                }

                quotationNo.isEmpty() -> {
                    quotationBinding.quotationQuotationoTextInputLayout.error =
                        getString(R.string.enter_quotation_no)
                }
                quotationBinding.quotationVatTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationVatTextInputLayout.error =
                        getString(R.string.enter_vat)
                }
                date.isEmpty() -> {
                    quotationBinding.quotationDateTextInputLayout.error =
                        getString(R.string.enter_date)
                }
                customerName.isEmpty() -> {
                    quotationBinding.quotationCustomerNameTextInputLayout.error =
                        getString(R.string.enter_customer_name)
                }
                comment.isEmpty() -> {
                    quotationBinding.quotationCommentTextInputLayout.error =
                        getString(R.string.enter_comment)
                }

                itemDes.isEmpty() -> {
                    quotationBinding.quotationItemDesTextInputLayout.error =
                        getString(R.string.enter_item_des)
                }
                paymentMethod.isEmpty() -> {
                    quotationBinding.quotationPayementMethodTextInputLayout.error =
                        "Select a payment method"
                }
                qty == 0 -> {
                    Toast.makeText(context, "Quantity Cannot be 0", Toast.LENGTH_SHORT).show()
                }
                quotationBinding.quotationUnitAmountTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationUnitAmountTextInputLayout.error = "Enter Unit Amount"
                }
                quotationBinding.quotationAdvanceAmountTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationAdvanceAmountTextInputLayout.error =
                        "Enter Advance Amount"
                }
                quotationBinding.quotationDiscountAmountTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationDiscountAmountTextInputLayout.error =
                        "Enter Discount Amount"
                }
                quotationBinding.quotationTotalAmountTextinput.text.toString().isEmpty() -> {
                    quotationBinding.quotationTotalAmountTextinputlayout.error =
                        "Enter Total Amount"
                }
                else ->{
                    viewModel.addQuotaiton(jobNo,quotationNo,vat!!,date,customerName,comment,itemDes,paymentMethod
                    ,qty,unitAmount!!,advanceAmount!!,discountAmount!!,totalAmount!!).observe(viewLifecycleOwner,
                        Observer {
                            it?.let {
                                Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                                findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
                            }
                        })
                }
            }
        }

        //Add On Click Listener to update quotation button
        quotationBinding.supplierUpdateButton.setOnClickListener {
            val jobNo = quotationBinding.quotationJobTextInput.text.toString()
            val quotationNo = quotationBinding.quotationQuotationoTextInput.text.toString()

            if (quotationBinding.quotationVatTextInput.text.toString().isNotEmpty()) {
                vat = quotationBinding.quotationVatTextInput.text.toString().toDouble()
            }
            val date = quotationBinding.quotationDateTextInput.text.toString()
            val customerName = quotationBinding.quotationCustomerNameTextInput.text.toString()
            val comment = quotationBinding.quotationCommentTextInput.text.toString()
            val itemDes = quotationBinding.quotationItemdesTextInput.text.toString()
            val paymentMethod = quotationBinding.quotationPayementMethodTextInput.text.toString()
            if (quotationBinding.quotationUnitAmountTextInput.text.toString().isNotEmpty()) {
                unitAmount =
                    quotationBinding.quotationUnitAmountTextInput.text.toString().toDouble()
            }
            if (quotationBinding.quotationAdvanceAmountTextInput.text.toString().isNotEmpty()) {
                advanceAmount =
                    quotationBinding.quotationAdvanceAmountTextInput.text.toString().toDouble()
            }

            if (quotationBinding.quotationDiscountAmountTextInput.text.toString().isNotEmpty()) {
                discountAmount =
                    quotationBinding.quotationDiscountAmountTextInput.text.toString().toDouble()
            }

            if (quotationBinding.quotationTotalAmountTextinput.text.toString().isNotEmpty()) {
                totalAmount =
                    quotationBinding.quotationTotalAmountTextinput.text.toString().toDouble()
            }

            when {
                jobNo.isEmpty() -> {
                    quotationBinding.quotationJobTextInputLayout.error =
                        getString(R.string.enter_job_no)
                }

                quotationNo.isEmpty() -> {
                    quotationBinding.quotationQuotationoTextInputLayout.error =
                        getString(R.string.enter_quotation_no)
                }
                quotationBinding.quotationVatTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationVatTextInputLayout.error =
                        getString(R.string.enter_vat)
                }
                date.isEmpty() -> {
                    quotationBinding.quotationDateTextInputLayout.error =
                        getString(R.string.enter_date)
                }
                customerName.isEmpty() -> {
                    quotationBinding.quotationCustomerNameTextInputLayout.error =
                        getString(R.string.enter_customer_name)
                }
                comment.isEmpty() -> {
                    quotationBinding.quotationCommentTextInputLayout.error =
                        getString(R.string.enter_comment)
                }

                itemDes.isEmpty() -> {
                    quotationBinding.quotationItemDesTextInputLayout.error =
                        getString(R.string.enter_item_des)
                }
                paymentMethod.isEmpty() -> {
                    quotationBinding.quotationPayementMethodTextInputLayout.error =
                        "Select a payment method"
                }
                qty == 0 -> {
                    Toast.makeText(context, "Quantity Cannot be 0", Toast.LENGTH_SHORT).show()
                }
                quotationBinding.quotationUnitAmountTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationUnitAmountTextInputLayout.error = "Enter Unit Amount"
                }
                quotationBinding.quotationAdvanceAmountTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationAdvanceAmountTextInputLayout.error =
                        "Enter Advance Amount"
                }
                quotationBinding.quotationDiscountAmountTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationDiscountAmountTextInputLayout.error =
                        "Enter Discount Amount"
                }
                quotationBinding.quotationTotalAmountTextinput.text.toString().isEmpty() -> {
                    quotationBinding.quotationTotalAmountTextinputlayout.error =
                        "Enter Total Amount"
                }
                else ->{
                    viewModel.updateQuotation(quotationItem.qid!!.toInt(),jobNo,quotationNo,vat!!,date,customerName,comment,itemDes,paymentMethod
                        ,qty,unitAmount!!,advanceAmount!!,discountAmount!!,totalAmount!!).observe(viewLifecycleOwner,
                        Observer {
                            it?.let {
                                Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                                findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
                            }
                        })
                }
            }
        }
        //Add Text Watcher to job no
        quotationBinding.quotationJobTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationJobTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationJobTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to quotation no
        quotationBinding.quotationQuotationoTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationQuotationoTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationQuotationoTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to vat
        quotationBinding.quotationVatTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationVatTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationVatTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to name
        quotationBinding.quotationCustomerNameTextInput.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationCustomerNameTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationCustomerNameTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to comment
        quotationBinding.quotationCommentTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationCommentTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationCommentTextInputLayout.isErrorEnabled = false
            }
        })

        //Add text watcher to item description
        quotationBinding.quotationItemdesTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationItemDesTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationItemDesTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to unit amount
        quotationBinding.quotationUnitAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationUnitAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationUnitAmountTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text watcher to advance amount
        quotationBinding.quotationAdvanceAmountTextInput.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationAdvanceAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationAdvanceAmountTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to discount amount
        quotationBinding.quotationDiscountAmountTextInput.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationDiscountAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationDiscountAmountTextInputLayout.isErrorEnabled = false
            }
        })

        //Add TExt watcher to total amount
        quotationBinding.quotationTotalAmountTextinput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationTotalAmountTextinputlayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationTotalAmountTextinputlayout.isErrorEnabled = false
            }
        })
        return quotationBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)
        // TODO: Use the ViewModel

        viewModel.quotationQuantityValue.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("${it.toString()}")
                quotationBinding.quotationQtyTv.text = it.toString()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if(action.equals(getString(R.string.update))){
            inflater.inflate(R.menu.main_menu,menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemid = item.itemId

        when(itemid){
             R.id.delete_item->{
                 viewModel.deleteQuotaton(quotationItem.qid!!.toInt()).observe(viewLifecycleOwner,
                     Observer {
                         Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                         findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
                     })
             }
            R.id.convert_pdf_item->{
                createPdf()
            }
        }
        return true
    }

    //Function to request read and write storage
    private fun requestReadPermissions() {
        Dexter.withActivity(activity)
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

    //Permission to make pdf
    private fun createPdf(){
        val mDoc = Document(PageSize.A4,8f,8f,8f,8f)
        val folder = File(Environment.getExternalStorageDirectory(),getString(R.string.app_name))
        Timber.e(folder.absolutePath)
        var success = true
        if(!folder.exists()){
            success = folder.mkdirs()
            Toast.makeText(context, "Folder created",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Folder exists",Toast.LENGTH_SHORT).show()
        }
        val mFileName = "jeff_account_."+SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val filePath =folder.absolutePath + "/" + mFileName + ".pdf"

        try {
            PdfWriter.getInstance(mDoc,FileOutputStream(filePath))
            mDoc.open()
            val lineSeparator = LineSeparator()
            lineSeparator.lineColor = BaseColor.BLUE

            val mChunk = Chunk(getString(R.string.quotation)
                ,Font(Font.FontFamily.TIMES_ROMAN,16.0f))
            val title = Paragraph(mChunk)
            title.alignment = Element.ALIGN_CENTER
            mDoc.add(title)
            mDoc.add(Chunk(lineSeparator))

            val quotationBody = Paragraph()
            createQuotationTable(quotationBody)
            mDoc.add(quotationBody)
            mDoc.close()
            val snackBar = Snackbar.make(quotationBinding.addQuotationCoordinator,"Pdf Created", Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction("Open", View.OnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                val data = Uri.parse("file://" +filePath)
                intent.setDataAndType(data,"application/pdf")
                startActivity(Intent.createChooser(intent, "Open Pdf"))
            })
            snackBar.show()
        }catch (e:Exception){

            Timber.e(e.message)
        }
    }

    private fun createQuotationTable(quotationBody: Paragraph) {

        val table = PdfPTable(floatArrayOf(5f,5f))
        table.widthPercentage = 100f
        table.defaultCell.isUseAscender = true

        var cell = PdfPCell(Phrase("Job No", Font(Font.FontFamily.TIMES_ROMAN)))
        var cell1 = PdfPCell(Phrase(quotationItem.jobNo))
        table.addCell(cell)
        table.addCell(cell1)
        table.addCell(PdfPCell(Phrase("Qutation No")))
        table.addCell(PdfPCell(Phrase(quotationItem.quotationNo)))
        table.addCell(PdfPCell(Phrase("Vat%")))
        table.addCell(PdfPCell(Phrase(quotationItem.vat)))
//        table.addCell(PdfPCell(Phrase("Qutation No")))
//        table.addCell(PdfPCell(Phrase("Qutation No")))
//        table.addCell(PdfPCell(Phrase("Qutation No")))
//        table.addCell(PdfPCell(Phrase("Qutation No")))
        quotationBody.add(table)
    }
}
