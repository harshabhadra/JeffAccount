package com.example.jeffaccount.ui.home.purchase

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddPurchaseFragmentBinding
import com.example.jeffaccount.model.PurchasePost
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.network.PItem
import com.example.jeffaccount.network.SearchCustomer
import com.example.jeffaccount.ui.home.quotation.ItemAdapter
import com.example.jeffaccount.ui.home.quotation.OnSearchItemClickListener
import com.example.jeffaccount.ui.home.quotation.SearchCustomerBottomSheetFragment
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
import java.text.SimpleDateFormat
import java.util.*


class AddPurchaseFragment : Fragment(), DatePickerDialog.OnDateSetListener, OnSearchItemClickListener {

    private lateinit var purchaseBinding: AddPurchaseFragmentBinding
    private lateinit var action: String
    private lateinit var purchase: PurchasePost
    private var _vat: Double = 0.0
    private var qty: Int = 0
    private var _unitAmount: Double = 0.0
    private var _advanceAmount: Double = 0.0
    private var _discountAmount: Double = 0.0
    private var _totalAmount: Double = 0.0
    private lateinit var filePath:String
    private lateinit var itemAdapter: ItemAdapter
    private var itemList: MutableList<Item> = mutableListOf()
    private var nameList:MutableList<String> = mutableListOf()
    private var addedItemList:MutableList<Item> = mutableListOf()
    private var itemNo: Int = 1

    companion object {
        fun newInstance() =
            AddPurchaseFragment()
    }

    private lateinit var viewModel: AddPurchaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Initializing DataBinding
        purchaseBinding = AddPurchaseFragmentBinding.inflate(inflater, container, false)

        val arguments = AddPurchaseFragmentArgs.fromBundle(arguments!!)
        action = arguments.action
        if (action.equals(getString(R.string.update))) {
            purchaseBinding.purchaseSaveButton.visibility = View.GONE
            purchaseBinding.purchaseEditButton.visibility = View.VISIBLE
            purchase = arguments.purchasePost!!
            qty = purchase.quantity!!.toInt()
            purchaseBinding.purchase = arguments.purchasePost
        }

        setHasOptionsMenu(true)

        //Set on click listener to add item text
        purchaseBinding.purchaseAddItemTv.setOnClickListener {
            createItemDialog()
        }
        //Set on click listener to save purchase button
        purchaseBinding.purchaseSaveButton.setOnClickListener {
            val jobNo = purchaseBinding.purchaseJobnoTextInput.text.toString()
            val quotationNo = purchaseBinding.purchaseQuotationTextInput.text.toString()
            val date = purchaseBinding.purchaseDateTextInputLayout.text.toString()
            val supplierName = purchaseBinding.purchaseSupplierTextInputLayout.text.toString()
            val comment = purchaseBinding.purchaseCommentTextInput.text.toString()
            val paymentMethod = purchaseBinding.purchasePaymentMethodTextInput.text.toString()

            when {
                jobNo.isEmpty() -> purchaseBinding.purchaseJobnoTextInputLayout.error =
                    getString(R.string.enter_job_no)
                quotationNo.isEmpty() -> purchaseBinding.purchaseQuotationTextInputLayout.error =
                    getString(R.string.enter_quotation_no)
                (qty == 0) -> Toast.makeText(context, "Quantity can't be zero", Toast.LENGTH_SHORT)
                    .show()
                date.isEmpty() -> purchaseBinding.purchaseDateTextInputLayout.error =
                    getString(R.string.enter_date)
                supplierName.isEmpty() -> purchaseBinding.purchaseSupplierTextInputLayout.error =
                    getString(R.string.enter_supplier_name)
                comment.isEmpty() -> purchaseBinding.purchaseCommentTextInputLayout.error =
                    getString(R.string.enter_comment)
                paymentMethod.isEmpty() -> purchaseBinding.purchasePaymentMethodTextInputLayout.error =
                    getString(
                        R.string.enter_payment_method
                    )
                else -> {
//                    viewModel.addPurchase(
//                    ).observe(viewLifecycleOwner,
//                        Observer {
//                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//                            findNavController().navigate(AddPurchaseFragmentDirections.actionAddPurchaseFragmentToPurchaseFragment())
//                        })
                }
            }
        }

