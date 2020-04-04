package com.example.jeffaccount.ui.home.purchase

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.os.Environment
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
import com.example.jeffaccount.createPreviewDialog
import com.example.jeffaccount.databinding.AddPurchaseFragmentBinding
import com.example.jeffaccount.model.ComPost
import com.example.jeffaccount.model.PurchasePost
import com.example.jeffaccount.model.SupPost
import com.example.jeffaccount.network.*
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.*
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
import org.w3c.dom.Text
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong


class AddPurchaseFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    OnSearchItemClickListener, OnSearchSupplierClickListener, OnCustomerNameClickListener,
    OnSupplierNameClickListener, OnQuotationJobNoClickListener, OnPurchaseJobNoClickListener,
    OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener {

    private lateinit var purchaseBinding: AddPurchaseFragmentBinding
    private lateinit var action: String
    private lateinit var purchase: PurchasePost
    private var _vat: Double = 0.0
    private var qty: Int = 0
    private var _unitAmount: Double = 0.0
    private var _advanceAmount: Double = 0.0
    private var _discountAmount: Double = 0.0
    private var _totalAmount: Double = 0.0
    private lateinit var filePath: String
    private lateinit var itemAdapter: ItemAdapter
    private var itemList: MutableList<Item> = mutableListOf()
    private var nameList: MutableList<String> = mutableListOf()
    private var comNameList: MutableList<String> = mutableListOf()
    private var addedItemList: MutableList<Item> = mutableListOf()
    private var itemNo: Int = 1
    private lateinit var company: ComPost
    private lateinit var supplier: SupPost
    private var vatP:Double = 00.0

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
        purchaseBinding.purchaseAddItemTv.setOnClickListener {
            createItemDialog(null)
        }
        //Set on click listener to save purchase button
        purchaseBinding.purchaseSaveButton.setOnClickListener {
            val jobNo = purchaseBinding.purchaseJobnoTextInput.text.toString()
            val quotationNo = purchaseBinding.purchaseQuotationTextInput.text.toString()
            val date = purchaseBinding.purchaseDateTextInputLayout.text.toString()
            val supplierName = purchaseBinding.purchaseSupplierTextInputLayout.text.toString()
            val comment = purchaseBinding.purchaseCommentTextInput.text.toString()
            val paymentMethod = purchaseBinding.purchasePaymentMethodTextInput.text.toString()
            val street = purchaseBinding.addPurchaseStreetTv.text.toString()
            val country = purchaseBinding.addPurchaseCountryTv.text.toString()
            val postCode = purchaseBinding.addPurchasePostTv.text.toString()
            val telephone = purchaseBinding.addPurchaseTeleTv.text.toString()
            val vat = purchaseBinding.purchaseVatTextinput.text.toString()
            if (vat.isNotEmpty()){
                vatP = vat.toDouble()
            }
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
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Save Purchase?")
                    builder.setPositiveButton("Save", DialogInterface.OnClickListener{ dialog, which ->
                        val purchaseAdd = PurchaseAdd(
                            "AngE9676#254r5",
                            jobNo,
                            quotationNo,
                            supplierName,
                            date,
                            street,
                            country,
                            postCode,
                            telephone,
                            paymentMethod,
                            comment,
                            vatP,
                            addedItemList
                        )
                        viewModel.addPurchase(purchaseAdd).observe(viewLifecycleOwner,
                            Observer {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(AddPurchaseFragmentDirections.actionAddPurchaseFragmentToPurchaseFragment())
                            })
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
        purchaseBinding.purchaseEditButton.setOnClickListener {
            val jobNo = purchaseBinding.purchaseJobnoTextInput.text.toString()
            val quotationNo = purchaseBinding.purchaseQuotationTextInput.text.toString()
            val date = purchaseBinding.purchaseDateTextInputLayout.text.toString()
            val supplierName = purchaseBinding.purchaseSupplierTextInputLayout.text.toString()
            val comment = purchaseBinding.purchaseCommentTextInput.text.toString()
            val paymentMethod = purchaseBinding.purchasePaymentMethodTextInput.text.toString()
            val street = purchaseBinding.addPurchaseStreetTv.text.toString()
            val country = purchaseBinding.addPurchaseCountryTv.text.toString()
            val postCode = purchaseBinding.addPurchasePostTv.text.toString()
            val telephone = purchaseBinding.addPurchaseTeleTv.text.toString()
            val vat = purchaseBinding.purchaseVatTextinput.text.toString()

            if(vat.isNotEmpty()){
                vatP = vat.toDouble()
            }
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
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Update Purchase?")
                    builder.setPositiveButton("Save",DialogInterface.OnClickListener{dialog, which ->
                        val purchaseUpdate = PurchaseUpdate(
                            purchase.pid,
                            "AngE9676#254r5",
                            jobNo,
                            quotationNo,
                            supplierName,
                            date,
                            street,
                            country,
                            postCode,
                            telephone,
                            paymentMethod,
                            comment,
                            vatP,
                            addedItemList
                        )
                        viewModel.updatePurchase(purchaseUpdate).observe(viewLifecycleOwner,
                            Observer {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(AddPurchaseFragmentDirections.actionAddPurchaseFragmentToPurchaseFragment())
                            })
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

            val searchnameBottomSheet = SearchCustomerBottomSheetFragment(
                getString(R.string.purchase), nameList, this
                , this
            ,this,this, this, this,
                this, this
            )
            searchnameBottomSheet.show(activity!!.supportFragmentManager, searchnameBottomSheet.tag)
        }

        val itemRecyclerView = purchaseBinding.supplierItemRecyclerView
        itemAdapter = ItemAdapter(OnAddedItemClickListener {
            val item = it
            createChoiceDialog(item)
        })
        itemRecyclerView.adapter = itemAdapter

        return purchaseBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)

        requestReadPermissions()
        val activity = activity as MainActivity
        activity.setToolbarText("Add Purchase")
        val arguments = AddPurchaseFragmentArgs.fromBundle(arguments!!)
        action = arguments.action

        if (action.equals(getString(R.string.update))) {
            purchaseBinding.purchaseSaveButton.visibility = View.GONE
            purchaseBinding.purchaseEditButton.visibility = View.VISIBLE
            purchase = arguments.purchasePost!!
            itemList = purchase.itemDescription
            viewModel.addItemToPurchase(itemList)
            itemNo = itemList.size.plus(1)
            purchaseBinding.purchase = arguments.purchasePost
            val purchase = arguments.purchasePost!!
            purchaseBinding.purchaseSupplierTextInputLayout.text = purchase.customerName
            purchaseBinding.addPurchaseStreetTv.text = purchase.street
            purchaseBinding.addPurchaseCountryTv.text = purchase.country
            purchaseBinding.addPurchasePostTv.text = purchase.postCode
            purchaseBinding.addPurchaseTeleTv.text = purchase.telephone
            purchaseBinding.purchaseInfoGroup.visibility = View.VISIBLE
            activity.setToolbarText("Update Purchase")
            purchaseBinding.purchaseAddItemTv.text = "No. of Items: ${itemList.size}"
        } else if (action.equals(getString(R.string.company_details))) {
            company = arguments.companyItem!!
            purchaseBinding.purchaseSupplierTextInputLayout.text = company.comname
            purchaseBinding.addPurchaseStreetTv.text = company.street
            purchaseBinding.addPurchaseCountryTv.text = company.country
            purchaseBinding.addPurchasePostTv.text = company.postcode
            purchaseBinding.addPurchaseTeleTv.text = company.telephone
            purchaseBinding.purchaseInfoGroup.visibility = View.VISIBLE
        } else if (action.equals(getString(R.string.supplier_details))) {
            supplier = arguments.supplierItem!!
            purchaseBinding.purchaseSupplierTextInputLayout.text = supplier.supname
            purchaseBinding.addPurchaseStreetTv.text = supplier.street
            purchaseBinding.addPurchaseCountryTv.text = supplier.country
            purchaseBinding.addPurchasePostTv.text = supplier.postcode
            purchaseBinding.addPurchaseTeleTv.text = supplier.telephone
            purchaseBinding.purchaseInfoGroup.visibility = View.VISIBLE
        }

        viewModel.itemChangedToPurchase.observe(viewLifecycleOwner, Observer {
            addedItemList = it
            qty = addedItemList.size
            itemAdapter.submitList(addedItemList)
            itemAdapter.notifyDataSetChanged()
        })

        //Get list of supplier to get the names
        viewModel.getSuppliers().observe(viewLifecycleOwner, Observer {
            it?.let {
                val list = it.posts
                for (item in list) {
                    nameList.add(item.supname!!)
                }
            }
        })

        //Get list of company
        viewModel.getCompanyList().observe(viewLifecycleOwner, Observer {
            it?.let {
                val list = it.posts
                for (item in list!!) {
                    comNameList.add(item.comname!!)
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
                savePdf()
            }
        }
        return true
    }

    private fun createChoiceDialog(item: Item) {
        val layout = LayoutInflater.from(context).inflate(R.layout.choose_layout, null)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(layout)
        val dialog = builder.create()
        dialog.show()
        val editButton: MaterialButton = layout.findViewById(R.id.choice_edit_button)
        val deleteButton: MaterialButton = layout.findViewById(R.id.choice_delete_button)
        deleteButton.setOnClickListener {
            addedItemList.remove(item)
            viewModel.addItemToPurchase(addedItemList)
            dialog.dismiss()
        }
        editButton.setOnClickListener {
            dialog.dismiss()
            createItemDialog(item)
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
            val headTable = PdfPTable(2)
            headTable.setWidths(intArrayOf(4, 2))
            headTable.widthPercentage = 100f
            val addressCell = getAddressTable()
            val dateCell = PdfPCell()
            dateCell.border = PdfPCell.NO_BORDER
            dateCell.addElement(Paragraph("Date: ${purchase.date}"))
            dateCell.addElement(Paragraph("Quotation No. : ${purchase.quotationNo}"))
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
            doc.add(Paragraph("Special Instructions "))
            doc.add(Paragraph(purchase.specialInstruction))
            doc.add(Paragraph(""))
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
                createPreviewDialog(filePath, context!!, activity!!)
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
        for (item in addedItemList) {
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
        taxDCell.addElement(Paragraph(purchase.vat))
        taxDCell.setPadding(8f)
        table.addCell(taxDCell)
        val taxAmountCell = PdfPCell()
        taxAmountCell.addElement(Paragraph("TAX AMOUNT"))
        taxAmountCell.setPadding(8f)
        table.addCell(taxAmountCell)
        val taxAmountDCell = PdfPCell()
        val taxAmount = (purchase.vat?.toDouble()?.div(100))?.times(subTotal)
        taxAmountDCell.addElement(Paragraph(taxAmount.toString()))
        taxAmountDCell.setPadding(8f)
        table.addCell(taxAmountDCell)
        val disocuntCell = PdfPCell()
        disocuntCell.addElement(Paragraph("DISCOUNT AMOUNT"))
        disocuntCell.setPadding(8f)
        table.addCell(disocuntCell)
        val discountDCell = PdfPCell()
        var totalDiscount = 0.0
        for (item in addedItemList) {
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
        val totalAmount = subTotal.plus(taxAmount!!).minus(totalDiscount).roundToLong()
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
        for (item in addedItemList) {
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

    private fun createItemDialog(item: Item?) {

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

        var amount = 0.0
        var discountAmount = 0.0

        unitAmountTv.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                if (s.isNotEmpty()){
                    amount = s.toString().toDouble()
                    if (qtytv.text.isNotEmpty()&& discountAmountTv.text.isNotEmpty()){
                        viewModel.calculateAmount(qty,amount,discountAmount)
                    }
                }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        discountAmountTv.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              s?.let {
                  if (s.isNotEmpty()){
                      discountAmount = s.toString().toDouble()
                      if (qtytv.text.isNotEmpty() && unitAmountTv.text.isNotEmpty()) {
                          viewModel.calculateAmount(qty,amount, discountAmount)
                      }
                  }
              }
            }
        })

        qtytv.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (s.isNotEmpty()) {
                        qty = s.toString().toInt()
                        if (unitAmountTv.text.isNotEmpty() && discountAmountTv.text.isNotEmpty()) {
                            viewModel.calculateAmount(qty, amount, discountAmount)
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
            qty = item.qty!!
            itemDestv.text = item.itemDes
            qtytv.text = item.qty.toString()
            unitAmountTv.text = item.unitAmount.toString()
            discountAmountTv.text = item.discountAmount.toString()
            totalAmountTv.text = item.totalAmount.toString()
            addButton.text = getString(R.string.update)
        }
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
        if (item == null) {
            addButton.setOnClickListener {
                viewModel.setDefaultAmount()
                val itemDes = itemDestv.text.toString()
                val qty = qtytv.text.toString()
                val unitAmount = unitAmountTv.text.toString()
                val discountAmount = discountAmountTv.text.toString()
                val totalAmount = totalAmountTv.text.toString()
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
                    else -> {
                        val item = Item(
                            itemNo,
                            itemDes,
                            qty.toInt(),
                            unitAmount.toDouble(),
                            discountAmount.toDouble(),
                            totalAmount.toDouble()
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
        }else{
            addButton.setOnClickListener {
                viewModel.setDefaultAmount()
                val itemDes = itemDestv.text.toString()
                val qty = qtytv.text.toString()
                val unitAmount = unitAmountTv.text.toString()
                val discountAmount = discountAmountTv.text.toString()
                val totalAmount = totalAmountTv.text.toString()
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
                    else -> {
                        val newItem = Item(
                            itemNo,
                            itemDes,
                            qty.toInt(),
                            unitAmount.toDouble(),
                            discountAmount.toDouble(),
                            totalAmount.toDouble()
                        )
                        itemNo++
                        addedItemList.add(newItem)
                        addedItemList.remove(item)
                        Timber.e("List size is ${itemList.size}")
                        purchaseBinding.purchaseAddItemTv.setText("No. of items: ${itemList.size}")
                        viewModel.addItemToPurchase(addedItemList)
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {

    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost) {

        purchaseBinding.purchaseInfoGroup.visibility = View.VISIBLE
        purchaseBinding.purchaseSupplierTextInputLayout.text = serchSupplierPost.supname
        purchaseBinding.addPurchaseStreetTv.text = serchSupplierPost.street
        purchaseBinding.addPurchaseCountryTv.text = serchSupplierPost.country
        purchaseBinding.addPurchasePostTv.text = serchSupplierPost.postcode
        purchaseBinding.addPurchaseTeleTv.text = serchSupplierPost.telephone

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
}
