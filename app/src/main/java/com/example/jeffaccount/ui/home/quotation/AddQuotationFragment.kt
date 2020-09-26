package com.example.jeffaccount.ui.home.quotation

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddQuotationFragmentBinding
import com.example.jeffaccount.model.*
import com.example.jeffaccount.network.*
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.utils.ItemMoveCallbackListener
import com.example.jeffaccount.utils.createPreviewDialog
import com.google.android.material.button.MaterialButton
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
import kotlinx.android.synthetic.main.add_quotation_fragment.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong


class AddQuotationFragment : Fragment(),
    DatePickerDialog.OnDateSetListener, OnSearchItemClickListener, OnSearchSupplierClickListener,
    OnCustomerNameClickListener, OnSupplierNameClickListener, OnQuotationJobNoClickListener,
    OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener,
    AdapterView.OnItemSelectedListener, OnAddedItemClickListener, OnStartDragListener {

    private lateinit var filePath: String
    private lateinit var touchHelper: ItemTouchHelper
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
    private var nameList: MutableList<String> = mutableListOf()
    private var addedItemList: MutableList<Item> = mutableListOf()
    private lateinit var street: String
    private lateinit var country: String
    private lateinit var telephone: String
    private lateinit var postCode: String
    private var itemNo: Int = 1
    private var singleItemQty = 0
    private lateinit var customer: Post
    private var vat: Double = 0.0
    private var company: ComPost? = null
    private lateinit var comid: String
    private lateinit var companyDetails: CompanyDetails
    private var logoList: MutableList<Logo> = mutableListOf()
    private var bmpList: MutableList<Bitmap> = mutableListOf()
    private lateinit var companyBitmap: Bitmap
    private lateinit var unit: String
    private var unitPostion: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)
        quotationBinding = AddQuotationFragmentBinding.inflate(inflater, container, false)

        val activity = activity as MainActivity
        activity.setToolbarText("Add Quotation")

        companyDetails = activity.companyDetails

        comid = activity.companyDetails.comid

        //Taking arguments from Quotation Fragment
        val arguments = AddQuotationFragmentArgs.fromBundle(arguments!!)
        action = arguments.actionQuotation

        if (action.equals(getString(R.string.update))) {
            activity.setToolbarText("Update Quotation")
            quotationItem = arguments.quotationItem!!
            quotationBinding.quotation = quotationItem
            quotationBinding.quotationJobTextInput.setText(quotationItem.jobNo)
            quotationBinding.quotationQuotationoTextInput.setText(quotationItem.quotationNo)
            quotationBinding.supplierUpdateButton.visibility = View.VISIBLE
            quotationBinding.saveQuotationButton.visibility = View.GONE
            itemList = quotationItem.itemDescription
            Timber.e("Intent item list size: ${itemList.size}")
            viewModel.addItemToQuotation(itemList)
            itemNo = itemList.size.plus(1)
            quotationBinding.quotationCustomerNameTextInputLayout.text = quotationItem.customerName
            quotationBinding.addQuotationStreetTv.text = quotationItem.street
            quotationBinding.addQuotaitonCountryTv.text = quotationItem.country
            quotationBinding.addQuotationPostcodeTv.text = quotationItem.postCode
            quotationBinding.addQuotationTelephoneTv.text = quotationItem.telephone
            quotationBinding.custAddtionalGroup.visibility = View.VISIBLE
            quotationBinding.quotationAddItemTv.setText("No. of items: ${itemList.size}")
        } else if (action.equals(getString(R.string.customer_data))) {
            customer = arguments.customerItem!!
            Timber.e("customer data ${customer.custname}")
            quotationBinding.quotationCustomerNameTextInputLayout.text = customer.custname
            quotationBinding.addQuotationStreetTv.text = customer.street
            quotationBinding.addQuotaitonCountryTv.text = customer.country
            quotationBinding.addQuotationPostcodeTv.text = customer.postcode
            quotationBinding.addQuotationTelephoneTv.text = customer.telephone
            quotationBinding.custAddtionalGroup.visibility = View.VISIBLE
            Timber.e("customer data after set ${quotationBinding.quotationCustomerNameTextInputLayout.text}")
            Toast.makeText(
                context,
                "Name is : ${quotationBinding.quotationCustomerNameTextInputLayout.text}",
                Toast.LENGTH_SHORT
            ).show()
        } else if (action.equals(getString(R.string.purchase)) || action.equals(getString(R.string.time_sheet))) {
            quotationBinding.quotationJobTextInput.setText(arguments.jobno!!)
            quotationBinding.quotationQuotationoTextInput.setText(arguments.quotationno!!)
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
            val vatPercentage = quotationBinding.quotationVatTextInput.text.toString()
            street = quotationBinding.addQuotationStreetTv.text.toString()
            country = quotationBinding.addQuotaitonCountryTv.text.toString()
            postCode = quotationBinding.addQuotationPostcodeTv.text.toString()
            telephone = quotationBinding.addQuotationTelephoneTv.text.toString()
            if (vatPercentage.isNotEmpty()) {
                vat = vatPercentage.toDouble()
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
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Save Quotation?")
                    builder.setPositiveButton(
                        "Save",
                        DialogInterface.OnClickListener { dialog, which ->
                            val quotation = QuotationAdd(
                                comid,
                                "AngE9676#254r5",
                                jobNo,
                                quotationNo,
                                customerName,
                                date,
                                street,
                                country,
                                postCode,
                                telephone,
                                paymentMethod,
                                comment,
                                vat,
                                itemList
                            )
                            viewModel.addQuotaiton(quotation).observe(viewLifecycleOwner,
                                Observer {
                                    it?.let {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
                                    }
                                })
                            dialog.dismiss()
                        })
                    builder.setNegativeButton(
                        "Cancel",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()

                        })
                    val dialog = builder.create()
                    dialog.show()

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
            val vatPercentage = quotationBinding.quotationVatTextInput.text.toString()
            street = quotationBinding.addQuotationStreetTv.text.toString()
            country = quotationBinding.addQuotaitonCountryTv.text.toString()
            postCode = quotationBinding.addQuotationPostcodeTv.text.toString()
            telephone = quotationBinding.addQuotationTelephoneTv.text.toString()
            if (vatPercentage.isNotEmpty()) {
                vat = vatPercentage.toDouble()
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
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Update Quotation?")
                    builder.setPositiveButton(
                        "Update",
                        DialogInterface.OnClickListener { dialog, which ->
                            val quotaionUpdate = QuotationUpdate(
                                comid,
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
                                paymentMethod,
                                comment,
                                vat,
                                itemList
                            )
                            viewModel.updateQuotation(quotaionUpdate).observe(viewLifecycleOwner,
                                Observer {
                                    it?.let {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
                                    }
                                })
                            dialog.dismiss()
                        })
                    builder.setNegativeButton(
                        "Cancel",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()

                        })
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }

        //Set on click listener to add item text view
        quotationBinding.quotationAddItemTv.setOnClickListener {
            createItemDialog(null, null)
        }

        quotationBinding.quotationCustomerNameTextInputLayout.setOnClickListener {
            val searchnameFragment = SearchCustomerBottomSheetFragment(
                getString(R.string.quotation),
                nameList,
                this,
                this,
                this, this,
                this, this,
                this, this
            )
            searchnameFragment.show(activity!!.supportFragmentManager, searchnameFragment.tag)
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

        //Add text watcher to vat percentage text input
        quotationBinding.quotationVatTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotation_vat_textInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotation_vat_textInputLayout.isErrorEnabled = false
            }
        })

        //Setting up item recycler view
        val itemRecycler = quotationBinding.quotationItemRecyclerView
        itemAdapter = ItemAdapter(this, this)
        val callback: ItemTouchHelper.Callback = ItemMoveCallbackListener(itemAdapter)

        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(itemRecycler)
        itemRecycler.adapter = itemAdapter

        //Observe customer data
        viewModel.customerData.observe(viewLifecycleOwner, Observer {
            it?.let {

            } ?: let {
                Timber.e("viewmodel customer data empty")
            }
        })

        return quotationBinding.root
    }

    private fun createChoiceDialog(item: Item, position: Int) {
        val layout = LayoutInflater.from(context).inflate(R.layout.choose_layout, null)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(layout)
        val dialog = builder.create()
        dialog.show()
        val editButton: MaterialButton = layout.findViewById(R.id.choice_edit_button)
        val deleteButton: MaterialButton = layout.findViewById(R.id.choice_delete_button)
        deleteButton.setOnClickListener {
            addedItemList.remove(item)
            viewModel.addItemToQuotation(addedItemList)
            dialog.dismiss()
        }
        editButton.setOnClickListener {
            dialog.dismiss()
            createItemDialog(item, position)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requestReadPermissions()

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
        viewModel.getCustomerList(comid).observe(viewLifecycleOwner, Observer {
            it?.let {
                val comList = it.posts
                for (item in comList) {
                    nameList.add(item.custname!!)
                }
                Timber.e("Name list size: ${nameList.size}, Com list size: ${comList.size}")
            }
        })


        viewModel.getLogoList(comid).observe(viewLifecycleOwner, Observer {
            it.logoList?.let {
                logoList.addAll(it)
                if (logoList.isNotEmpty()) {
                    for (logo in logoList) {
                        val imgUrl =
                            "https://alphabusinessdesigns.com/wordpress/appproject/jtapp/${logo.fileName}"
                        Timber.e("Image url is:$imgUrl")
                        viewModel.getBitmapFromUrl(imgUrl)
                    }
                }
            }
        })

        //Observe to get logo bitmap list
        viewModel.imageBitmap.observe(viewLifecycleOwner, Observer {
            it?.let {

                bmpList.add(it)
                Timber.e("Bitmap list size: ${bmpList.size}")
            }
        })

        companyDetails.caomimge?.let {
            viewModel.getCompanyBitmap("https://alphabusinessdesigns.com/wordpress/appproject/jtapp/$it")
        }

        //Observe to get company logo bitmap
        viewModel.companyBitmap.observe(viewLifecycleOwner, Observer {
            it?.let {
                companyBitmap = it
                Timber.e("Company bitmap size: ${companyBitmap.height}, ${companyBitmap.width}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (action.equals(getString(R.string.update))) {
            inflater.inflate(R.menu.quotation_menu, menu)
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
            R.id.add_to_invoice -> {
                findNavController().navigate(
                    AddQuotationFragmentDirections.actionAddQuotationFragmentToAddInvoiceFragment(
                        getString(R.string.quotation),
                        null,
                        null,
                        quotationItem
                        , null, null
                    )
                )
            }
            R.id.purchase_action -> {
                findNavController().navigate(
                    AddQuotationFragmentDirections.actionAddQuotationFragmentToAddPurchaseFragment(
                        null,
                        getString(R.string.quotation),
                        null,
                        null,
                        quotationItem.jobNo,
                        quotationItem.quotationNo
                    )
                )
            }
            R.id.action_timeSheet -> {
                findNavController().navigate(
                    AddQuotationFragmentDirections.actionAddQuotationFragmentToAddTimeSheetFragment(
                        null,
                        getString(R.string.quotation),
                        quotationItem.jobNo,
                        quotationItem.quotationNo
                    )
                )
            }
            R.id.worksheet_action -> {
                findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToCreateWorkSheetFragment())
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
            val cBitmap = BITMAP_RESIZER(companyBitmap, 80, 80)
            val stream = ByteArrayOutputStream()
            cBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = com.itextpdf.text.Image.getInstance(stream.toByteArray())
            doc.add(image)
            val headTable = PdfPTable(2)
            headTable.setWidths(intArrayOf(4, 2))
            headTable.widthPercentage = 100f
            val addressCell = getAddressTable()
            val dateCell = PdfPCell()
            dateCell.border = PdfPCell.NO_BORDER
            dateCell.addElement(Paragraph("Date: ${quotationItem.date}"))
            dateCell.addElement(Paragraph("Job No. : ${quotationItem.jobNo}"))
            dateCell.addElement(Paragraph("Quotation No. : ${quotationItem.quotationNo}"))
            dateCell.horizontalAlignment = Element.ALIGN_RIGHT
            headTable.addCell(addressCell)
            headTable.addCell(dateCell)
            doc.add(headTable)
            doc.add(Paragraph(" "))
            val customerDetailsTitle =
                Paragraph("Customer Details", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            customerDetailsTitle.alignment = Element.ALIGN_CENTER
            doc.add(customerDetailsTitle)
            doc.add(Paragraph(""))
            val detailsTable = populateDetailsTable()
            doc.add(detailsTable)
            doc.add(Paragraph(" "))
            doc.add(
                Paragraph(
                    "Click the link below to visit our website", Font(
                        Font.FontFamily.COURIER, 10f, Font.BOLD,
                        BaseColor.RED
                    )
                )
            )
            doc.add(Paragraph(" "))
            val web = companyDetails.web
            if (web.isNotEmpty()) {
                val chunk = Chunk(web)
                chunk.setAnchor(URL(web))
                doc.add(Paragraph(chunk))
            }
            doc.add(Paragraph(" "))
            val invoiceTitle =
                Paragraph("QUOTATION", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            invoiceTitle.alignment = Element.ALIGN_CENTER
            val invoiceTable = createInvoiceTable()
            doc.add(invoiceTitle)
            doc.add(Paragraph(" "))
            doc.add(invoiceTable)
            doc.add(Paragraph(" "))
            doc.add(Paragraph("Payment Method: ${quotationItem.paymentMethod}"))
            doc.add(Paragraph(" "))
            doc.add(Paragraph("Special instruction"))
            doc.add(Paragraph(quotationItem.comment))
            doc.add(Paragraph(" "))
            val totalTable = createTotalTable()
            doc.add(totalTable)
            doc.add(Paragraph(" "))
            doc.add(Paragraph(companyDetails.comDesription))
            doc.add(
                Paragraph(
                    "${getString(R.string.jeff_inquiry_message)} ${companyDetails.web}",
                    Font(Font.FontFamily.UNDEFINED, 10f, Font.BOLD, BaseColor.RED)
                )
            )

            doc.add(Paragraph(" "))
            for (bitmap in bmpList) {
                try {
                    Timber.e("Bitmap size: ${bitmap.width}, ${bitmap.height}")
                    val btm = BITMAP_RESIZER(bitmap, 60, 60)
                    Timber.e(bitmap.toString())
                    val iStream = ByteArrayOutputStream()
                    btm?.compress(Bitmap.CompressFormat.PNG, 100, iStream)
                    val logoImg = Image.getInstance(iStream.toByteArray())
                    doc.add(logoImg)
                    doc.add(Paragraph(" "))
                } catch (e: Exception) {
                    Timber.e("Error adding image to pdf: ${e.message}")
                }
            }

            doc.close().let {
                loadingDialog?.dismiss()
                Toast.makeText(context, "Pdf Saved in $filePath", Toast.LENGTH_SHORT).show()
                Timber.e("Pdf saved in $filePath")
                createPreviewDialog(
                    filePath,
                    context!!,
                    activity!!
                )
            }
        } catch (e: java.lang.Exception) {
            loadingDialog?.dismiss()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
        for (item in addedItemList) {
            subTotal += item.totalAmount!!
        }
        subTotalDCell.addElement(Paragraph(subTotal.toString()))
        subTotalDCell.setPadding(8f)
        table.addCell(subTotalDCell)
        val taxCell = PdfPCell()
        taxCell.addElement(Paragraph("TAX %"))
        taxCell.setPadding(8f)
        table.addCell(taxCell)
        val taxDCell = PdfPCell()
        taxDCell.addElement(Paragraph(quotationItem.vat.toString()))
        taxDCell.setPadding(8f)
        table.addCell(taxDCell)
        val taxAmountCell = PdfPCell()
        val taxAmount = (quotationItem.vat?.toDouble()?.div(100))?.times(subTotal)
        taxAmountCell.addElement(Paragraph("TAX AMOUNT"))
        taxAmountCell.setPadding(8f)
        table.addCell(taxAmountCell)
        val taxAmountDCell = PdfPCell()
        taxAmountDCell.addElement(Paragraph(taxAmount.toString()))
        taxAmountDCell.setPadding(8f)
        table.addCell(taxAmountDCell)
        val totalAmountCell = PdfPCell()
        totalAmountCell.addElement(Paragraph("TOTAL AMOUNT"))
        totalAmountCell.setPadding(8f)
        table.addCell(totalAmountCell)
        val totalAmountDCell = PdfPCell()
        val totalAmount = subTotal.plus(taxAmount!!).roundToLong()
        totalAmountDCell.addElement(Paragraph(totalAmount.toString()))
        totalAmountDCell.setPadding(8f)
        table.addCell(totalAmountDCell)
        return table
    }

    private fun createInvoiceTable(): PdfPTable {
        val table = PdfPTable(5)
        table.setWidths(intArrayOf(4, 2, 2, 2, 2))
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
        val totalCell = PdfPCell(Paragraph("Total Amount"))
        totalCell.setPadding(8f)
        table.addCell(totalCell)

        for (item in addedItemList) {
            val itemDesCell = PdfPCell(Paragraph(item.itemDes))
            itemDesCell.setPadding(8f)
            val qtyCell = PdfPCell(Paragraph(item.qty.toString() + "  " + item.unit))
            qtyCell.setPadding(8f)
            val unitDCell = PdfPCell(Paragraph(item.unitAmount.toString()))
            unitDCell.setPadding(8f)
            val disDCell = PdfPCell(Paragraph(item.discountAmount.toString()))
            disDCell.setPadding(8f)
            val totalDCell = PdfPCell(Paragraph(item.totalAmount.toString()))
            totalCell.setPadding(8f)
            table.addCell(itemDesCell)
            table.addCell(qtyCell)
            table.addCell(unitDCell)
            table.addCell(disDCell)
            table.addCell(totalDCell)
        }
        table.widthPercentage = 100f
        return table
    }

    private fun populateDetailsTable(): PdfPTable {

        val table = PdfPTable(2)
        val nameCell = PdfPCell()
        table.setWidths(intArrayOf(2, 2))
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
                companyDetails.comname,
                Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD)
            )
        )
        cell.addElement(
            Paragraph(
                companyDetails.street,
                Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
            )
        )
        cell.addElement(
            Paragraph(
                "${companyDetails.county}, ${companyDetails.postcode}",
                Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
            )
        )
        cell.addElement(
            Paragraph(
                "Phone: ${companyDetails.telephone}",
                Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
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
        val date = "$dayOfMonth/${monthOfYear + 1}/$year"
        quotationBinding.quotationDateTextInputLayout.setText(date)
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


    private fun createItemDialog(item: Item?, position: Int?) {

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
        val addButton: Button = layout.findViewById(R.id.add_item_button)
        val negButton: ImageButton = layout.findViewById(R.id.neg_qty_button)
        val posButton: ImageButton = layout.findViewById(R.id.pos_qty_button)
        val unitEditText: EditText = layout.findViewById(R.id.item_unit_editText)

        var amount = 0.0
        var discountAmount = 0.0

        unitAmountTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (s.isNotEmpty()) {
                        amount = s.toString().toDouble()
                        if (qtytv.text.isNotEmpty()) {
                            viewModel.calculateAmount(singleItemQty, amount, discountAmount)
                        }
                    }
                }
            }
        })

        discountAmountTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (s.isNotEmpty()) {
                        discountAmount = s.toString().toDouble()
                        if (qtytv.text.isNotEmpty() && unitAmountTv.text.isNotEmpty()) {
                            viewModel.calculateAmount(singleItemQty, amount, discountAmount)
                        }
                    }
                }
            }
        })

        qtytv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (s.isNotEmpty()) {
                        singleItemQty = s.toString().toInt()
                        if (unitAmountTv.text.isNotEmpty()) {
                            viewModel.calculateAmount(singleItemQty, amount, discountAmount)
                        }
                    }
                }
            }
        })
        viewModel.totalAmount.observe(viewLifecycleOwner, Observer {
            it?.let {
                totalAmountTv.text = it.toString()
            }
        })
        item?.let {
            singleItemQty = item.qty!!
            itemDestv.text = item.itemDes
            qtytv.text = item.qty.toString()
            unitAmountTv.text = item.unitAmount.toString()
            discountAmountTv.text = item.discountAmount.toString()
            discountAmount = item.discountAmount!!
            totalAmountTv.text = item.totalAmount.toString()
            addButton.text = getString(R.string.update)
            unitEditText.setText(item.unit)
        }
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

        if (item == null) {
            addButton.setOnClickListener {
                viewModel.setDefaultAmount()
                val itemDes = itemDestv.text.toString()
                val qty = qtytv.text.toString()
                val unitAmount = unitAmountTv.text.toString()
                val totalAmount = totalAmountTv.text.toString()
                val unit = unitEditText.text.toString()
                when {
                    itemDes.isEmpty() -> Toast.makeText(
                        context,
                        "Add item description",
                        Toast.LENGTH_SHORT
                    ).show()
                    qty.isEmpty() -> Toast.makeText(context, "Add Quantity", Toast.LENGTH_SHORT)
                        .show()
                    unitAmount.isEmpty() -> Toast.makeText(
                        context,
                        "Enter Unit Amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    totalAmount.isEmpty() -> Toast.makeText(
                        context,
                        "Enter Total Amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    unit.isEmpty() -> Toast.makeText(
                        requireContext(),
                        "Enter Unit",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> {
                        val item = Item(
                            itemDes,
                            qty.toInt(),
                            unit,
                            unitAmount.toDouble(),
                            discountAmount?.toDouble(),
                            totalAmount.toDouble()
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
        } else {

            addButton.setOnClickListener {
                viewModel.setDefaultAmount()
                val itemDes = itemDestv.text.toString()
                val qty = qtytv.text.toString()
                val unitAmount = unitAmountTv.text.toString()
                val totalAmount = totalAmountTv.text.toString()
                val unit = unitEditText.text.toString()
                when {
                    itemDes.isEmpty() -> Toast.makeText(
                        context,
                        "Add item description",
                        Toast.LENGTH_SHORT
                    ).show()
                    qty.isEmpty() -> Toast.makeText(context, "Add Quantity", Toast.LENGTH_SHORT)
                        .show()
                    unitAmount.isEmpty() -> Toast.makeText(
                        context,
                        "Enter Unit Amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    totalAmount.isEmpty() -> Toast.makeText(
                        context,
                        "Enter Total Amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    unit.isEmpty() -> Toast.makeText(
                        requireContext(),
                        "Enter Unit",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> {
                        val newItem = Item(
                            itemDes,
                            qty.toInt(),
                            unit,
                            unitAmount.toDouble(),
                            discountAmount.toDouble(),
                            totalAmount.toDouble()
                        )
                        itemNo++
                        addedItemList.removeAt(position!!)
                        addedItemList.add(position, newItem)

                        Timber.e("List size is ${itemList.size}")
                        quotationBinding.quotationAddItemTv.setText("No. of items: ${itemList.size}")
                        viewModel.addItemToQuotation(addedItemList)
                        dialog.dismiss()
                    }
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

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost, action: String) {

    }

    override fun onCustomerNameClick(name: String) {

    }

    override fun onSupplierNameClick(name: String) {

    }

    override fun onQuotationNameClick(name: String) {

    }

    override fun onPurchaseNameClick(name: String) {

    }

    override fun onInvoiceJobNoClick(name: String) {

    }

    override fun onTimeSheetJobClick(name: String) {

    }

    fun BITMAP_RESIZER(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val ratioX = newWidth / bitmap.width.toFloat()
        val ratioY = newHeight / bitmap.height.toFloat()
        val middleX = newWidth / 2.0f
        val middleY = newHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bitmap,
            middleX - bitmap.width / 2,
            middleY - bitmap.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        return scaledBitmap
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedText = parent?.getChildAt(0) as TextView
        selectedText.setTextColor(Color.WHITE)
        unit = parent.selectedItem as String
        unitPostion = position
    }

    override fun itemClick(item: Item, position: Int) {
        createChoiceDialog(item, position)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }
}
