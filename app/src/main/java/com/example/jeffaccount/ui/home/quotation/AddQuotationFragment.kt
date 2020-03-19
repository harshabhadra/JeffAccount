package com.example.jeffaccount.ui.home.quotation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddQuotationFragmentBinding
import com.example.jeffaccount.model.QuotationPost
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.network.QuotationAdd
import com.example.jeffaccount.network.QuotationUpdate
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


class AddQuotationFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var filePath: String
//    private var apiKey = getString(R.string.api_key)

    companion object {
        fun newInstance() =
            AddQuotationFragment()
    }

    private var loadingDialog: AlertDialog? = null
    private lateinit var viewModel: AddQuotationViewModel
    private lateinit var quotationBinding: AddQuotationFragmentBinding
    private lateinit var quotationItem: QuotationPost
    private lateinit var action: String
    private lateinit var itemAdapter: ItemAdapter
    private var itemList: MutableList<Item> = mutableListOf()
    private var itemNo: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.e("on create")
        quotationBinding = AddQuotationFragmentBinding.inflate(inflater, container, false)

        quotationBinding.saveQuotationButton.setOnClickListener {
            findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
        }

        setHasOptionsMenu(true)

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
            val date = quotationBinding.quotationDateTextInputLayout.text.toString()
            val customerName = quotationBinding.quotationCustomerNameTextInput.text.toString()
            val comment = quotationBinding.quotationCommentTextInput.text.toString()
            val paymentMethod = quotationBinding.quotationPayementMethodTextInput.text.toString()

            when {
                jobNo.isEmpty() -> {
                    quotationBinding.quotationJobTextInputLayout.error =
                        getString(R.string.enter_job_no)
                }

                quotationNo.isEmpty() -> {
                    quotationBinding.quotationQuotationoTextInputLayout.error =
                        getString(R.string.enter_quotation_no)
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
                paymentMethod.isEmpty() -> {
                    quotationBinding.quotationPayementMethodTextInputLayout.error =
                        "Select a payment method"
                }
                else -> {
                    val quotation = QuotationAdd(
                        "AngE9676#254r5", jobNo, quotationNo, customerName, date, "kolkata",
                        "United Kingdom", "78536", "89657421", itemList
                    )
                    viewModel.addQuotaiton(quotation).observe(viewLifecycleOwner,
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
            val date = quotationBinding.quotationDateTextInputLayout.text.toString()
            val customerName = quotationBinding.quotationCustomerNameTextInput.text.toString()
            val comment = quotationBinding.quotationCommentTextInput.text.toString()
            val paymentMethod = quotationBinding.quotationPayementMethodTextInput.text.toString()

            when {
                jobNo.isEmpty() -> {
                    quotationBinding.quotationJobTextInputLayout.error =
                        getString(R.string.enter_job_no)
                }

                quotationNo.isEmpty() -> {
                    quotationBinding.quotationQuotationoTextInputLayout.error =
                        getString(R.string.enter_quotation_no)
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

                paymentMethod.isEmpty() -> {
                    quotationBinding.quotationPayementMethodTextInputLayout.error =
                        "Select a payment method"
                }
                else -> {
                    val quotaionUpdate = QuotationUpdate(
                        quotationItem.qid,
                        "AngE9676#254r5",
                        jobNo,
                        quotationNo,
                        customerName,
                        date,
                        "Kolkata",
                        "Unied Kingdom",
                        "735226",
                        "789657400",
                        itemList
                    )
                    viewModel.updateQuotation(quotaionUpdate).observe(viewLifecycleOwner,
                        Observer {
                            it?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
                            }
                        })
                }
            }
        }

        //Set on click listener to add item text view
        quotationBinding.quotationAddItemTv.setOnClickListener {
            createItemDialog()
//            findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToAddItemFragment(
//                item
//            ))
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

        //Setting up item recycler view
        val itemRecycler = quotationBinding.quotationItemRecyclerView
        itemAdapter = ItemAdapter()
        itemRecycler.adapter = itemAdapter
        return quotationBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requestReadPermissions()
        viewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)
        //Taking arguments from Quotation Fragment
        val arguments = AddQuotationFragmentArgs.fromBundle(arguments!!)
        action = arguments.actionQuotation

        if (action.equals(getString(R.string.update))) {
            quotationItem = arguments.quotationItem!!
            quotationBinding.quotation = quotationItem
            quotationBinding.supplierUpdateButton.visibility = View.VISIBLE
            quotationBinding.saveQuotationButton.visibility = View.GONE
            itemList = quotationItem.itemDescription
            viewModel.addItemToQuotation(itemList)
            itemNo = itemList.size.plus(1)
            quotationBinding.quotationAddItemTv.setText("No. of items: ${itemList.size}")
        }

        viewModel.dateString.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e(it)
                quotationBinding.quotationDateTextInputLayout.setText(it)
            }
        })

        viewModel.itemAddedToQuotation.observe(viewLifecycleOwner, Observer {
            it?.let {
                itemAdapter.submitList(it)
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
                    viewModel.deleteQuotaton(quotationItem.qid!!.toInt())
                        .observe(viewLifecycleOwner,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.data?.also {
                Toast.makeText(context, "Pdf saved at ${it.toString()}", Toast.LENGTH_SHORT).show()
            }
        }
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
        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(filePath))
            mDoc.open()
            val lineSeparator = LineSeparator()
            lineSeparator.lineColor = BaseColor.WHITE
            val jeffChunk = Chunk(
                getString(R.string.app_name), Font(Font.FontFamily.TIMES_ROMAN, 32.0f)
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
            dateCell.addElement(Paragraph(quotationItem.date))
            dateCell.addElement(Paragraph(quotationItem.quotationNo))
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
                loadingDialog?.dismiss()
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
//        taxDCell.addElement(Paragraph(quotationItem.vat))
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
//        discountDCell.addElement(Paragraph(quotationItem.discountAmount))
        discountDCell.setPadding(8f)
        table.addCell(discountDCell)
        val totalAmountCell = PdfPCell()
        totalAmountCell.addElement(Paragraph("TOTAL AMOUNT"))
        totalAmountCell.setPadding(8f)
        table.addCell(totalAmountCell)
        val totalAmountDCell = PdfPCell()
//        totalAmountDCell.addElement(Paragraph(quotationItem.totalAmount))
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
//        val itemDesCell = PdfPCell(Paragraph(quotationItem.itemDescription))
//        itemDesCell.setPadding(8f)
//        table.addCell(itemDesCell)
//        val qtyCell = PdfPCell(Paragraph(quotationItem.quantity))
//        qtyCell.setPadding(8f)
//        table.addCell(qtyCell)
//        val unitDCell = PdfPCell(Paragraph(quotationItem.unitAmount))
//        unitDCell.setPadding(8f)
//        table.addCell(unitDCell)
//        val disDCell = PdfPCell(Paragraph(quotationItem.discountAmount))
//        disDCell.setPadding(8f)
//        table.addCell(disDCell)
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
        nameDataCell.addElement(Paragraph(quotationItem.customerName))
        nameDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val addressCell = PdfPCell()
        addressCell.border = PdfPCell.NO_BORDER
        addressCell.addElement(Paragraph("Street Address"))
        val addressDataCell = PdfPCell()
        addressDataCell.border = PdfPCell.NO_BORDER
        addressDataCell.addElement(Paragraph(quotationItem.street))
        addressDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val countryCell = PdfPCell()
        countryCell.border = PdfPCell.NO_BORDER
        countryCell.addElement(Paragraph("Country"))
        val countrydataCell = PdfPCell()
        countrydataCell.border = PdfPCell.NO_BORDER
        countrydataCell.addElement(Paragraph(quotationItem.country))
        countrydataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val postCell = PdfPCell()
        postCell.border = PdfPCell.NO_BORDER
        postCell.addElement(Paragraph("Post Code"))
        val postDataCell = PdfPCell()
        postDataCell.border = PdfPCell.NO_BORDER
        postDataCell.addElement(Paragraph(quotationItem.postCode))
        postDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val teleCell = PdfPCell()
        teleCell.border = PdfPCell.NO_BORDER
        teleCell.addElement(Paragraph("Telephone No"))
        val teleDataCell = PdfPCell()
        teleDataCell.border = PdfPCell.NO_BORDER
        teleDataCell.addElement(Paragraph(quotationItem.telephone))
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

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, 2)
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
//        val vatDCell = PdfPCell(Phrase(quotationItem.vat))
//        vatDCell.paddingBottom = 8f
//        vatDCell.paddingLeft = 8f
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
//        val commentDCell = PdfPCell(Phrase(quotationItem.specialInstruction))
//        commentDCell.paddingBottom = 8f
//        commentDCell.paddingLeft = 8f
        val desCell = PdfPCell(Phrase("Item Description"))
        desCell.paddingBottom = 8f
        desCell.paddingLeft = 8f
//        val desDCell = PdfPCell(Phrase(quotationItem.itemDescription))
//        desDCell.paddingBottom = 8f
//        desDCell.paddingLeft = 8f
        val paymentCell = PdfPCell(Phrase("Payment Method"))
        paymentCell.paddingBottom = 8f
        paymentCell.paddingLeft = 8f
//        val paymentDCell = PdfPCell(Phrase(quotationItem.paymentMethod))
//        paymentDCell.paddingBottom = 8f
//        paymentDCell.paddingLeft = 8f
//        val qtyCell = PdfPCell(Phrase("Quantity"))
//        qtyCell.paddingBottom = 8f
//        qtyCell.paddingLeft = 8f
//        val qtyDCell = PdfPCell(Phrase(quotationItem.quantity))
//        qtyDCell.paddingBottom = 8f
//        qtyDCell.paddingLeft = 8f
//        val unitCell = PdfPCell(Phrase("Unit Amount"))
//        unitCell.paddingBottom = 8f
//        unitCell.paddingLeft = 8f
//        val unitDCell = PdfPCell(Phrase(quotationItem.unitAmount))
//        unitDCell.paddingBottom = 8f
//        unitDCell.paddingLeft = 8f
//        val advanceCell = PdfPCell(Phrase("Advance Amount"))
//        advanceCell.paddingBottom = 8f
//        advanceCell.paddingLeft = 8f
//        val advanceDCell = PdfPCell(Phrase(quotationItem.advanceAmount))
//        advanceDCell.paddingBottom = 8f
//        advanceDCell.paddingLeft = 8f
//        val discountCell = PdfPCell(Phrase("Discount Amount"))
//        discountCell.paddingBottom = 8f
//        discountCell.paddingLeft = 8f
//        val discountDcell = PdfPCell(Phrase(quotationItem.discountAmount))
//        discountDcell.paddingBottom = 8f
//        discountDcell.paddingLeft = 8f
//        val totalCell = PdfPCell(Phrase("Total Amount"))
//        totalCell.paddingBottom = 8f
//        totalCell.paddingLeft = 8f
//        val totalDCell = PdfPCell(Phrase(quotationItem.totalAmount))
//        totalDCell.paddingBottom = 8f
//        totalDCell.paddingLeft = 8f
        table.addCell(cell)
        table.addCell(cell1)
        table.addCell(qnoCell)
        table.addCell(qnoDCell)
        table.addCell(vatCell)
//        table.addCell(vatDCell)
        table.addCell(dateCell)
        table.addCell(dateDCell)
        table.addCell(nameCell)
        table.addCell(nameDCell)
        table.addCell(commentCell)
//        table.addCell(commentDCell)
        table.addCell(desCell)
//        table.addCell(desDCell)
        table.addCell(paymentCell)
//        table.addCell(paymentDCell)
//        table.addCell(qtyCell)
//        table.addCell(qtyDCell)
//        table.addCell(unitCell)
//        table.addCell(unitDCell)
//        table.addCell(advanceCell)
//        table.addCell(advanceDCell)
//        table.addCell(discountCell)
//        table.addCell(discountDcell)
//        table.addCell(totalCell)
//        table.addCell(totalDCell)
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
        quotationBinding.quotationDateTextInputLayout.setText(
            viewModel.changeDateFormat(
                dayOfMonth,
                monthOfYear,
                year
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.e("on save instance State")
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
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
                    quotationBinding.quotationAddItemTv.setText("No. of items: ${itemList.size}")
                    viewModel.addItemToQuotation(itemList)
                    dialog.dismiss()
                }
            }
        }
    }
}