        //Set on click listener to update button
        purchaseBinding.purchaseEditButton.setOnClickListener {
            val jobNo = purchaseBinding.purchaseJobnoTextInput.text.toString()
            val quotationNo = purchaseBinding.purchaseQuotationTextInput.text.toString()
            val date = purchaseBinding.purchaseDateTextInputLayout.text.toString()
            val supplierName = purchaseBinding.purchaseSupplierTextInputLayout.text.toString()
            val comment = purchaseBinding.purchaseCommentTextInput.text.toString()
            val paymentMethod = purchaseBinding.purchasePaymentMethodTextInput.text.toString()

            when {
                jobNo.isEmpty() -> purchaseBinding.purchaseJobnoTextInputLayout.error =
                    getString(R.string.enter_job_no)
                quotationNo.isEmpty() -> purchaseBinding.purchaseQuotationTextInputLayout.error =
                    getString(R.string.enter_quotation_no)
                (qty == 0) -> Toast.makeText(context, "Quantity can't be zero", Toast.LENGTH_SHORT)
                    .show()
                date.isEmpty() -> purchaseBinding.purchaseDateTextInputLayout.error =
                    getString(R.string.enter_date)
                supplierName.isEmpty() -> purchaseBinding.purchaseSupplierTextInputLayout.error =
                    getString(R.string.enter_supplier_name)
                comment.isEmpty() -> purchaseBinding.purchaseCommentTextInputLayout.error =
                    getString(R.string.enter_comment)
                paymentMethod.isEmpty() -> purchaseBinding.purchasePaymentMethodTextInputLayout.error =
                    getString(
                        R.string.enter_payment_method
                    )
                else -> {
                    viewModel.updatePurchase(
                        "AngE9676#254r5",
                        purchase.pid!!.toInt(),
                        jobNo,
                        quotationNo,
                        _vat,
                        date,
                        supplierName,
                        "",
                        "United Kingdom",
                        "",
                        "",
                        comment,
                        "",
                        paymentMethod,
                        qty,
                        _unitAmount,
                        _advanceAmount,
                        _discountAmount,
                        _totalAmount
                    ).observe(viewLifecycleOwner,
                        Observer {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(AddPurchaseFragmentDirections.actionAddPurchaseFragmentToPurchaseFragment())
                        })
                }
            }
        }

