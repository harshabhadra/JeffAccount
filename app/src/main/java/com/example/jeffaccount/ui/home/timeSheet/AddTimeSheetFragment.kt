package com.example.jeffaccount.ui.home.timeSheet

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentAddTimeSheetBinding
import com.example.jeffaccount.model.TimeSheetPost
import com.example.jeffaccount.ui.MainActivity
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
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
    private lateinit var filePath:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addTimeSheetBinding = FragmentAddTimeSheetBinding.inflate(inflater, container, false)

        requestReadPermissions()
        val activity = activity as MainActivity
        activity.setToolbarText("Add Time sheet")
        val arguments = AddTimeSheetFragmentArgs.fromBundle(arguments!!)
        action = arguments.action
        if (action.equals(getString(R.string.update))) {
            val activity = activity as MainActivity
            activity.setToolbarText("Update Time sheet")
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
            val street = addTimeSheetBinding.tsStreetTextInput.text.toString()
            val postCode = addTimeSheetBinding.tsPostCodeTextInput.text.toString()
            val telephone = addTimeSheetBinding.tsTelephoneTextInput.text.toString()
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
                itemDes.isEmpty() -> addTimeSheetBinding.tsItemDesTextInputLayout.error =
                    getString(R.string.enter_item_des)

                (_hours == 0) -> Toast.makeText(context, "Hours can't be zero", Toast.LENGTH_SHORT)
                    .show()
                amount.isEmpty() -> addTimeSheetBinding.tsAmountTextInputLayout.error =
                    getString(R.string.enter_amount)
                totalAmount.isEmpty() -> addTimeSheetBinding.tsTotalAmountTextInputLayout.error =
                    getString(R.string.enter_total_amount)
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Save TimeSheet?")
                    builder.setPositiveButton("Save",DialogInterface.OnClickListener{dialog, which ->
                        viewModel.addTimeSheet(
                            "AngE9676#254r5",
                            jobNo,
                            quotationNo,
                            _vat,
                            date,
                            name,
                            street,
                            "United Kingdom",
                            postCode,
                            telephone,
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
                        dialog.dismiss()
                    })
                    builder.setNegativeButton("Cancel",DialogInterface.OnClickListener{dialog, which ->
                        dialog.dismiss()

                    })
                    val dialog = builder.create()
                    dialog.show()

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
            val street = addTimeSheetBinding.tsStreetTextInput.text.toString()
            val postCode = addTimeSheetBinding.tsPostCodeTextInput.text.toString()
            val telephone = addTimeSheetBinding.tsTelephoneTextInput.text.toString()
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
                (_hours == 0) -> Toast.makeText(context, "Hours can't be zero", Toast.LENGTH_SHORT)
                    .show()
                amount.isEmpty() -> addTimeSheetBinding.tsAmountTextInputLayout.error =
                    getString(R.string.enter_amount)
                totalAmount.isEmpty() -> addTimeSheetBinding.tsTotalAmountTextInputLayout.error =
                    getString(R.string.enter_total_amount)
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Update TimeSheet?")
                    builder.setPositiveButton("Save", DialogInterface.OnClickListener{ dialog, which ->
                        viewModel.updateTimeSheet(
                            "AngE9676#254r5",
                            timeSheet.tid!!.toInt(),
                            jobNo,
                            quotationNo,
                            _vat,
                            date,
                            name,
                            street,
                            "United Kingdom",
                            postCode,
                            telephone,
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
                        dialog.dismiss()
                    })
                    builder.setNegativeButton("Cancel",DialogInterface.OnClickListener{dialog, which ->
                        dialog.dismiss()

                    })
                    val dialog = builder.create()
                    dialog.show()
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
            R.id.convert_pdf_item -> {
                val mFileName =
                    "jeff_account_" + SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.getDefault())
                        .format(System.currentTimeMillis())
                val folder = File(
                    Environment.getExternalStorageDirectory(),
                    getString(R.string.app_name)
                )
                Timber.e(folder.absolutePath)
                var success = true
                if (!folder.exists()) {
                    success = folder.mkdirs()
                }
                filePath = "$folder/$mFileName.pdf"
                Timber.e("file path: $filePath")
                savePdf()
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

    private fun savePdf() {
        val doc = Document(PageSize.A4)
        try {
            PdfWriter.getInstance(doc, FileOutputStream(filePath))
            doc.open()
            val headTable = PdfPTable(2)
            headTable.setWidths(intArrayOf(4, 2))
            headTable.widthPercentage = 100f
            val addressCell = getAddressTable()
            val dateCell = PdfPCell()
            dateCell.border = PdfPCell.NO_BORDER
            dateCell.addElement(Paragraph(timeSheet.date))
            dateCell.addElement(Paragraph(timeSheet.quotationNo))
            dateCell.horizontalAlignment = Element.ALIGN_RIGHT
            headTable.addCell(addressCell)
            headTable.addCell(dateCell)
            doc.add(headTable)
            doc.add(Paragraph(" "))
            val detailsTable = populateDetailsTable()
            doc.add(detailsTable)
            doc.add(Paragraph(" "))
            doc.add(
                Paragraph(
                    "WEBSITE: www.jeffelectrical.com", Font(
                        Font.FontFamily.COURIER, 10f, Font.BOLD,
                        BaseColor.RED
                    )
                )
            )
            doc.add(Paragraph(" "))
            val invoiceTitle =
                Paragraph("INVOICE", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            invoiceTitle.alignment = Element.ALIGN_CENTER
            val invoiceTable = createInvoiceTable()
            doc.add(invoiceTitle)
            doc.add(Paragraph(" "))
            doc.add(invoiceTable)
            doc.add(Paragraph(" "))
            doc.add(Paragraph("Additional Charges"))
            doc.add(Paragraph(" "))
            val totalTable = createTotalTable()
            doc.add(totalTable)
            doc.add(Paragraph(" "))
            doc.add(Paragraph(getString(R.string.jeff_message_to_cus)))
            doc.add(
                Paragraph(
                    getString(R.string.jeff_inquiry_message),
                    Font(Font.FontFamily.UNDEFINED, 10f, Font.BOLD, BaseColor.RED)
                )
            )
            doc.close().let {
                Toast.makeText(context, "Pdf Saved in $filePath", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_VIEW)
                val data = Uri.parse("file://" + filePath)
                intent.setDataAndType(data, "application/pdf")
                startActivity(Intent.createChooser(intent, "Open Pdf"))
            }
        } catch (e: java.lang.Exception) {

            Timber.e("Error: ${e.message}")
        }
    }

    private fun createTotalTable(): PdfPTable {
        val table = PdfPTable(2)
        table.widthPercentage = 100f
        val subTotalCell = PdfPCell()
        subTotalCell.addElement(Paragraph("SUB TOTAL"))
        subTotalCell.setPadding(8f)
        table.addCell(subTotalCell)
        val subTotalDCell = PdfPCell()
        subTotalDCell.addElement(Paragraph(" "))
        subTotalDCell.setPadding(8f)
        table.addCell(subTotalDCell)
        val taxCell = PdfPCell()
        taxCell.addElement(Paragraph("TAX %"))
        taxCell.setPadding(8f)
        table.addCell(taxCell)
        val taxDCell = PdfPCell()
        taxDCell.addElement(Paragraph(timeSheet.vat))
        taxDCell.setPadding(8f)
        table.addCell(taxDCell)
        val taxAmountCell = PdfPCell()
        taxAmountCell.addElement(Paragraph("TAX AMOUNT"))
        taxAmountCell.setPadding(8f)
        table.addCell(taxAmountCell)
        val taxAmountDCell = PdfPCell()
        taxAmountDCell.addElement(Paragraph(" "))
        taxAmountDCell.setPadding(8f)
        table.addCell(taxAmountDCell)
        val disocuntCell = PdfPCell()
        disocuntCell.addElement(Paragraph("DISCOUNT AMOUNT"))
        disocuntCell.setPadding(8f)
        table.addCell(disocuntCell)
        val discountDCell = PdfPCell()
        discountDCell.addElement(Paragraph(timeSheet.discountAmount))
        discountDCell.setPadding(8f)
        table.addCell(discountDCell)
        val totalAmountCell = PdfPCell()
        totalAmountCell.addElement(Paragraph("TOTAL AMOUNT"))
        totalAmountCell.setPadding(8f)
        table.addCell(totalAmountCell)
        val totalAmountDCell = PdfPCell()
        totalAmountDCell.addElement(Paragraph(timeSheet.totalAmount))
        totalAmountDCell.setPadding(8f)
        table.addCell(totalAmountDCell)
        return table
    }

    private fun createInvoiceTable(): PdfPTable {
        val table = PdfPTable(5)
        table.setWidths(intArrayOf(1, 4, 1, 2, 2))
        val jobNoCell = PdfPCell(Paragraph("Job No."))
        jobNoCell.setPadding(8f)
        table.addCell(jobNoCell)
        val desCell = PdfPCell(Paragraph("Description"))
        desCell.horizontalAlignment = Element.ALIGN_CENTER
        desCell.setPadding(8f)
        table.addCell(desCell)
        val quantityCell = PdfPCell(Paragraph("Hours"))
        quantityCell.setPadding(8f)
        table.addCell(quantityCell)
        val unitCell = PdfPCell(Paragraph("Unit Amount"))
        unitCell.setPadding(8f)
        table.addCell(unitCell)
        val discountCell = PdfPCell(Paragraph("Discount Amount"))
        discountCell.setPadding(8f)
        table.addCell(discountCell)

        val noCell = PdfPCell(Paragraph("1"))
        noCell.setPadding(8f)
        table.addCell(noCell)
        val itemDesCell = PdfPCell(Paragraph(timeSheet.itemDescription))
        itemDesCell.setPadding(8f)
        table.addCell(itemDesCell)
        val qtyCell = PdfPCell(Paragraph(timeSheet.hrs))
        qtyCell.setPadding(8f)
        table.addCell(qtyCell)
        val unitDCell = PdfPCell(Paragraph(timeSheet.amount))
        unitDCell.setPadding(8f)
        table.addCell(unitDCell)
        val disDCell = PdfPCell(Paragraph(timeSheet.discountAmount))
        disDCell.setPadding(8f)
        table.addCell(disDCell)
        table.widthPercentage = 100f
        return table
    }

    private fun populateDetailsTable(): PdfPTable {

        val table = PdfPTable(2)
        val nameCell = PdfPCell()
        table.widthPercentage = 100f
        nameCell.border = PdfPCell.NO_BORDER
        nameCell.addElement(Paragraph("Name"))
        val nameDataCell = PdfPCell()
        nameDataCell.border = PdfPCell.NO_BORDER
        nameDataCell.addElement(Paragraph(timeSheet.name))
        nameDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val addressCell = PdfPCell()
        addressCell.border = PdfPCell.NO_BORDER
        addressCell.addElement(Paragraph("Street Address"))
        val addressDataCell = PdfPCell()
        addressDataCell.border = PdfPCell.NO_BORDER
        addressDataCell.addElement(Paragraph(timeSheet.street))
        addressDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val countryCell = PdfPCell()
        countryCell.border = PdfPCell.NO_BORDER
        countryCell.addElement(Paragraph("Country"))
        val countrydataCell = PdfPCell()
        countrydataCell.border = PdfPCell.NO_BORDER
        countrydataCell.addElement(Paragraph(timeSheet.country))
        countrydataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val postCell = PdfPCell()
        postCell.border = PdfPCell.NO_BORDER
        postCell.addElement(Paragraph("Post Code"))
        val postDataCell = PdfPCell()
        postDataCell.border = PdfPCell.NO_BORDER
        postDataCell.addElement(Paragraph(timeSheet.postCode))
        postDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val teleCell = PdfPCell()
        teleCell.border = PdfPCell.NO_BORDER
        teleCell.addElement(Paragraph("Telephone No"))
        val teleDataCell = PdfPCell()
        teleDataCell.border = PdfPCell.NO_BORDER
        teleDataCell.addElement(Paragraph(timeSheet.telephone))
        teleDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        table.addCell(nameCell)
        table.addCell(nameDataCell)
        table.addCell(addressCell)
        table.addCell(addressDataCell)
        table.addCell(countryCell)
        table.addCell(countrydataCell)
        table.addCell(postCell)
        table.addCell(postDataCell)
        table.addCell(teleCell)
        table.addCell(teleDataCell)
        return table
    }

    private fun getAddressTable(): PdfPCell {

        val cell = PdfPCell()
        cell.border = PdfPCell.NO_BORDER
        cell.addElement(
            Paragraph(
                "JEFF Electrical installation and testing",
                Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
            )
        )
        cell.addElement(
            Paragraph(
                "2 Palgrave Road",
                Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.NORMAL)
            )
        )
        cell.addElement(
            Paragraph(
                "Bedform, MK429DH",
                Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.NORMAL)
            )
        )
        cell.addElement(
            Paragraph(
                "Phone: 004-7881871100",
                Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.NORMAL)
            )
        )
        return cell
    }
}
