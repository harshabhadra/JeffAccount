package com.example.jeffaccount.ui.home.purchase

import android.Manifest
import android.content.Intent
import android.graphics.fonts.FontStyle
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddPurchaseFragmentBinding
import com.example.jeffaccount.model.PurchasePost
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
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class AddPurchaseFragment : Fragment() {

    private lateinit var purchaseBinding: AddPurchaseFragmentBinding
    private lateinit var action: String
    private lateinit var purchase: PurchasePost
    private var _vat: Double = 0.0
    private var qty: Int = 0
    private var _unitAmount: Double = 0.0
    private var _advanceAmount: Double = 0.0
    private var _discountAmount: Double = 0.0
    private var _totalAmount: Double = 0.0

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

        //Set on click listener to add quantity button
        purchaseBinding.purchaseQtyPlusButton.setOnClickListener {
            if (qty >= 0) {
                qty++
                viewModel.changeQuantity(qty)
            }
        }

        //Set on click listener to subtract quantity value
        purchaseBinding.purchaseQtyMinusButton.setOnClickListener {
            if (qty > 0) {
                qty--
                viewModel.changeQuantity(qty)
            }
        }

        //Set on click listener to save purchase button
        purchaseBinding.purchaseSaveButton.setOnClickListener {
            val jobNo = purchaseBinding.purchaseJobnoTextInput.text.toString()
            val quotationNo = purchaseBinding.purchaseQuotationTextInput.text.toString()
            val vat = purchaseBinding.purchaseVatTextInput.text.toString()
            if (vat.isNotEmpty()) {
                _vat = vat.toDouble()
            }
            val date = purchaseBinding.purchaseDateTextInput.text.toString()
            val supplierName = purchaseBinding.purchaseSupplierTextInput.text.toString()
            val comment = purchaseBinding.purchaseCommentTextInput.text.toString()
            val itemDes = purchaseBinding.purchaseItemdesTextInput.text.toString()
            val paymentMethod = purchaseBinding.purchasePaymentMethodTextInput.text.toString()
            val advanceAmount = purchaseBinding.purchaseAdvanceAmountTextInput.text.toString()
            if (advanceAmount.isNotEmpty()) {
                _advanceAmount = advanceAmount.toDouble()
            }
            val unitAmount = purchaseBinding.purchaseUnitAmountTextInput.text.toString()
            if (unitAmount.isNotEmpty()) {
                _unitAmount = unitAmount.toDouble()
            }
            val discountAmount = purchaseBinding.purchaseDiscountAmountTextInput.text.toString()
            if (discountAmount.isNotEmpty()) {
                _discountAmount = discountAmount.toDouble()
            }
            val totalAmount = purchaseBinding.purchaseTotalAmountTextinput.text.toString()
            if (totalAmount.isNotEmpty()) {
                _totalAmount = totalAmount.toDouble()
            }

            when {
                jobNo.isEmpty() -> purchaseBinding.purchaseJobnoTextInputLayout.error =
                    getString(R.string.enter_job_no)
                quotationNo.isEmpty() -> purchaseBinding.purchaseQuotationTextInputLayout.error =
                    getString(R.string.enter_quotation_no)
                vat.isEmpty() -> purchaseBinding.purchaseVatTextInputLayout.error =
                    getString(R.string.enter_vat)
                (qty == 0) -> Toast.makeText(context, "Quantity can't be zero", Toast.LENGTH_SHORT)
                    .show()
                date.isEmpty() -> purchaseBinding.purchaseDateTextInputLayout.error =
                    getString(R.string.enter_date)
                supplierName.isEmpty() -> purchaseBinding.purchaseSupplierTextInputLayout.error =
                    getString(R.string.enter_supplier_name)
                comment.isEmpty() -> purchaseBinding.purchaseCommentTextInputLayout.error =
                    getString(R.string.enter_comment)
                itemDes.isEmpty() -> purchaseBinding.purchaseItemdesTextInputLayout.error =
                    getString(R.string.enter_item_des)
                paymentMethod.isEmpty() -> purchaseBinding.purchasePaymentMethodTextInputLayout.error =
                    getString(
                        R.string.enter_payment_method
                    )
                unitAmount.isEmpty() -> purchaseBinding.purchaseUnitAmountTextInputLayout.error =
                    getString(
                        R.string.enter_unit_amount
                    )
                advanceAmount.isEmpty() -> purchaseBinding.purchaseAdvanceAmountTextInputLayout.error =
                    getString(
                        R.string.enter_advance_amount
                    )
                discountAmount.isEmpty() -> purchaseBinding.purchaseDiscountAmountTextInputLayout.error =
                    getString(
                        R.string.enter_discount_amount
                    )
                totalAmount.isEmpty() -> purchaseBinding.purchaseTotalAmountTextinputlayout.error =
                    getString(
                        R.string.enter_total_amount
                    )
                else -> {
                    viewModel.addPurchase(
                        jobNo,
                        quotationNo,
                        _vat,
                        date,
                        supplierName,
                        comment,
                        itemDes,
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

        //Set on click listener to update button
        purchaseBinding.purchaseEditButton.setOnClickListener {
            val jobNo = purchaseBinding.purchaseJobnoTextInput.text.toString()
            val quotationNo = purchaseBinding.purchaseQuotationTextInput.text.toString()
            val vat = purchaseBinding.purchaseVatTextInput.text.toString()
            if (vat.isNotEmpty()) {
                _vat = vat.toDouble()
            }
            val date = purchaseBinding.purchaseDateTextInput.text.toString()
            val supplierName = purchaseBinding.purchaseSupplierTextInput.text.toString()
            val comment = purchaseBinding.purchaseCommentTextInput.text.toString()
            val itemDes = purchaseBinding.purchaseItemdesTextInput.text.toString()
            val paymentMethod = purchaseBinding.purchasePaymentMethodTextInput.text.toString()
            val advanceAmount = purchaseBinding.purchaseAdvanceAmountTextInput.text.toString()
            if (advanceAmount.isNotEmpty()) {
                _advanceAmount = advanceAmount.toDouble()
            }
            val unitAmount = purchaseBinding.purchaseUnitAmountTextInput.text.toString()
            if (unitAmount.isNotEmpty()) {
                _unitAmount = unitAmount.toDouble()
            }
            val discountAmount = purchaseBinding.purchaseDiscountAmountTextInput.text.toString()
            if (discountAmount.isNotEmpty()) {
                _discountAmount = discountAmount.toDouble()
            }
            val totalAmount = purchaseBinding.purchaseTotalAmountTextinput.text.toString()
            if (totalAmount.isNotEmpty()) {
                _totalAmount = totalAmount.toDouble()
            }

            when {
                jobNo.isEmpty() -> purchaseBinding.purchaseJobnoTextInputLayout.error =
                    getString(R.string.enter_job_no)
                quotationNo.isEmpty() -> purchaseBinding.purchaseQuotationTextInputLayout.error =
                    getString(R.string.enter_quotation_no)
                vat.isEmpty() -> purchaseBinding.purchaseVatTextInputLayout.error =
                    getString(R.string.enter_vat)
                (qty == 0) -> Toast.makeText(context, "Quantity can't be zero", Toast.LENGTH_SHORT)
                    .show()
                date.isEmpty() -> purchaseBinding.purchaseDateTextInputLayout.error =
                    getString(R.string.enter_date)
                supplierName.isEmpty() -> purchaseBinding.purchaseSupplierTextInputLayout.error =
                    getString(R.string.enter_supplier_name)
                comment.isEmpty() -> purchaseBinding.purchaseCommentTextInputLayout.error =
                    getString(R.string.enter_comment)
                itemDes.isEmpty() -> purchaseBinding.purchaseItemdesTextInputLayout.error =
                    getString(R.string.enter_item_des)
                paymentMethod.isEmpty() -> purchaseBinding.purchasePaymentMethodTextInputLayout.error =
                    getString(
                        R.string.enter_payment_method
                    )
                unitAmount.isEmpty() -> purchaseBinding.purchaseUnitAmountTextInputLayout.error =
                    getString(
                        R.string.enter_unit_amount
                    )
                advanceAmount.isEmpty() -> purchaseBinding.purchaseAdvanceAmountTextInputLayout.error =
                    getString(
                        R.string.enter_advance_amount
                    )
                discountAmount.isEmpty() -> purchaseBinding.purchaseDiscountAmountTextInputLayout.error =
                    getString(
                        R.string.enter_discount_amount
                    )
                totalAmount.isEmpty() -> purchaseBinding.purchaseTotalAmountTextinputlayout.error =
                    getString(
                        R.string.enter_total_amount
                    )
                else -> {
                    viewModel.updatePurchase(
                        purchase.pid!!.toInt(),
                        jobNo,
                        quotationNo,
                        _vat,
                        date,
                        supplierName,
                        comment,
                        itemDes,
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

        purchaseBinding.purchaseVatTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseVatTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseVatTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseSupplierTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                purchaseBinding.purchaseSupplierTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseSupplierTextInputLayout.isErrorEnabled = false
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

        purchaseBinding.purchaseItemdesTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseItemdesTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseItemdesTextInputLayout.isErrorEnabled = false
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

        purchaseBinding.purchaseUnitAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseUnitAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseUnitAmountTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseAdvanceAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseAdvanceAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseAdvanceAmountTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseDiscountAmountTextInput.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseDiscountAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseDiscountAmountTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseTotalAmountTextinput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseTotalAmountTextinputlayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseTotalAmountTextinputlayout.isErrorEnabled = false
            }
        })
        return purchaseBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)

        requestReadPermissions()

        //Observe quantity value from viewModel
        viewModel.purchaseQuantityValue.observe(viewLifecycleOwner, Observer {
            it?.let {
                purchaseBinding.purchaseQtyTv.text = it.toString()
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
                getString(R.string.purchase)
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
}
