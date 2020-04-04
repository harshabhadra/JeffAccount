package com.example.jeffaccount.ui.home.invoice

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
import com.example.jeffaccount.databinding.FragmentAddInvoiceBinding
import com.example.jeffaccount.model.Invoice
import com.example.jeffaccount.network.*
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.*
import com.google.android.material.button.MaterialButton
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

/**
 * A simple [Fragment] subclass.
 */
class AddInvoiceFragment : Fragment(), OnSearchItemClickListener, OnSearchSupplierClickListener,
    DatePickerDialog.OnDateSetListener, OnCustomerNameClickListener, OnSupplierNameClickListener,
    OnQuotationJobNoClickListener, OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener,
    OnTimeSheetJobNoClickListener {

    private lateinit var invoiceVieModel: InvoiceViewModel
    private lateinit var addInvoiceBinding: FragmentAddInvoiceBinding
    private lateinit var street: String
    private lateinit var country: String
    private lateinit var telephone: String
    private lateinit var postCode: String
    private var itemList: MutableList<Item> = mutableListOf()
    private var nameList: MutableList<String> = mutableListOf()
    private var addedItemList: MutableList<Item> = mutableListOf()
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var invoiceItem: Invoice
    private lateinit var action: String
    private lateinit var filePath: String

    private var vatP:Double = 0.0
    private var itemNo: Int = 1
    private var singleItemQty = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addInvoiceBinding = FragmentAddInvoiceBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        invoiceVieModel = ViewModelProvider(this).get(InvoiceViewModel::class.java)

        val activity = activity as MainActivity
        activity.setToolbarText("Add Invoice")

        //Getting arguments from other fragments
        val arguments = AddInvoiceFragmentArgs.fromBundle(arguments!!)
        action = arguments.action
        Timber.e("Items received: ${arguments.action}")
        if (action.equals(getString(R.string.update))) {
            Timber.e("Items received: ${arguments.action}")
            invoiceItem = arguments.invoice!!
            activity.setToolbarText("Update Invoice")
            addInvoiceBinding.invoice = invoiceItem
            addInvoiceBinding.invoiceUpdateButton.visibility = View.VISIBLE
            addInvoiceBinding.saveInvoiceButton.visibility = View.GONE
            itemList = invoiceItem.itemDescription
            invoiceVieModel.addItemToInvoice(itemList)
            itemNo = itemList.size.plus(1)
            addInvoiceBinding.invoiceCustomerNameTextInputLayout.text = invoiceItem.customerName
            addInvoiceBinding.addInvoiceStreetTv.text = invoiceItem.street
            addInvoiceBinding.addInvoiceCountryTv.text = invoiceItem.country
            addInvoiceBinding.addInvoicePostcodeTv.text = invoiceItem.postCode
            addInvoiceBinding.addInvoiceTelephoneTv.text = invoiceItem.telephone
            addInvoiceBinding.invoiceCustAddtionalGroup.visibility = View.VISIBLE
            addInvoiceBinding.invoiceAddItemTv.text = "No. of items: ${itemList.size}"
        } else if (action.equals(getString(R.string.customer_data))) {
            Timber.e("Action is $action")
            val customer = arguments.customer!!
            addInvoiceBinding.invoiceCustomerNameTextInputLayout.text = customer.custname
            addInvoiceBinding.addInvoiceStreetTv.text = customer.street
            addInvoiceBinding.addInvoiceCountryTv.text = customer.country
            addInvoiceBinding.addInvoicePostcodeTv.text = customer.postcode
            addInvoiceBinding.addInvoiceTelephoneTv.text = customer.telephone
            addInvoiceBinding.invoiceCustAddtionalGroup.visibility = View.VISIBLE
        } else if (action.equals(getString(R.string.quotation))) {
            Timber.e("Action is $action")
            val quotation = arguments.quotation!!
            val invoice = Invoice(
                quotation.qid,
                quotation.jobNo,
                quotation.quotationNo,
                quotation.customerName,
                quotation.date,
                quotation.street,
                quotation.country,
                quotation.postCode,
                quotation.telephone,
                quotation.paymentMethod,
                quotation.comment,
                quotation.vat,
                quotation.itemDescription
            )
            addInvoiceBinding.invoice = invoice
            itemList = quotation.itemDescription
            invoiceVieModel.addItemToInvoice(itemList)
            addInvoiceBinding.invoiceCustomerNameTextInputLayout.text = quotation.customerName
            addInvoiceBinding.addInvoiceStreetTv.text = quotation.street
            addInvoiceBinding.addInvoiceCountryTv.text = quotation.country
            addInvoiceBinding.addInvoicePostcodeTv.text = quotation.postCode
            addInvoiceBinding.addInvoiceTelephoneTv.text = quotation.telephone
            addInvoiceBinding.invoiceCustAddtionalGroup.visibility = View.VISIBLE
        }

        //Get customer list and make a list of name
        invoiceVieModel.getCustomerList().observe(viewLifecycleOwner, Observer {
            it?.let {
                val customerList = it.posts
                for (item in customerList) {
                    nameList.add(item.custname!!)
                }
            }
        })

        //Set on click listener to customer name tv
        addInvoiceBinding.invoiceCustomerNameTextInputLayout.setOnClickListener {
            val searchNameFrag =
                SearchCustomerBottomSheetFragment(
                    getString(R.string.invoice), nameList, this, this
                    , this, this,
                    this, this, this, this
                )
            searchNameFrag.show(activity!!.supportFragmentManager, searchNameFrag.tag)
        }

        //Set on click listeener to date tv
        addInvoiceBinding.invoiceDateTextInputLayout.setOnClickListener {
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

        //Set on click listener to add item tv
        addInvoiceBinding.invoiceAddItemTv.setOnClickListener {
            createItemDialog(null)
        }

        //Setting up item recyclerView
        itemAdapter = ItemAdapter(OnAddedItemClickListener {
            createChoiceDialog(it)
        })
        addInvoiceBinding.invoiceItemRecyclerView.adapter = itemAdapter

        //Observe when items are added to invoice
        invoiceVieModel.itemAddedToInvoice.observe(viewLifecycleOwner, Observer {
            it?.let {
                addedItemList = it
                itemAdapter.submitList(addedItemList)
                addInvoiceBinding.invoiceAddItemTv.text = "No. of Items: ${addedItemList.size}"
                itemAdapter.notifyDataSetChanged()
            }
        })

        //Set on click listener to save button
        addInvoiceBinding.saveInvoiceButton.setOnClickListener {
            val jobNo = addInvoiceBinding.invoiceJobTextInput.text.toString()
            val quotationNo = addInvoiceBinding.invoiceQuotationoTextInput.text.toString()
            val date = addInvoiceBinding.invoiceDateTextInputLayout.text.toString()
            val customerName = addInvoiceBinding.invoiceCustomerNameTextInputLayout.text.toString()
            val comment = addInvoiceBinding.invoiceCommentTextInput.text.toString()
            val paymentMethod = addInvoiceBinding.invoicePayementMethodTextInput.text.toString()
            val vatPercentage = addInvoiceBinding.invoiceVatTextInput.text.toString()
            street = addInvoiceBinding.addInvoiceStreetTv.text.toString()
            country = addInvoiceBinding.addInvoiceCountryTv.text.toString()
            postCode = addInvoiceBinding.addInvoicePostcodeTv.text.toString()
            telephone = addInvoiceBinding.addInvoiceTelephoneTv.text.toString()

            if (vatPercentage.isNotEmpty()){
                vatP = vatPercentage.toDouble()
            }
            when {
                jobNo.isEmpty() -> {
                    addInvoiceBinding.invoiceJobTextInputLayout.error =
                        getString(R.string.enter_job_no)
                }

                quotationNo.isEmpty() -> {
                    addInvoiceBinding.invoiceQuotationoTextInputLayout.error =
                        getString(R.string.enter_quotation_no)
                }
                date.isEmpty() -> {
                    addInvoiceBinding.invoiceDateTextInputLayout.error =
                        getString(R.string.enter_date)
                }
                comment.isEmpty() -> {
                    addInvoiceBinding.invoiceCommentTextInputLayout.error =
                        getString(R.string.enter_comment)
                }
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Save Invoice?")
                    builder.setPositiveButton(
                        "Save",
                        DialogInterface.OnClickListener { dialog, which ->
                            val quotation = QuotationAdd(
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
                                vatP,
                                itemList
                            )
                            invoiceVieModel.saveInvoice(quotation)
                                .observe(viewLifecycleOwner, Observer {
                                    it?.let {
                                        Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(AddInvoiceFragmentDirections.actionAddInvoiceFragmentToInvoiceFragment())
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

        //Update Invoice
        addInvoiceBinding.invoiceUpdateButton.setOnClickListener {
            val jobNo = addInvoiceBinding.invoiceJobTextInput.text.toString()
            val quotationNo = addInvoiceBinding.invoiceQuotationoTextInput.text.toString()
            val date = addInvoiceBinding.invoiceDateTextInputLayout.text.toString()
            val customerName = addInvoiceBinding.invoiceCustomerNameTextInputLayout.text.toString()
            val comment = addInvoiceBinding.invoiceCommentTextInput.text.toString()
            val paymentMethod = addInvoiceBinding.invoicePayementMethodTextInput.text.toString()
            val vatPercentage = addInvoiceBinding.invoiceVatTextInput.text.toString()
            street = addInvoiceBinding.addInvoiceStreetTv.text.toString()
            country = addInvoiceBinding.addInvoiceCountryTv.text.toString()
            postCode = addInvoiceBinding.addInvoicePostcodeTv.text.toString()
            telephone = addInvoiceBinding.addInvoiceTelephoneTv.text.toString()

            if (vatPercentage.isNotEmpty()){
                vatP = vatPercentage.toDouble()
            }
            when {
                jobNo.isEmpty() -> {
                    addInvoiceBinding.invoiceJobTextInputLayout.error =
                        getString(R.string.enter_job_no)
                }

                quotationNo.isEmpty() -> {
                    addInvoiceBinding.invoiceQuotationoTextInputLayout.error =
                        getString(R.string.enter_quotation_no)
                }
                date.isEmpty() -> {
                    addInvoiceBinding.invoiceDateTextInputLayout.error =
                        getString(R.string.enter_date)
                }
                customerName.isEmpty() -> {
                    addInvoiceBinding.invoiceCustomerNameTextInputLayout.error =
                        getString(R.string.enter_customer_name)
                }
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Update Invoice?")
                    builder.setPositiveButton(
                        "Save",
                        DialogInterface.OnClickListener { dialog, which ->
                            val invoice = InvoiceUpdate(
                                invoiceItem.inid,
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
                                vatP,
                                itemList
                            )
                            invoiceVieModel.updateInvoice(invoice)
                                .observe(viewLifecycleOwner, Observer {
                                    it?.let {
                                        Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(AddInvoiceFragmentDirections.actionAddInvoiceFragmentToInvoiceFragment())
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

        setHasOptionsMenu(true)

        return addInvoiceBinding.root
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
            invoiceVieModel.addItemToInvoice(addedItemList)
            dialog.dismiss()
        }
        editButton.setOnClickListener {
            dialog.dismiss()
            createItemDialog(item)
        }

    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {
        addInvoiceBinding.invoiceCustomerNameTextInputLayout.text = searchCustomer.custname
        addInvoiceBinding.addInvoiceStreetTv.text = searchCustomer.street
        addInvoiceBinding.addInvoiceCountryTv.text = searchCustomer.country
        addInvoiceBinding.addInvoicePostcodeTv.text = searchCustomer.postcode
        addInvoiceBinding.addInvoiceTelephoneTv.text = searchCustomer.telephone
        addInvoiceBinding.invoiceCustAddtionalGroup.visibility = View.VISIBLE
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost) {

    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        addInvoiceBinding.invoiceDateTextInputLayout.text = "$dayOfMonth/$monthOfYear/$year"
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
                    invoiceVieModel.deleteInvoice("AngE9676#254r5", invoiceItem.inid!!.toInt())
                        .observe(viewLifecycleOwner,
                            Observer {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                dialog?.dismiss()
                                findNavController().navigate(AddInvoiceFragmentDirections.actionAddInvoiceFragmentToInvoiceFragment())
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
            dateCell.addElement(Paragraph("Date: ${invoiceItem.date}"))
            dateCell.addElement(Paragraph("Quotation No. : ${invoiceItem.quotationNo}"))
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
            doc.add(Paragraph("Special instruction"))
            doc.add(Paragraph(invoiceItem.comment))
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
                Timber.e("Pdf saved in $filePath")
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
        taxDCell.addElement(Paragraph(invoiceItem.vat.toString()))
        taxDCell.setPadding(8f)
        table.addCell(taxDCell)
        val taxAmountCell = PdfPCell()
        val taxAmount = (invoiceItem.vat?.toDouble()?.div(100))?.times(subTotal)
        taxAmountCell.addElement(Paragraph("TAX AMOUNT"))
        taxAmountCell.setPadding(8f)
        table.addCell(taxAmountCell)
        val taxAmountDCell = PdfPCell()
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
        nameDataCell.addElement(Paragraph(invoiceItem.customerName))
        nameDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val addressCell = PdfPCell()
        addressCell.border = PdfPCell.NO_BORDER
        addressCell.addElement(Paragraph("Street Address"))
        val addressDataCell = PdfPCell()
        addressDataCell.border = PdfPCell.NO_BORDER
        addressDataCell.addElement(Paragraph(invoiceItem.street))
        addressDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val countryCell = PdfPCell()
        countryCell.border = PdfPCell.NO_BORDER
        countryCell.addElement(Paragraph("Country"))
        val countrydataCell = PdfPCell()
        countrydataCell.border = PdfPCell.NO_BORDER
        countrydataCell.addElement(Paragraph(invoiceItem.country))
        countrydataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val postCell = PdfPCell()
        postCell.border = PdfPCell.NO_BORDER
        postCell.addElement(Paragraph("Post Code"))
        val postDataCell = PdfPCell()
        postDataCell.border = PdfPCell.NO_BORDER
        postDataCell.addElement(Paragraph(invoiceItem.postCode))
        postDataCell.horizontalAlignment = Element.ALIGN_RIGHT
        val teleCell = PdfPCell()
        teleCell.border = PdfPCell.NO_BORDER
        teleCell.addElement(Paragraph("Telephone No"))
        val teleDataCell = PdfPCell()
        teleDataCell.border = PdfPCell.NO_BORDER
        teleDataCell.addElement(Paragraph(invoiceItem.telephone))
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

        unitAmountTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isNotEmpty()){
                        amount = s.toString().toDouble()
                        if (qtytv.text.isNotEmpty()&& discountAmountTv.text.isNotEmpty()){
                            invoiceVieModel.calculateAmount(singleItemQty,amount,discountAmount)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        discountAmountTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (s.isNotEmpty()){
                        discountAmount = s.toString().toDouble()
                        if (qtytv.text.isNotEmpty() && unitAmountTv.text.isNotEmpty()) {
                            invoiceVieModel.calculateAmount(singleItemQty,amount, discountAmount)
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
                        if (unitAmountTv.text.isNotEmpty() && discountAmountTv.text.isNotEmpty()) {
                            invoiceVieModel.calculateAmount(singleItemQty, amount, discountAmount)
                        }
                    }
                }
            }
        })

        invoiceVieModel.totalAmount.observe(viewLifecycleOwner, Observer {
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
            totalAmountTv.text = item.totalAmount.toString()
            addButton.text = getString(R.string.update)
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
                invoiceVieModel.setDefaultAmount()
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
                        addInvoiceBinding.invoiceAddItemTv.setText("No. of items: ${itemList.size}")
                        invoiceVieModel.addItemToInvoice(itemList)
                        dialog.dismiss()
                    }
                }
            }
        } else {
            invoiceVieModel.setDefaultAmount()
            addButton.setOnClickListener {
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
                        addedItemList.remove(item)
                        addedItemList.add(newItem)
                        addInvoiceBinding.invoiceAddItemTv.setText("No. of items: ${itemList.size}")
                        invoiceVieModel.addItemToInvoice(addedItemList)
                        dialog.dismiss()
                    }
                }
            }
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
}
