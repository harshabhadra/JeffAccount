package com.example.jeffaccount.ui.home.timeSheet

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentAddTimeSheetBinding
import com.example.jeffaccount.model.TimeSheetPost
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddTimeSheetFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var addTimeSheetBinding: FragmentAddTimeSheetBinding
    private lateinit var viewModel: TimeSheetViewModel
    private var _vat: Double = 0.0
    private var _hours: Int = 0
    private var _amount: Double = 0.0
    private var _advanceAmount: Double = 0.0
    private var _discountAmount: Double = 0.0
    private var _totalAmount: Double = 0.0
    private lateinit var action: String
    private lateinit var timeSheet: TimeSheetPost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addTimeSheetBinding = FragmentAddTimeSheetBinding.inflate(inflater, container, false)

        requestReadPermissions()

        val arguments = AddTimeSheetFragmentArgs.fromBundle(arguments!!)
        action = arguments.action
        if (action.equals(getString(R.string.update))) {
            addTimeSheetBinding.tsUpdateButton.visibility = View.VISIBLE
            addTimeSheetBinding.tsSaveButton.visibility = View.GONE
            timeSheet = arguments.timeSheetItem!!
            addTimeSheetBinding.timeSheet = timeSheet
            _hours = timeSheet.hrs!!.toInt()
        }

        setHasOptionsMenu(true)

        //Adding Text watcher to all input fields
        addTimeSheetBinding.timeSheetJobNoTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.timeSheetJobNoTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.timeSheetJobNoTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsQuotationNoTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsQuotationNoTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsQuotationNoTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsVatTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsVatTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsVatTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsNameTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsNameTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsNameTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsCommentTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsCommentTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsCommentTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsItemDesTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsItemDesTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsItemDesTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsPaymentMethodTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsPaymentMethodTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsPaymentMethodTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsAmountTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsAdvanceAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsAdvanceAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsAdvanceAmountTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsDiscountAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsDiscountAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsDiscountAmountTextInputLayout.isErrorEnabled = false
            }
        })

        addTimeSheetBinding.tsTotalAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addTimeSheetBinding.tsTotalAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addTimeSheetBinding.tsTotalAmountTextInputLayout.isErrorEnabled = false
            }
        })
        return addTimeSheetBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(TimeSheetViewModel::class.java)

        //Set on click listener to hrs plus button
        addTimeSheetBinding.tsPlusButton.setOnClickListener {
            if (_hours >= 0) {
                _hours++
                viewModel.changeHours(_hours)
            }
        }

        //Set on click listener to hrs minus button
        addTimeSheetBinding.tsMinusButton.setOnClickListener {
            if (_hours > 0) {
                _hours--
                viewModel.changeHours(_hours)
            }
        }

        //Observe hours value
        viewModel.timeSheetHours.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e(it.toString())
                addTimeSheetBinding.tsHoursTv.text = it.toString()
            }
        })

        //set on click listener to date  tv
        addTimeSheetBinding.tsDateTv.setOnClickListener {
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

        //Set on Click listener to save button
        addTimeSheetBinding.tsSaveButton.setOnClickListener {

            val jobNo = addTimeSheetBinding.timeSheetJobNoTextInput.text.toString()
            val quotationNo = addTimeSheetBinding.tsQuotationNoTextInput.text.toString()
            val vat = addTimeSheetBinding.tsVatTextInput.text.toString()
            if (vat.isNotEmpty()) {
                _vat = vat.toDouble()
            }
            val date = addTimeSheetBinding.tsDateTv.text.toString()
            val name = addTimeSheetBinding.tsNameTextInput.text.toString()
            val comment = addTimeSheetBinding.tsCommentTextInput.text.toString()
            val itemDes = addTimeSheetBinding.tsItemDesTextInput.text.toString()
            val paymentMethod = addTimeSheetBinding.tsPaymentMethodTextInput.text.toString()
            val amount = addTimeSheetBinding.tsAmountTextInput.text.toString()
            if (amount.isNotEmpty()) {
                _amount = amount.toDouble()
            }
            val advanceAmount = addTimeSheetBinding.tsAdvanceAmountTextInput.text.toString()
            if (advanceAmount.isNotEmpty()) {
                _advanceAmount = advanceAmount.toDouble()
            }
            val discountAmount = addTimeSheetBinding.tsDiscountAmountTextInput.text.toString()
            if (discountAmount.isNotEmpty()) {
                _discountAmount = discountAmount.toDouble()
            }
            val totalAmount = addTimeSheetBinding.tsTotalAmountTextInput.text.toString()
            if (totalAmount.isNotEmpty()) {
                _totalAmount = totalAmount.toDouble()
            }

            //Check if fields are not empty
            when {
                jobNo.isEmpty() -> addTimeSheetBinding.timeSheetJobNoTextInputLayout.error =
                    getString(R.string.enter_job_no)
                quotationNo.isEmpty() -> addTimeSheetBinding.tsQuotationNoTextInputLayout.error =
                    getString(R.string.enter_quotation_no)
                vat.isEmpty() -> addTimeSheetBinding.tsVatTextInputLayout.error =
                    getString(R.string.enter_vat)
                date.isEmpty() -> Toast.makeText(context, "Enter date", Toast.LENGTH_SHORT).show()
                name.isEmpty() -> addTimeSheetBinding.tsNameTextInputLayout.error =
                    getString(R.string.enter_name)
                comment.isEmpty() -> addTimeSheetBinding.tsCommentTextInputLayout.error =
                    getString(R.string.enter_comment)
                itemDes.isEmpty() -> addTimeSheetBinding.tsItemDesTextInputLayout.error =
                    getString(R.string.enter_item_des)
                paymentMethod.isEmpty() -> addTimeSheetBinding.tsPaymentMethodTextInputLayout.error =
                    getString(R.string.enter_payment_method)
                (_hours == 0) -> Toast.makeText(context, "Hours can't be zero", Toast.LENGTH_SHORT)
                    .show()
                amount.isEmpty() -> addTimeSheetBinding.tsAmountTextInputLayout.error =
                    getString(R.string.enter_amount)
                advanceAmount.isEmpty() -> addTimeSheetBinding.tsAdvanceAmountTextInputLayout.error =
                    getString(R.string.enter_advance_amount)
                discountAmount.isEmpty() -> addTimeSheetBinding.tsDiscountAmountTextInputLayout.error =
                    getString(R.string.enter_discount_amount)
                totalAmount.isEmpty() -> addTimeSheetBinding.tsTotalAmountTextInputLayout.error =
                    getString(R.string.enter_total_amount)
                else -> {
                    viewModel.addTimeSheet(
                        jobNo,
                        quotationNo,
                        _vat,
                        date,
                        name,
                        comment,
                        itemDes,
                        paymentMethod,
                        _hours,
                        _amount,
                        _advanceAmount,
                        _discountAmount,
                        _totalAmount
                    ).observe(
                        viewLifecycleOwner, Observer {
                            it?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(AddTimeSheetFragmentDirections.actionAddTimeSheetFragmentToTimeSheetFragment())
                            }
                        }
                    )
                }
            }
        }

        //Set on click listener to update button
        addTimeSheetBinding.tsUpdateButton.setOnClickListener {
            val jobNo = addTimeSheetBinding.timeSheetJobNoTextInput.text.toString()
            val quotationNo = addTimeSheetBinding.tsQuotationNoTextInput.text.toString()
            val vat = addTimeSheetBinding.tsVatTextInput.text.toString()
            if (vat.isNotEmpty()) {
                _vat = vat.toDouble()
            }
            val date = addTimeSheetBinding.tsDateTv.text.toString()
            val name = addTimeSheetBinding.tsNameTextInput.text.toString()
            val comment = addTimeSheetBinding.tsCommentTextInput.text.toString()
            val itemDes = addTimeSheetBinding.tsItemDesTextInput.text.toString()
            val paymentMethod = addTimeSheetBinding.tsPaymentMethodTextInput.text.toString()
            val amount = addTimeSheetBinding.tsAmountTextInput.text.toString()
            if (amount.isNotEmpty()) {
                _amount = amount.toDouble()
            }
            val advanceAmount = addTimeSheetBinding.tsAdvanceAmountTextInput.text.toString()
            if (advanceAmount.isNotEmpty()) {
                _advanceAmount = advanceAmount.toDouble()
            }
            val discountAmount = addTimeSheetBinding.tsDiscountAmountTextInput.text.toString()
            if (discountAmount.isNotEmpty()) {
                _discountAmount = discountAmount.toDouble()
            }
            val totalAmount = addTimeSheetBinding.tsTotalAmountTextInput.text.toString()
            if (totalAmount.isNotEmpty()) {
                _totalAmount = totalAmount.toDouble()
            }

            //Check if fields are not empty
            when {
                jobNo.isEmpty() -> addTimeSheetBinding.timeSheetJobNoTextInputLayout.error =
                    getString(R.string.enter_job_no)
                quotationNo.isEmpty() -> addTimeSheetBinding.tsQuotationNoTextInputLayout.error =
                    getString(R.string.enter_quotation_no)
                vat.isEmpty() -> addTimeSheetBinding.tsVatTextInputLayout.error =
                    getString(R.string.enter_vat)
                date.isEmpty() -> Toast.makeText(context, "Enter date", Toast.LENGTH_SHORT).show()
                name.isEmpty() -> addTimeSheetBinding.tsNameTextInputLayout.error =
                    getString(R.string.enter_name)
                comment.isEmpty() -> addTimeSheetBinding.tsCommentTextInputLayout.error =
                    getString(R.string.enter_comment)
                itemDes.isEmpty() -> addTimeSheetBinding.tsItemDesTextInputLayout.error =
                    getString(R.string.enter_item_des)
                paymentMethod.isEmpty() -> addTimeSheetBinding.tsPaymentMethodTextInputLayout.error =
                    getString(R.string.enter_payment_method)
                (_hours == 0) -> Toast.makeText(context, "Hours can't be zero", Toast.LENGTH_SHORT)
                    .show()
                amount.isEmpty() -> addTimeSheetBinding.tsAmountTextInputLayout.error =
                    getString(R.string.enter_amount)
                advanceAmount.isEmpty() -> addTimeSheetBinding.tsAdvanceAmountTextInputLayout.error =
                    getString(R.string.enter_advance_amount)
                discountAmount.isEmpty() -> addTimeSheetBinding.tsDiscountAmountTextInputLayout.error =
                    getString(R.string.enter_discount_amount)
                totalAmount.isEmpty() -> addTimeSheetBinding.tsTotalAmountTextInputLayout.error =
                    getString(R.string.enter_total_amount)
                else -> {
                    viewModel.updateTimeSheet(
                        timeSheet.tid!!.toInt(),
                        jobNo,
                        quotationNo,
                        _vat,
                        date,
                        name,
                        comment,
                        itemDes,
                        paymentMethod,
                        _hours,
                        _amount,
                        _advanceAmount,
                        _discountAmount,
                        _totalAmount
                    ).observe(
                        viewLifecycleOwner, Observer {
                            it?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(AddTimeSheetFragmentDirections.actionAddTimeSheetFragmentToTimeSheetFragment())
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        addTimeSheetBinding.tsDateTv.text =
            viewModel.changeDateFormat(dayOfMonth, monthOfYear, year)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (action.equals(getString(R.string.update))) {
            inflater.inflate(R.menu.main_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId
        when (itemId) {
            R.id.delete_item -> {
                val layout =
                    LayoutInflater.from(context).inflate(R.layout.delete_confirmation, null)
                val builder = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
                builder?.setCancelable(false)
                builder?.setView(layout)
                val dialog = builder?.create()
                dialog?.show()

                val delButton = layout.findViewById<Button>(R.id.delete_button)
                val canButton: Button = layout.findViewById(R.id.cancel_del_button)
                delButton.setOnClickListener {
                    dialog?.dismiss()
                    viewModel.deleteTimeSheet(timeSheet.tid!!.toInt()).observe(viewLifecycleOwner,
                        Observer {
                            it?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(
                                    AddTimeSheetFragmentDirections
                                        .actionAddTimeSheetFragmentToTimeSheetFragment()
                                )
                            }
                        })
                }
                canButton.setOnClickListener {
                    dialog?.dismiss()
                }
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
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {

                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        //openSettingsDialog();
                        Toast.makeText(
                                context,
                                "We need To access your storage to create Pdf",
                                Toast.LENGTH_SHORT
                            )
                            .show()
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
                getString(R.string.time_sheet)
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
    private fun createQuotationTable(timeSheetBody: Paragraph) {

        val table = PdfPTable(floatArrayOf(5f, 5f))
        table.widthPercentage = 100f
        table.defaultCell.isUseAscender = true

        val cell = PdfPCell(Phrase("Job No"))
        cell.paddingBottom = 8f
        cell.paddingLeft = 8f
        val cell1 = PdfPCell(Phrase(timeSheet.jobNo))
        cell1.paddingBottom = 8f
        cell1.paddingLeft = 8f
        val qnoCell = PdfPCell(Phrase("Qutation No"))
        qnoCell.paddingBottom = 8f
        qnoCell.paddingLeft = 8f
        val qnoDCell = PdfPCell(Phrase(timeSheet.quotationNo))
        qnoDCell.paddingBottom = 8f
        qnoDCell.paddingLeft = 8f
        val vatCell = PdfPCell(Phrase("Vat%"))
        vatCell.paddingBottom = 8f
        vatCell.paddingLeft = 8f
        val vatDCell = PdfPCell(Phrase(timeSheet.vat))
        vatDCell.paddingBottom = 8f
        vatDCell.paddingLeft = 8f
        val dateCell = PdfPCell(Phrase("Date"))
        dateCell.paddingBottom = 8f
        dateCell.paddingLeft = 8f
        val dateDCell = PdfPCell(Phrase(timeSheet.date))
        dateDCell.paddingBottom = 8f
        dateDCell.paddingLeft = 8f
        val nameCell = PdfPCell(Phrase("Name"))
        nameCell.paddingBottom = 8f
        nameCell.paddingLeft = 8f
        val nameDCell = PdfPCell(Phrase(timeSheet.name))
        nameDCell.paddingBottom = 8f
        nameDCell.paddingLeft = 8f
        val commentCell = PdfPCell(Phrase("Special Instruction"))
        commentCell.paddingBottom = 8f
        commentCell.paddingLeft = 8f
        val commentDCell = PdfPCell(Phrase(timeSheet.specialInstruction))
        commentDCell.paddingBottom = 8f
        commentDCell.paddingLeft = 8f
        val desCell = PdfPCell(Phrase("Item Description"))
        desCell.paddingBottom = 8f
        desCell.paddingLeft = 8f
        val desDCell = PdfPCell(Phrase(timeSheet.itemDescription))
        desDCell.paddingBottom = 8f
        desDCell.paddingLeft = 8f
        val paymentCell = PdfPCell(Phrase("Payment Method"))
        paymentCell.paddingBottom = 8f
        paymentCell.paddingLeft = 8f
        val paymentDCell = PdfPCell(Phrase(timeSheet.paymentMethod))
        paymentDCell.paddingBottom = 8f
        paymentDCell.paddingLeft = 8f
        val qtyCell = PdfPCell(Phrase("Hours"))
        qtyCell.paddingBottom = 8f
        qtyCell.paddingLeft = 8f
        val qtyDCell = PdfPCell(Phrase(timeSheet.hrs))
        qtyDCell.paddingBottom = 8f
        qtyDCell.paddingLeft = 8f
        val unitCell = PdfPCell(Phrase("Unit Amount"))
        unitCell.paddingBottom = 8f
        unitCell.paddingLeft = 8f
        val unitDCell = PdfPCell(Phrase(timeSheet.amount))
        unitDCell.paddingBottom = 8f
        unitDCell.paddingLeft = 8f
        val advanceCell = PdfPCell(Phrase("Advance Amount"))
        advanceCell.paddingBottom = 8f
        advanceCell.paddingLeft = 8f
        val advanceDCell = PdfPCell(Phrase(timeSheet.advanceAmount))
        advanceDCell.paddingBottom = 8f
        advanceDCell.paddingLeft = 8f
        val discountCell = PdfPCell(Phrase("Discount Amount"))
        discountCell.paddingBottom = 8f
        discountCell.paddingLeft = 8f
        val discountDcell = PdfPCell(Phrase(timeSheet.discountAmount))
        discountDcell.paddingBottom = 8f
        discountDcell.paddingLeft = 8f
        val totalCell = PdfPCell(Phrase("Total Amount"))
        totalCell.paddingBottom = 8f
        totalCell.paddingLeft = 8f
        val totalDCell = PdfPCell(Phrase(timeSheet.totalAmount))
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
        timeSheetBody.add(table)
    }
}
