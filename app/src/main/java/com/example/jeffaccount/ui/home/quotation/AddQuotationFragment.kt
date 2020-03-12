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
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddQuotationFragmentBinding
import com.example.jeffaccount.databinding.FragmentQuotationBinding
import com.example.jeffaccount.model.QuotationPost
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Phaser


class AddQuotationFragment : Fragment() ,DatePickerDialog.OnDateSetListener{

    companion object {
        fun newInstance() =
            AddQuotationFragment()
    }

    private var loadingDialog:AlertDialog? = null
    private lateinit var viewModel: AddQuotationViewModel
    private lateinit var quotationBinding: AddQuotationFragmentBinding
    private var vat: Double? = 0.0
    private var unitAmount: Double? = 0.0
    private var advanceAmount: Double? = 0.0
    private var discountAmount: Double? = 0.0
    private var totalAmount: Double? = 0.0
    private var qty = 0
    private lateinit var quotationItem: QuotationPost
    private lateinit var action: String

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

        if (action.equals(getString(R.string.update))) {
            quotationItem = arguments.quotationItem!!
            quotationBinding.quotation = quotationItem
            quotationBinding.supplierUpdateButton.visibility = View.VISIBLE
            quotationBinding.saveQuotationButton.visibility = View.GONE
            qty = quotationItem.quantity!!.toInt()
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

        quotationBinding.quotationDateTextInputLayout.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd =
                DatePickerDialog.newInstance(
                    this,
                    now[Calendar.YEAR],  // Initial year selection
                    now[Calendar.MONTH],  // Initial month selection
                    now[Calendar.DAY_OF_MONTH] // Inital day selection
                )
            dpd.show(activity?.supportFragmentManager!!, "Datepickerdialog")
        }
        //Set on click listener to the quotation save button
        quotationBinding.saveQuotationButton.setOnClickListener {
            val jobNo = quotationBinding.quotationJobTextInput.text.toString()
            val quotationNo = quotationBinding.quotationQuotationoTextInput.text.toString()

            if (quotationBinding.quotationVatTextInput.text.toString().isNotEmpty()) {
                vat = quotationBinding.quotationVatTextInput.text.toString().toDouble()
            }
            val date = quotationBinding.quotationDateTextInputLayout.text.toString()
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
                else -> {
                    viewModel.addQuotaiton(
                        jobNo,
                        quotationNo,
                        vat!!,
                        date,
                        customerName,
                        comment,
                        itemDes,
                        paymentMethod
                        ,
                        qty,
                        unitAmount!!,
                        advanceAmount!!,
                        discountAmount!!,
                        totalAmount!!
                    ).observe(viewLifecycleOwner,
                        Observer {
                            it?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
            val date = quotationBinding.quotationDateTextInputLayout.text.toString()
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
                else -> {
                    viewModel.updateQuotation(
                        quotationItem.qid!!.toInt(),
                        jobNo,
                        quotationNo,
                        vat!!,
                        date,
                        customerName,
                        comment,
                        itemDes,
                        paymentMethod
                        ,
                        qty,
                        unitAmount!!,
                        advanceAmount!!,
                        discountAmount!!,
                        totalAmount!!
                    ).observe(viewLifecycleOwner,
                        Observer {
                            it?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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

        requestReadPermissions()
        viewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)

        //Observe quantity value
        viewModel.quotationQuantityValue.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("$it")
                quotationBinding.quotationQtyTv.text = it.toString()
            }
        })

        viewModel.dateString.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e(it)
                quotationBinding.quotationDateTextInputLayout.setText(it)
            }
        })