        //Adding Text watcher to all input fields
        purchaseBinding.purchaseJobnoTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseJobnoTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseJobnoTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseQuotationTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseQuotationTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseQuotationTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseCommentTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseCommentTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseCommentTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchasePaymentMethodTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchasePaymentMethodTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchasePaymentMethodTextInputLayout.isErrorEnabled = false
            }
        })

        //Set on click listener to customer name tv
        purchaseBinding.purchaseSupplierTextInputLayout.setOnClickListener {

            val searchnameBottomSheet = SearchCustomerBottomSheetFragment(getString(R.string.purchase),nameList,this)
            searchnameBottomSheet.show(activity!!.supportFragmentManager, searchnameBottomSheet.tag)
        }

        val itemRecyclerView = purchaseBinding.supplierItemRecyclerView
        itemAdapter = ItemAdapter()
        itemRecyclerView.adapter = itemAdapter

        return purchaseBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)

        requestReadPermissions()

        viewModel.itemAddToPurchase.observe(viewLifecycleOwner, Observer {

            addedItemList = it
            itemAdapter.submitList(addedItemList)
        })

        //Get list of supplier to get the names
        viewModel.getPurchaseList("AngE9676#254r5").observe(viewLifecycleOwner, Observer {
            it?.let {
                val list = it.posts
                for (item in list){
                    nameList.add(item.customerName!!)
                }
            }
        })
        //Set on click listener to date tv
        purchaseBinding.purchaseDateTextInputLayout.setOnClickListener {
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (action.equals(getString(R.string.update))) {
            inflater.inflate(R.menu.main_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        when (id) {
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
                    viewModel.deletePurchase(purchase.pid!!.toInt()).observe(viewLifecycleOwner,
                        Observer {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(AddPurchaseFragmentDirections.actionAddPurchaseFragmentToPurchaseFragment())
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
                savePdf()            }
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
        val mFileName =
            "jeff_account_." + SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis())
        val filePath = folder.absolutePath + "/" + mFileName + ".pdf"

        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(filePath))
            mDoc.open()
            val lineSeparator = LineSeparator()
            lineSeparator.lineColor = BaseColor.WHITE


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
    private fun createQuotationTable(purchaseBody: Paragraph) {

        val table = PdfPTable(floatArrayOf(5f, 5f))
        table.widthPercentage = 100f
        table.defaultCell.isUseAscender = true

        val cell = PdfPCell(Phrase("Job No"))
        cell.paddingBottom = 8f
        cell.paddingLeft = 8f
        val cell1 = PdfPCell(Phrase(purchase.jobNo))
        cell1.paddingBottom = 8f
        cell1.paddingLeft = 8f
        val qnoCell = PdfPCell(Phrase("Qutation No"))
        qnoCell.paddingBottom = 8f
        qnoCell.paddingLeft = 8f
        val qnoDCell = PdfPCell(Phrase(purchase.quotationNo))
        qnoDCell.paddingBottom = 8f
        qnoDCell.paddingLeft = 8f
        val vatCell = PdfPCell(Phrase("Vat%"))
        vatCell.paddingBottom = 8f
        vatCell.paddingLeft = 8f
        val vatDCell = PdfPCell(Phrase(purchase.vat))
        vatDCell.paddingBottom = 8f
        vatDCell.paddingLeft = 8f
        val dateCell = PdfPCell(Phrase("Date"))
        dateCell.paddingBottom = 8f
        dateCell.paddingLeft = 8f
        val dateDCell = PdfPCell(Phrase(purchase.date))
        dateDCell.paddingBottom = 8f
        dateDCell.paddingLeft = 8f
        val nameCell = PdfPCell(Phrase("Customer Name"))
        nameCell.paddingBottom = 8f
        nameCell.paddingLeft = 8f
        val nameDCell = PdfPCell(Phrase(purchase.customerName))
        nameDCell.paddingBottom = 8f
        nameDCell.paddingLeft = 8f
        val commentCell = PdfPCell(Phrase("Special Instruction"))
        commentCell.paddingBottom = 8f
        commentCell.paddingLeft = 8f
        val commentDCell = PdfPCell(Phrase(purchase.specialInstruction))
        commentDCell.paddingBottom = 8f
        commentDCell.paddingLeft = 8f
        val desCell = PdfPCell(Phrase("Item Description"))
        desCell.paddingBottom = 8f
        desCell.paddingLeft = 8f
        val desDCell = PdfPCell(Phrase(purchase.itemDescription))
        desDCell.paddingBottom = 8f
        desDCell.paddingLeft = 8f
        val paymentCell = PdfPCell(Phrase("Payment Method"))
        paymentCell.paddingBottom = 8f
        paymentCell.paddingLeft = 8f
        val paymentDCell = PdfPCell(Phrase(purchase.paymentMethod))
        paymentDCell.paddingBottom = 8f
        paymentDCell.paddingLeft = 8f
        val qtyCell = PdfPCell(Phrase("Quantity"))
        qtyCell.paddingBottom = 8f
        qtyCell.paddingLeft = 8f
        val qtyDCell = PdfPCell(Phrase(purchase.quantity))
        qtyDCell.paddingBottom = 8f
        qtyDCell.paddingLeft = 8f
        val unitCell = PdfPCell(Phrase("Unit Amount"))
        unitCell.paddingBottom = 8f
        unitCell.paddingLeft = 8f
        val unitDCell = PdfPCell(Phrase(purchase.unitAmount))
        unitDCell.paddingBottom = 8f
        unitDCell.paddingLeft = 8f
        val advanceCell = PdfPCell(Phrase("Advance Amount"))
        advanceCell.paddingBottom = 8f
        advanceCell.paddingLeft = 8f
        val advanceDCell = PdfPCell(Phrase(purchase.advanceAmount))
        advanceDCell.paddingBottom = 8f
        advanceDCell.paddingLeft = 8f
        val discountCell = PdfPCell(Phrase("Discount Amount"))
        discountCell.paddingBottom = 8f
        discountCell.paddingLeft = 8f
        val discountDcell = PdfPCell(Phrase(purchase.discountAmount))
        discountDcell.paddingBottom = 8f
        discountDcell.paddingLeft = 8f
        val totalCell = PdfPCell(Phrase("Total Amount"))
        totalCell.paddingBottom = 8f
        totalCell.paddingLeft = 8f
        val totalDCell = PdfPCell(Phrase(purchase.totalAmount))
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
        purchaseBody.add(table)
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
            dateCell.addElement(Paragraph(purchase.date))
            dateCell.addElement(Paragraph(purchase.quotationNo))
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
        taxDCell.addElement(Paragraph(purchase.vat))
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
        discountDCell.addElement(Paragraph(purchase.discountAmount))
        discountDCell.setPadding(8f)
        table.addCell(discountDCell)
        val totalAmountCell = PdfPCell()
        totalAmountCell.addElement(Paragraph("TOTAL AMOUNT"))
        totalAmountCell.setPadding(8f)
        table.addCell(totalAmountCell)
        val totalAmountDCell = PdfPCell()
        totalAmountDCell.addElement(Paragraph(purchase.totalAmount))
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
        val quantityCell = PdfPCell(Paragraph("Quantity"))
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
        val itemDesCell = PdfPCell(Paragraph(purchase.itemDescription))
        itemDesCell.setPadding(8f)
        table.addCell(itemDesCell)
        val qtyCell = PdfPCell(Paragraph(purchase.quantity))
        qtyCell.setPadding(8f)
        table.addCell(qtyCell)
        val unitDCell = PdfPCell(Paragraph(purchase.unitAmount))
        unitDCell.setPadding(8f)
        table.addCell(unitDCell)
        val disDCell = PdfPCell(Paragraph(purchase.discountAmount))
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
        nameDataCell.addElement(Paragraph(purchase.customerName))
        nameDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val addressCell = PdfPCell()
        addressCell.border = PdfPCell.NO_BORDER
        addressCell.addElement(Paragraph("Street Address"))
        val addressDataCell = PdfPCell()
        addressDataCell.border = PdfPCell.NO_BORDER
        addressDataCell.addElement(Paragraph(purchase.street))
        addressDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val countryCell = PdfPCell()
        countryCell.border = PdfPCell.NO_BORDER
        countryCell.addElement(Paragraph("Country"))
        val countrydataCell = PdfPCell()
        countrydataCell.border = PdfPCell.NO_BORDER
        countrydataCell.addElement(Paragraph(purchase.country))
        countrydataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val postCell = PdfPCell()
        postCell.border = PdfPCell.NO_BORDER
        postCell.addElement(Paragraph("Post Code"))
        val postDataCell = PdfPCell()
        postDataCell.border = PdfPCell.NO_BORDER
        postDataCell.addElement(Paragraph(purchase.postCode))
        postDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val teleCell = PdfPCell()
        teleCell.border = PdfPCell.NO_BORDER
        teleCell.addElement(Paragraph("Telephone No"))
        val teleDataCell = PdfPCell()
        teleDataCell.border = PdfPCell.NO_BORDER
        teleDataCell.addElement(Paragraph(purchase.telephone))
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
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        purchaseBinding.purchaseDateTextInputLayout.text =
            viewModel.changeDateFormat(dayOfMonth, monthOfYear, year)
    }

    private fun createItemDialog() {

        val layout = LayoutInflater.from(context).inflate(R.layout.fragment_add_item, null)
        val builder = context.let { androidx.appcompat.app.AlertDialog.Builder(it!!) }
        builder.setView(layout)
        val dialog = builder.create()
        dialog.show()

        val itemDestv: TextView = layout.findViewById(R.id.item_des_editText)
        val qtytv: TextView = layout.findViewById(R.id.item_qty_editText)
        val unitAmountTv: TextView = layout.findViewById(R.id.item_unitamount_editText)
        val discountAmountTv: TextView = layout.findViewById(R.id.discount_amount_editText)
        val totalAmountTv: TextView = layout.findViewById(R.id.total_editText)
        val vatTv: TextView = layout.findViewById(R.id.vat_editText)
        val addButton: Button = layout.findViewById(R.id.add_item_button)
        val negButton: ImageButton = layout.findViewById(R.id.neg_qty_button)
        val posButton: ImageButton = layout.findViewById(R.id.pos_qty_button)

        var qty = 0
        posButton.setOnClickListener {
            if (qty >= 0) {
                qty++
                qtytv.setText(qty.toString())
            }
        }
        negButton.setOnClickListener {
            if (qty > 0) {
                qty--
                qtytv.setText(qty.toString())
            }
        }
        addButton.setOnClickListener {
            val itemDes = itemDestv.text.toString()
            val qty = qtytv.text.toString()
            val unitAmount = unitAmountTv.text.toString()
            val discountAmount = discountAmountTv.text.toString()
            val totalAmount = totalAmountTv.text.toString()
            val vat = vatTv.text.toString()
            when {
                itemDes.isEmpty() -> Toast.makeText(
                    context,
                    "Add item description",
                    Toast.LENGTH_SHORT
                ).show()
                qty.isEmpty() -> Toast.makeText(context, "Add Quantity", Toast.LENGTH_SHORT).show()
                unitAmount.isEmpty() -> Toast.makeText(
                    context,
                    "Enter Unit Amount",
                    Toast.LENGTH_SHORT
                ).show()
                discountAmount.isEmpty() -> Toast.makeText(
                    context,
                    "Enter Discount Amount",
                    Toast.LENGTH_SHORT
                ).show()
                totalAmount.isEmpty() -> Toast.makeText(
                    context,
                    "Enter Total Amount",
                    Toast.LENGTH_SHORT
                ).show()
                vat.isEmpty() -> Toast.makeText(context, "Enter vat Amount", Toast.LENGTH_SHORT)
                    .show()
                else -> {
                    val item = Item(
                        itemNo,
                        itemDes,
                        qty.toInt(),
                        unitAmount.toDouble(),
                        discountAmount.toDouble(),
                        totalAmount.toDouble(),
                        vat.toDouble()
                    )
                    itemNo++
                    itemList.add(item)
                    Timber.e("List size is ${itemList.size}")
                    purchaseBinding.purchaseAddItemTv.setText("No. of items: ${itemList.size}")
                    viewModel.addItemToPurchase(itemList)
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {

    }
}
