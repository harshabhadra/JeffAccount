package com.example.jeffaccount.ui.home.purchase

import android.Manifest
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddPurchaseFragmentBinding
import com.example.jeffaccount.model.*
import com.example.jeffaccount.network.*
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.*
import com.example.jeffaccount.ui.home.supplier.SupplierFragmentDirections
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
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class AddPurchaseFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    OnSearchItemClickListener, OnSearchSupplierClickListener, OnCustomerNameClickListener,
    OnSupplierNameClickListener, OnQuotationJobNoClickListener, OnPurchaseJobNoClickListener,
    OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener, onSaveSupplierListener,
    OnSupplierClickListener {

    private lateinit var purchaseBinding: AddPurchaseFragmentBinding
    private lateinit var action: String
    private lateinit var purchase: PurchasePost
    private lateinit var filePath: String
    private var itemList: MutableList<Item> = mutableListOf()
    private var nameList: MutableList<String> = mutableListOf()
    private var custNameList: MutableList<String> = mutableListOf()
    private var itemNo: Int = 1
    private lateinit var companyDetails: CompanyDetails
    private lateinit var supplier: SupPost
    private lateinit var comid: String
    private lateinit var supplierListAdapter: SupplierListAdapter
    private var supllierList: MutableList<SupList> = mutableListOf()
    private lateinit var supName: String
    private lateinit var supCountyName: String
    private var logoList: MutableList<Logo> = mutableListOf()
    private var bmpList: MutableList<Bitmap> = mutableListOf()
    private lateinit var companyBitmap: Bitmap
    private  var customerName: String = ""
    private  var customerStreet: String = ""
    private  var customerPostCode: String = ""
    private  var customerTelephone: String = ""
    private  var customerEmail: String = ""
    private  var customerWeb: String = ""
    private  var customerCountry: String = ""
    private  var customerCounty: String = ""

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

        setHasOptionsMenu(true)

        //Set on click listener to add item text
        purchaseBinding.purchaseAddSupplierTv.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("What you'd like to do?")
            builder.setPositiveButton("Select", DialogInterface.OnClickListener { dialog, which ->
                val searchnameBottomSheet = SearchCustomerBottomSheetFragment(
                    getString(R.string.purchase), nameList, this
                    , this
                    , this, this, this, this,
                    this, this
                )
                searchnameBottomSheet.show(activity!!.supportFragmentManager, searchnameBottomSheet.tag)
            }).setNegativeButton("Create New", DialogInterface.OnClickListener { dialog, which ->

                findNavController().navigate(
                    AddPurchaseFragmentDirections.actionAddPurchaseFragmentToAddSupplierFragment(
                    SupPost(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    ),"add"
                ))            })
            val dialog = builder.create()
            dialog.show()

        }

        //Set on Click listener to Add customer button
        purchaseBinding.purchasAddCustomerTextView.setOnClickListener {

            val searchNameBottomSheet = SearchCustomerBottomSheetFragment(
                getString(R.string.quotation), custNameList, this
                , this
                , this, this, this, this,
                this, this
            )
            searchNameBottomSheet.show(activity!!.supportFragmentManager, searchNameBottomSheet.tag)
        }
        //Set on click listener to save purchase button
        purchaseBinding.purchaseSaveButton.setOnClickListener {
            val jobNo = purchaseBinding.purchaseJobnoTextInput.text.toString()
            val quotationNo = purchaseBinding.purchaseQuotationTextInput.text.toString()
            val date = purchaseBinding.purchaseDateTextInputLayout.text.toString()
            val comment = purchaseBinding.purchaseCommentTextInput.text.toString()

            when {
                jobNo.isEmpty() -> purchaseBinding.purchaseJobnoTextInputLayout.error =
                    getString(R.string.enter_job_no)
                quotationNo.isEmpty() -> purchaseBinding.purchaseQuotationTextInputLayout.error =
                    getString(R.string.enter_quotation_no)
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Save Purchase?")
                    builder.setPositiveButton(
                        "Save",
                        DialogInterface.OnClickListener { dialog, which ->
                            val purchaseAdd = PurchaseAdd(
                                comid,
                                "AngE9676#254r5",
                                jobNo,
                                quotationNo,
                                supllierList,
                                customerName,
                                customerStreet,
                                customerPostCode,
                                customerTelephone,
                                customerEmail,
                                customerWeb,
                                customerCountry,
                                customerCounty,
                                date,
                                comment
                            )
                            viewModel.addPurchase(purchaseAdd).observe(viewLifecycleOwner,
                                Observer {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(AddPurchaseFragmentDirections.actionAddPurchaseFragmentToPurchaseFragment())
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

        //Set on click listener to update button
        purchaseBinding.purchaseEditButton.setOnClickListener {
            val jobNo = purchaseBinding.purchaseJobnoTextInput.text.toString()
            val quotationNo = purchaseBinding.purchaseQuotationTextInput.text.toString()
            val date = purchaseBinding.purchaseDateTextInputLayout.text.toString()
            val comment = purchaseBinding.purchaseCommentTextInput.text.toString()

            when {
                jobNo.isEmpty() -> purchaseBinding.purchaseJobnoTextInputLayout.error =
                    getString(R.string.enter_job_no)
                quotationNo.isEmpty() -> purchaseBinding.purchaseQuotationTextInputLayout.error =
                    getString(R.string.enter_quotation_no)
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Update Purchase?")
                    builder.setPositiveButton(
                        "Update",
                        DialogInterface.OnClickListener { dialog, which ->
                            val purchaseUpdate = PurchaseUpdate(
                                comid,
                                purchase.pid!!,
                                "AngE9676#254r5",
                                jobNo,
                                quotationNo,
                                supllierList,
                                customerName,
                                customerStreet,
                                customerPostCode,
                                customerTelephone,
                                customerEmail,
                                customerWeb,
                                customerCountry,
                                customerCounty,
                                date,
                                comment
                            )
                            viewModel.updatePurchase(purchaseUpdate).observe(viewLifecycleOwner,
                                Observer {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(AddPurchaseFragmentDirections.actionAddPurchaseFragmentToPurchaseFragment())
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


        return purchaseBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)

        requestReadPermissions()
        val activity = activity as MainActivity
        activity.setToolbarText("Add Purchase")
        companyDetails = activity.companyDetails
        companyDetails.caomimge?.let {
            viewModel.getCompanyBitmap("https://alphabusinessdesigns.com/wordpress/appproject/jtapp/$it")
        }
        comid = activity.companyDetails.comid

        val arguments = AddPurchaseFragmentArgs.fromBundle(arguments!!)
        action = arguments.action

        if (action.equals(getString(R.string.update))) {
            purchaseBinding.purchaseSaveButton.visibility = View.GONE
            purchaseBinding.purchaseEditButton.visibility = View.VISIBLE
            purchaseBinding.purchaseCustomerGroup.visibility = View.VISIBLE
            purchase = arguments.purchasePost!!
            purchase.supList?.let {
                supllierList = purchase.supList!!.toMutableList()
                viewModel.changeItemToSupList(supllierList)
            }
            itemNo = itemList.size.plus(1)
            purchaseBinding.purchase = arguments.purchasePost
            purchaseBinding.purchaseJobnoTextInput.setText(purchase.jobNo)
            purchaseBinding.purchaseQuotationTextInput.setText(purchase.quotationNo)
            if (purchase.custname != "") {
                purchaseBinding.purchasAddCustomerTextView.setText("Name: ${purchase.custname}")
                purchaseBinding.purchaseStreetTextView.setText("Street: ${purchase.street}")
                purchaseBinding.purchaseEmailTextView.setText("Email: ${purchase.customeremail}")
                purchaseBinding.purchaseTelephoneTextView.setText("Telephone: ${purchase.telephone}")
                purchaseBinding.purchaseCountyTextView.setText("County: ${purchase.county}")
                purchaseBinding.purchasePostcodeTextView.text = "Post code: ${purchase.postcode}"
                customerName = purchase.custname
                customerStreet = purchase.street
                customerPostCode = purchase.postcode
                customerTelephone = purchase.telephone
                customerEmail = purchase.customeremail
                customerWeb = purchase.web
                customerCountry = purchase.country
                customerCounty = purchase.county
            }
            activity.setToolbarText("Update Purchase")
        } else if (action.equals(getString(R.string.supplier_details))) {
            supplier = arguments.supplierItem!!
        } else if (action.equals(getString(R.string.quotation)) || action.equals(R.string.time_sheet)) {
            purchaseBinding.purchaseJobnoTextInput.setText(arguments.jobno!!)
            purchaseBinding.purchaseQuotationTextInput.setText(arguments.quotationno!!)
        }


        //Get list of supplier to get the names
        viewModel.getSuppliers(comid).observe(viewLifecycleOwner, Observer {
            it?.let {
                val list = it.posts
                for (item in list) {
                    nameList.add(item.supname!!)
                }
            }
        })

        //Get list of customers
        viewModel.getCustomerList(comid).observe(viewLifecycleOwner, Observer {
            it?.let {
                val list = it.posts
                for (cust in list) {
                    custNameList.add(cust.custname!!)
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

        //Observe supplier change to purchase
        viewModel.supplierChangedToPurchase.observe(viewLifecycleOwner, Observer {
            it?.let {
                supplierListAdapter = SupplierListAdapter(context!!, this, it)
                purchaseBinding.supplierItemRecyclerView.adapter = supplierListAdapter
                supplierListAdapter.notifyDataSetChanged()
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
            inflater.inflate(R.menu.purchase_menu, menu)
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
                savePdf()
            }
            R.id.action_quotation -> {
                findNavController().navigate(
                    AddPurchaseFragmentDirections.actionAddPurchaseFragmentToAddQuotationFragment(
                        null,
                        getString(R.string.purchase),
                        null,
                        null,
                        purchase.jobNo,
                        purchase.quotationNo
                    )
                )
            }
            R.id.action_timeSheet -> {
                findNavController().navigate(
                    AddPurchaseFragmentDirections.actionAddPurchaseFragmentToAddTimeSheetFragment(
                        null, getString(R.string.purchase), purchase.jobNo, purchase.quotationNo
                    )
                )
            }
            R.id.worksheet_action -> {
                findNavController().navigate(AddPurchaseFragmentDirections.actionAddPurchaseFragmentToCreateWorkSheetFragment())
            }
        }
        return true
    }

    private fun createChoiceDialog(item: SupList, position: Int) {
        val layout = LayoutInflater.from(context).inflate(R.layout.choose_layout, null)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(layout)
        val dialog = builder.create()
        dialog.show()
        val editButton: MaterialButton = layout.findViewById(R.id.choice_edit_button)
        val deleteButton: MaterialButton = layout.findViewById(R.id.choice_delete_button)
        deleteButton.setOnClickListener {
            supllierList.remove(item)
            viewModel.changeItemToSupList(supllierList)
            dialog.dismiss()
        }
        editButton.setOnClickListener {
            val addSupplierBottomSheetFragment =
                AddSupplierBottomSheetFragment(
                    item,
                    this, position
                )
            addSupplierBottomSheetFragment.isCancelable = false
            addSupplierBottomSheetFragment.show(
                activity!!.supportFragmentManager,
                addSupplierBottomSheetFragment.tag
            )
            supllierList.remove(item)
            dialog.dismiss()
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
            var addressCell = getAddressTable()
            val dateCell = PdfPCell()
            dateCell.border = PdfPCell.NO_BORDER
            dateCell.addElement(Paragraph("Date: ${purchase.date}"))
            dateCell.addElement(Paragraph("Job no. : ${purchase.jobNo}"))
            dateCell.addElement(Paragraph("Quotation No. : ${purchase.quotationNo}"))
            dateCell.horizontalAlignment = Element.ALIGN_RIGHT
            headTable.addCell(addressCell)
            headTable.addCell(dateCell)
            doc.add(headTable)
            doc.add(Paragraph(" "))
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
            if (web.isNotEmpty()){
                val chunk = Chunk(web)
                chunk.setAnchor(URL(web))
                doc.add(Paragraph(chunk))
            }
            doc.add(Paragraph(" "))
            val invoiceTitle =
                Paragraph("PURCHASE", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            invoiceTitle.alignment = Element.ALIGN_CENTER
            doc.add(invoiceTitle)

            val suppliers = purchase.supList
            suppliers?.let {
                for (supplier in suppliers) {
                    doc.add(Paragraph(" "))
                    doc.add(Paragraph("Supplier Name: ${supplier.supName}"))
                    doc.add(Paragraph("County Name: ${supplier.county}"))
                    doc.add(Paragraph("Purchase Date: ${supplier.supDate}"))
                    doc.add(Paragraph(" "))
                    val supplierTable = createSupplierTable(supplier.itemList)
                    doc.add(supplierTable)
                    doc.add(Paragraph("Vat%: ${supplier.vat}"))
                    var totalEx = 0.0
                    supplier.itemList?.let {
                        for (item in supplier.itemList!!) {
                            item.totalAmount?.let {
                                totalEx += item.totalAmount!!
                            }
                        }
                        val totalPurhcase = (supplier.vat?.div(100)?.times(totalEx))?.plus(totalEx)
                        doc.add(Paragraph("Total Expense with Vat: $totalPurhcase"))
                        doc.add(Paragraph("Payment Method: ${supplier.paymentMethod}"))
                    }
                    doc.add(Paragraph(" "))

                }
            }

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
                Toast.makeText(context, "Pdf Saved in $filePath", Toast.LENGTH_SHORT).show()
                createPreviewDialog(
                    filePath,
                    context!!,
                    activity!!
                )
            }
        } catch (e: java.lang.Exception) {

            Timber.e("Error: ${e.message}")
        }
    }

    private fun createSupplierTable(itemList: List<Item>?): PdfPTable {
        val table = PdfPTable(5)
        table.widthPercentage = 100f
        table.setWidths(intArrayOf(2, 1, 1, 1, 1))
        val itemNameCell = PdfPCell(Paragraph("Item Name"))
        itemNameCell.setPadding(4f)
        val qtyCell = PdfPCell(Paragraph("Qty"))
        qtyCell.setPadding(4f)
        val amountCell = PdfPCell(Paragraph("Unit Amount"))
        amountCell.setPadding(4f)
        val discountCell = PdfPCell(Paragraph("Discount"))
        discountCell.setPadding(4f)
        val totoalCell = PdfPCell(Paragraph("Total"))
        totoalCell.setPadding(4f)
        table.addCell(itemNameCell)
        table.addCell(qtyCell)
        table.addCell(amountCell)
        table.addCell(discountCell)
        table.addCell(totoalCell)

        itemList?.let {
            for (item in itemList) {
                val itemNameVCell = PdfPCell(Paragraph(item.itemDes))
                itemNameVCell.setPadding(4f)
                val qtyVCell = PdfPCell(Paragraph(item.qty.toString() + "  " + item.unit))
                qtyVCell.setPadding(4f)
                val amountVCell = PdfPCell(Paragraph(item.unitAmount.toString()))
                amountVCell.setPadding(4f)
                val discountVCell = PdfPCell(Paragraph(item.discountAmount.toString()))
                discountVCell.setPadding(4f)
                val totoalVCell = PdfPCell(Paragraph(item.totalAmount.toString()))
                totoalVCell.setPadding(4f)
                table.addCell(itemNameVCell)
                table.addCell(qtyVCell)
                table.addCell(amountVCell)
                table.addCell(discountVCell)
                table.addCell(totoalVCell)
            }
        }
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

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = "$dayOfMonth/${monthOfYear + 1}/$year"
        purchaseBinding.purchaseDateTextInputLayout.text = date
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {

        customerName = searchCustomer.custname
        customerStreet = searchCustomer.street
        customerPostCode = searchCustomer.postcode
        customerTelephone = searchCustomer.telephone
        customerEmail = searchCustomer.customerEmail
        customerWeb = searchCustomer.web
        customerCountry = searchCustomer.country
        customerCounty = searchCustomer.county
        purchaseBinding.purchaseCustomerGroup.visibility = View.VISIBLE
        purchaseBinding.purchasAddCustomerTextView.setText("Name: " + customerName)
        purchaseBinding.purchaseStreetTextView.setText("Street address: $customerStreet")
        purchaseBinding.purchaseEmailTextView.setText("Email: $customerEmail")
        purchaseBinding.purchaseTelephoneTextView.setText("Telephone: $customerTelephone")
        purchaseBinding.purchaseCountyTextView.setText("County: $customerCounty")
        purchaseBinding.purchasePostcodeTextView.text = "Post code: ${customerPostCode}"
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost, action: String) {

        if (action.equals(getString(R.string.select))) {
            supName = serchSupplierPost.supname!!
            supCountyName = serchSupplierPost.county
            val supList = SupList(supName, null, supCountyName, null, 0.0, null)
            val addSupplierBottomSheetFragment =
                AddSupplierBottomSheetFragment(supList, this, null)
            addSupplierBottomSheetFragment.show(
                activity!!.supportFragmentManager,
                addSupplierBottomSheetFragment.tag
            )
        } else {
            findNavController().navigate(
                AddPurchaseFragmentDirections.actionAddPurchaseFragmentToAddSupplierFragment(
                    serchSupplierPost.asSupplierPost(), getString(R.string.edit)
                )
            )
        }
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

    override fun onSaveSupplier(
        supList: SupList, position: Int?
    ) {
        position?.let {
            supllierList.add(position, supList)
        } ?: let {
            supllierList.add(supList)
        }
        viewModel.changeItemToSupList(supllierList)
    }

    override fun onSupplierClick(position: Int, supList: SupList) {
        supList?.let {
            Timber.e("Item position is $position")
            createChoiceDialog(supList, position)
        }
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
}
