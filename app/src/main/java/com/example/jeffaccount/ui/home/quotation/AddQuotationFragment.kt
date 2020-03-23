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
import com.example.jeffaccount.network.*
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
import kotlin.math.roundToLong


class AddQuotationFragment : Fragment(),
    DatePickerDialog.OnDateSetListener,OnSearchItemClickListener,OnSearchSupplierClickListener {

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
    private var nameList:MutableList<String> = mutableListOf()
    private var addedItemList:MutableList<Item> = mutableListOf()
    private lateinit var street:String
    private lateinit var country:String
    private lateinit var telephone:String
    private lateinit var postCode:String
    private var itemNo: Int = 1
    private var singleItemQty = 0

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
            val customerName = quotationBinding.quotationCustomerNameTextInputLayout.text.toString()
            val comment = quotationBinding.quotationCommentTextInput.text.toString()
            val paymentMethod = quotationBinding.quotationPayementMethodTextInput.text.toString()
            street = quotationBinding.addQuotationStreetTv.text.toString()
            country = quotationBinding.addQuotaitonCountryTv.text.toString()
            postCode = quotationBinding.addQuotationPostcodeTv.text.toString()
            telephone = quotationBinding.addQuotationTelephoneTv.text.toString()

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
                        "AngE9676#254r5", jobNo, quotationNo, customerName, date, street,
                        country, postCode, telephone,paymentMethod,comment, itemList
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
            val customerName = quotationBinding.quotationCustomerNameTextInputLayout.text.toString()
            val comment = quotationBinding.quotationCommentTextInput.text.toString()
            val paymentMethod = quotationBinding.quotationPayementMethodTextInput.text.toString()
            street = quotationBinding.addQuotationStreetTv.text.toString()
            country = quotationBinding.addQuotaitonCountryTv.text.toString()
            postCode = quotationBinding.addQuotationPostcodeTv.text.toString()
            telephone = quotationBinding.addQuotationTelephoneTv.text.toString()

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
                        street,
                        country,
                        postCode,
                        telephone,
                        comment,
                        paymentMethod,
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
        }

        quotationBinding.quotationCustomerNameTextInputLayout.setOnClickListener {
            val searchnameFragment = SearchCustomerBottomSheetFragment(getString(R.string.quotation),nameList,this,this)
            searchnameFragment.show(activity!!.supportFragmentManager,searchnameFragment.tag)
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
        itemAdapter = ItemAdapter(OnAddedItemClickListener {
            val item = it
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
                addedItemList.remove(item)
                viewModel.removeItem(addedItemList)
                dialog?.dismiss()
            }
            canButton.setOnClickListener {
                dialog?.dismiss()
            }
        })
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
            quotationBinding.custAddtionalGroup.visibility = View.VISIBLE
            quotationBinding.quotationAddItemTv.setText("No. of items: ${itemList.size}")
        }

        viewModel.dateString.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e(it)
                quotationBinding.quotationDateTextInputLayout.setText(it)
            }
        })

        viewModel.itemChangedToQuotation.observe(viewLifecycleOwner, Observer {
            it?.let {
                addedItemList = it
                itemAdapter.submitList(addedItemList)
                itemAdapter.notifyDataSetChanged()
            }
        })
        viewModel.getDate()

        //Get Company list
        viewModel.getCustomerList().observe(viewLifecycleOwner, Observer {
            it?.let {
                val comList = it.posts
                for (item in comList){
                    nameList.add(item.custname!!)
                }
                Timber.e("Name list size: ${nameList.size}, Com list size: ${comList.size}")
            }
        })
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

    //Save pdf
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
            dateCell.addElement(Paragraph("Date: $quotationItem.date"))
            dateCell.addElement(Paragraph("Quotation No. : $quotationItem.quotationNo"))
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
        var subTotal = 0.0
        for (item in addedItemList){
            subTotal += item.unitAmount!!
        }
        subTotalDCell.addElement(Paragraph(subTotal.toString()))
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
        var totalDiscount = 0.0
        for (item in addedItemList){
            totalDiscount += item.discountAmount!!
        }
        discountDCell.addElement(Paragraph(totalDiscount.toString()))
        discountDCell.setPadding(8f)
        table.addCell(discountDCell)
        val totalAmountCell = PdfPCell()
        totalAmountCell.addElement(Paragraph("TOTAL AMOUNT"))
        totalAmountCell.setPadding(8f)
        table.addCell(totalAmountCell)
        val totalAmountDCell = PdfPCell()
        val totalAmount = subTotal.minus(totalDiscount).roundToLong()
        totalAmountDCell.addElement(Paragraph(totalAmount.toString()))
        totalAmountDCell.setPadding(8f)
        table.addCell(totalAmountDCell)
        return table
    }

    private fun createInvoiceTable(): PdfPTable {
        val table = PdfPTable(5)
        table.setWidths(intArrayOf(1, 4, 1, 2, 2))
        val jobNoCell = PdfPCell(Paragraph("No."))
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

        for(item in addedItemList){
            val itemNoCell = PdfPCell(Paragraph(item.noOfItem.toString()))
            itemNoCell.setPadding(8f)
            val itemDesCell = PdfPCell(Paragraph(item.itemDes))
            itemDesCell.setPadding(8f)
            val qtyCell = PdfPCell(Paragraph(item.qty.toString()))
            qtyCell.setPadding(8f)
            val unitDCell = PdfPCell(Paragraph(item.unitAmount.toString()))
            unitDCell.setPadding(8f)
            val disDCell = PdfPCell(Paragraph(item.discountAmount.toString()))
            disDCell.setPadding(8f)
            table.addCell(itemNoCell)
            table.addCell(itemDesCell)
            table.addCell(qtyCell)
            table.addCell(unitDCell)
            table.addCell(disDCell)
        }
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

        singleItemQty = when {
            qtytv.text.isEmpty() -> {
                0
            }
            else -> {
                qtytv.text.toString().toInt()
            }
        }
        posButton.setOnClickListener {
            if (singleItemQty >= 0) {
                singleItemQty++
                qtytv.setText(singleItemQty.toString())
            }
        }
        negButton.setOnClickListener {
            if (singleItemQty > 0) {
                singleItemQty--
                qtytv.setText(singleItemQty.toString())
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

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {
        searchCustomer?.let {
            quotationBinding.custAddtionalGroup.visibility = View.VISIBLE
            quotationBinding.quotationCustomerNameTextInputLayout.text = searchCustomer.custname
            quotationBinding.addQuotaitonCountryTv.text = searchCustomer.country
            quotationBinding.addQuotationStreetTv.text = searchCustomer.street
            quotationBinding.addQuotationPostcodeTv.text = searchCustomer.telephone
            quotationBinding.addQuotationTelephoneTv.text = searchCustomer.postcode

        }
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost) {

    }
}