        viewModel.getDate()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (action.equals(getString(R.string.update))) {
            inflater.inflate(R.menu.main_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemid = item.itemId

        when (itemid) {
            R.id.delete_item -> {
                val layout = LayoutInflater.from(context).inflate(R.layout.delete_confirmation,null)
                val builder = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
                builder?.setCancelable(false)
                builder?.setView(layout)
                val dialog = builder?.create()
                dialog?.show()

                val delButton = layout.findViewById<Button>(R.id.delete_button)
                val canButton: Button = layout.findViewById(R.id.cancel_del_button)
                delButton.setOnClickListener {
                    viewModel.deleteQuotaton(quotationItem.qid!!.toInt()).observe(viewLifecycleOwner,
                        Observer {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            dialog?.dismiss()
                            findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
                        })
                }
                canButton.setOnClickListener {
                    dialog?.dismiss()
                }

            }
            R.id.convert_pdf_item -> {
                loadingDialog = createLoadingDialog()
                loadingDialog?.show()
                createPdf()
            }
        }
        return true
    }

    //Function to request read and write storage
    private fun requestReadPermissions() {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(
                                context,
                                "All permissions are granted by user!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        //openSettingsDialog();
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
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
    private fun createPdf() {
        val mDoc = Document(PageSize.A4, 8f, 8f, 8f, 8f)
        val folder = File(Environment.getExternalStorageDirectory(), getString(R.string.app_name))
        Timber.e(folder.absolutePath)
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        val mFileName = "jeff_account_." + SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val filePath = folder.absolutePath + "/" + mFileName + ".pdf"

        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(filePath))
            mDoc.open()
            val lineSeparator = LineSeparator()
            lineSeparator.lineColor = BaseColor.WHITE
            val jeffChunk = Chunk(
                getString(R.string.app_name), Font(Font.FontFamily.TIMES_ROMAN,32.0f)
            )
            val heading = Paragraph(jeffChunk)
            heading.alignment = Element.ALIGN_CENTER
            val mChunk = Chunk(
                getString(R.string.quotation)
                , Font(Font.FontFamily.TIMES_ROMAN, 24.0f)
            )
            val title = Paragraph(mChunk)
            title.alignment = Element.ALIGN_CENTER
            mDoc.add(heading)
            mDoc.add(title)
            mDoc.add(lineSeparator)
            mDoc.add(Paragraph(" "))
            val quotationBody = Paragraph()
            createQuotationTable(quotationBody)
            mDoc.add(quotationBody)
            mDoc.close().let {
                loadingDialog?.dismiss()
                Toast.makeText(context, "Pdf Saved in $filePath", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_VIEW)
                val data = Uri.parse("file://" + filePath)
                intent.setDataAndType(data, "application/pdf")
                startActivity(Intent.createChooser(intent, "Open Pdf"))
            }
        } catch (e: Exception) {

            Timber.e(e)
        }
    }

    //Create Quotation table
    private fun createQuotationTable(quotationBody: Paragraph) {

        val table = PdfPTable(floatArrayOf(5f, 5f))
        table.widthPercentage = 100f
        table.defaultCell.isUseAscender = true

        val cell = PdfPCell(Phrase("Job No"))
        cell.paddingBottom = 8f
        cell.paddingLeft = 8f
        val cell1 = PdfPCell(Phrase(quotationItem.jobNo))
        cell1.paddingBottom = 8f
        cell1.paddingLeft = 8f
        val qnoCell = PdfPCell(Phrase("Qutation No"))
        qnoCell.paddingBottom = 8f
        qnoCell.paddingLeft = 8f
        val qnoDCell = PdfPCell(Phrase(quotationItem.quotationNo))
        qnoDCell.paddingBottom = 8f
        qnoDCell.paddingLeft = 8f
        val vatCell = PdfPCell(Phrase("Vat%"))
        vatCell.paddingBottom = 8f
        vatCell.paddingLeft = 8f
        val vatDCell = PdfPCell(Phrase(quotationItem.vat))
        vatDCell.paddingBottom = 8f
        vatDCell.paddingLeft = 8f
        val dateCell = PdfPCell(Phrase("Date"))
        dateCell.paddingBottom = 8f
        dateCell.paddingLeft = 8f
        val dateDCell = PdfPCell(Phrase(quotationItem.date))
        dateDCell.paddingBottom = 8f
        dateDCell.paddingLeft = 8f
        val nameCell = PdfPCell(Phrase("Customer Name"))
        nameCell.paddingBottom = 8f
        nameCell.paddingLeft = 8f
        val nameDCell = PdfPCell(Phrase(quotationItem.customerName))
        nameDCell.paddingBottom = 8f
        nameDCell.paddingLeft = 8f
        val commentCell = PdfPCell(Phrase("Special Instruction"))
        commentCell.paddingBottom = 8f
        commentCell.paddingLeft = 8f
        val commentDCell = PdfPCell(Phrase(quotationItem.specialInstruction))
        commentDCell.paddingBottom = 8f
        commentDCell.paddingLeft = 8f
        val desCell = PdfPCell(Phrase("Item Description"))
        desCell.paddingBottom = 8f
        desCell.paddingLeft = 8f
        val desDCell = PdfPCell(Phrase(quotationItem.itemDescription))
        desDCell.paddingBottom = 8f
        desDCell.paddingLeft = 8f
        val paymentCell = PdfPCell(Phrase("Payment Method"))
        paymentCell.paddingBottom = 8f
        paymentCell.paddingLeft = 8f
        val paymentDCell = PdfPCell(Phrase(quotationItem.paymentMethod))
        paymentDCell.paddingBottom = 8f
        paymentDCell.paddingLeft = 8f
        val qtyCell = PdfPCell(Phrase("Quantity"))
        qtyCell.paddingBottom = 8f
        qtyCell.paddingLeft = 8f
        val qtyDCell = PdfPCell(Phrase(quotationItem.quantity))
        qtyDCell.paddingBottom = 8f
        qtyDCell.paddingLeft = 8f
        val unitCell = PdfPCell(Phrase("Unit Amount"))
        unitCell.paddingBottom = 8f
        unitCell.paddingLeft = 8f
        val unitDCell = PdfPCell(Phrase(quotationItem.unitAmount))
        unitDCell.paddingBottom = 8f
        unitDCell.paddingLeft = 8f
        val advanceCell = PdfPCell(Phrase("Advance Amount"))
        advanceCell.paddingBottom = 8f
        advanceCell.paddingLeft = 8f
        val advanceDCell = PdfPCell(Phrase(quotationItem.advanceAmount))
        advanceDCell.paddingBottom = 8f
        advanceDCell.paddingLeft = 8f
        val discountCell = PdfPCell(Phrase("Discount Amount"))
        discountCell.paddingBottom = 8f
        discountCell.paddingLeft = 8f
        val discountDcell = PdfPCell(Phrase(quotationItem.discountAmount))
        discountDcell.paddingBottom = 8f
        discountDcell.paddingLeft = 8f
        val totalCell = PdfPCell(Phrase("Total Amount"))
        totalCell.paddingBottom = 8f
        totalCell.paddingLeft = 8f
        val totalDCell = PdfPCell(Phrase(quotationItem.totalAmount))
        totalDCell.paddingBottom = 8f
        totalDCell.paddingLeft = 8f
        table.addCell(cell)
        table.addCell(cell1)
        table.addCell(qnoCell)
        table.addCell(qnoDCell)
        table.addCell(vatCell)
        table.addCell(vatDCell)
        table.addCell(dateCell)
        table.addCell(dateDCell)
        table.addCell(nameCell)
        table.addCell(nameDCell)
        table.addCell(commentCell)
        table.addCell(commentDCell)
        table.addCell(desCell)
        table.addCell(desDCell)
        table.addCell(paymentCell)
        table.addCell(paymentDCell)
        table.addCell(qtyCell)
        table.addCell(qtyDCell)
        table.addCell(unitCell)
        table.addCell(unitDCell)
        table.addCell(advanceCell)
        table.addCell(advanceDCell)
        table.addCell(discountCell)
        table.addCell(discountDcell)
        table.addCell(totalCell)
        table.addCell(totalDCell)
        quotationBody.add(table)
    }

    //Create a loading Dialog
    private fun createLoadingDialog(): AlertDialog? {
        val layout = LayoutInflater.from(context).inflate(R.layout.loading_layout, null)
        val builder = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
        builder?.setCancelable(false)
        builder?.setView(layout)
        return builder?.create()
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        quotationBinding.quotationDateTextInputLayout.setText(viewModel.changeDateFormat(dayOfMonth,monthOfYear,year))
    }
}
