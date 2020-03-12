package com.example.jeffaccount.ui.home.customer

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddCustomerFragmentBinding
import com.example.jeffaccount.model.Post
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
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*

private var loadingDialog: androidx.appcompat.app.AlertDialog? = null

class AddCustomerFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddCustomerFragment()
    }

    private lateinit var viewModel: CustomerViewModel
    private lateinit var customer: Post
    private lateinit var action: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val addCustomerBinding = AddCustomerFragmentBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
//        Getting arguments
        val arguments = AddCustomerFragmentArgs.fromBundle(arguments!!)
        customer = arguments.post
        action = arguments.add

        if (action == getString(R.string.edit)) {
            addCustomerBinding.customer = customer
            addCustomerBinding.updateCustomerButton.visibility = View.VISIBLE
            addCustomerBinding.customerSaveButton.visibility = View.GONE

        }

        //Set on click listener to save button
        addCustomerBinding.customerSaveButton.setOnClickListener {

            val name = addCustomerBinding.customerNameTextInput.text.toString()
            val street = addCustomerBinding.customerAddressTextInput.text.toString()
            val country = addCustomerBinding.customerCountryTv.text.toString()
            val postCode = addCustomerBinding.customerPostCodeTextInput.text.toString()
            val phone = addCustomerBinding.customerTelephoneTextInput.text.toString()
            val email = addCustomerBinding.customerEmailTextInput.text.toString()
            val web = addCustomerBinding.customerWebTextInput.text.toString()

            when {
                name.isEmpty() -> {
                    addCustomerBinding.customerNameTextInputLayout.error =
                        getString(R.string.add_name)
                }
                street.isEmpty() -> {
                    addCustomerBinding.customerAddressTextInputLayout.error =
                        getString(R.string.add_address)
                }
                postCode.isEmpty() -> {
                    addCustomerBinding.customerPostCodeTextInputLayout.error =
                        getString(R.string.enter_post_code)
                }
                phone.isEmpty() -> {
                    addCustomerBinding.customerTelephoneTextInputLayout.error =
                        getString(R.string.add_phone_no)
                }
                email.isEmpty() -> {
                    addCustomerBinding.customerEmailTextInputLayout.error =
                        getString(R.string.add_email)
                }
                web.isEmpty() -> {
                    addCustomerBinding.customerWebTextInputLayout.error =
                        getString(R.string.add_web_address)
                }
                else -> {
                    loadingDialog = createLoadingDialog()
                    loadingDialog?.show()
                    addCustomer(name, street, country, postCode, phone, email, web)
                }
            }
        }

        //Set OnClick listener to Update Button
        addCustomerBinding.updateCustomerButton.setOnClickListener {

            val name = addCustomerBinding.customerNameTextInput.text.toString()
            val street = addCustomerBinding.customerAddressTextInput.text.toString()
            val country = addCustomerBinding.customerCountryTv.text.toString()
            val postCode = addCustomerBinding.customerPostCodeTextInput.text.toString()
            val phone = addCustomerBinding.customerTelephoneTextInput.text.toString()
            val email = addCustomerBinding.customerEmailTextInput.text.toString()
            val web = addCustomerBinding.customerWebTextInput.text.toString()

            when {
                name.isEmpty() -> {
                    addCustomerBinding.customerNameTextInputLayout.error =
                        getString(R.string.add_name)
                }
                street.isEmpty() -> {
                    addCustomerBinding.customerAddressTextInputLayout.error =
                        getString(R.string.add_address)
                }
                postCode.isEmpty() -> {
                    addCustomerBinding.customerPostCodeTextInputLayout.error =
                        getString(R.string.enter_post_code)
                }
                phone.isEmpty() -> {
                    addCustomerBinding.customerTelephoneTextInputLayout.error =
                        getString(R.string.add_phone_no)
                }
                email.isEmpty() -> {
                    addCustomerBinding.customerEmailTextInputLayout.error =
                        getString(R.string.add_email)
                }
                web.isEmpty() -> {
                    addCustomerBinding.customerWebTextInputLayout.error =
                        getString(R.string.add_web_address)
                }
                else -> {
                    loadingDialog = createLoadingDialog()
                    loadingDialog?.show()
                    updateCustomer(
                        customer.custid!!,
                        name,
                        street,
                        country,
                        postCode,
                        phone,
                        email,
                        web
                    )
                }
            }
        }

        //Adding text watcher to customer name
        addCustomerBinding.customerNameTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerNameTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerNameTextInputLayout.isErrorEnabled = false
            }

        })

        //Adding text watcher to street address
        addCustomerBinding.customerAddressTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerAddressTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerAddressTextInputLayout.isErrorEnabled = false
            }

        })

        //Add Text watcher to post code
        addCustomerBinding.customerPostCodeTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerPostCodeTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerPostCodeTextInputLayout.isErrorEnabled = false
            }

        })

        //Add Text watcher to telephone no
        addCustomerBinding.customerTelephoneTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerTelephoneTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerTelephoneTextInputLayout.isErrorEnabled = false
            }

        })

        //Add Text Watcher to email input
        addCustomerBinding.customerEmailTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                addCustomerBinding.customerEmailTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerEmailTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to web
        addCustomerBinding.customerWebTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerWebTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerWebTextInputLayout.isErrorEnabled = false
            }
        })

        return addCustomerBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)
        requestReadPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (action.equals(getString(R.string.edit))) {
            inflater.inflate(R.menu.main_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.delete_item) {
            val layout = LayoutInflater.from(context).inflate(R.layout.delete_confirmation, null)
            val builder = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
            builder?.setCancelable(false)
            builder?.setView(layout)
            val dialog = builder?.create()
            dialog?.show()

            val delButton = layout.findViewById<Button>(R.id.delete_button)
            val canButton: Button = layout.findViewById(R.id.cancel_del_button)

            delButton.setOnClickListener {
                loadingDialog = createLoadingDialog()
                loadingDialog?.show()
                deleteUser(customer.custid!!)
                dialog?.dismiss()
            }

            canButton.setOnClickListener {
                dialog?.dismiss()
            }

        } else if (id == R.id.convert_pdf_item) {
            Timber.e("Pdf click")
            createPdf()
        }
        return true
    }

    //Add Customer
    private fun addCustomer(
        customerName: String,
        streetAdd: String,
        coutry: String = getString(R.string.united_kingdom),
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        viewModel.addCustomer(customerName, streetAdd, coutry, postCode, telephone, email, web)
            .observe(viewLifecycleOwner,
                Observer {
                    it?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        loadingDialog?.dismiss()
                        findNavController().navigate(AddCustomerFragmentDirections.actionAddCustomerToCustomerFragment())
                    }
                })
    }

    //Update Customer
    private fun updateCustomer(
        customerId: String,
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        viewModel.updateCustomer(
            customerId, customerName,
            streetAdd, coutry, postCode, telephone, email, web
        ).observe(viewLifecycleOwner,
            Observer {
                it?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    loadingDialog?.dismiss()
                    findNavController().navigate(AddCustomerFragmentDirections.actionAddCustomerToCustomerFragment())
                }
            })
    }

    //Delete User
    private fun deleteUser(customerId: String) {
        viewModel.deleteUser(customerId).observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                loadingDialog?.dismiss()
                findNavController().navigate(AddCustomerFragmentDirections.actionAddCustomerToCustomerFragment())
            } ?: let {
                loadingDialog?.dismiss()
            }
        })
    }

    //Create a loading Dialog
    private fun createLoadingDialog(): androidx.appcompat.app.AlertDialog? {
        val layout = LayoutInflater.from(context).inflate(R.layout.loading_layout, null)
        val builder = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
        builder?.setCancelable(false)
        builder?.setView(layout)
        return builder?.create()
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

            val jeffChunk = Chunk(
                getString(R.string.app_name), Font(Font.FontFamily.TIMES_ROMAN,32.0f)
            )
            val heading = Paragraph(jeffChunk)
            heading.alignment = Element.ALIGN_CENTER
            val mChunk = Chunk(
                getString(R.string.customer)
                , Font(Font.FontFamily.TIMES_ROMAN, 24.0f)
            )
            val title = Paragraph(mChunk)
            title.alignment = Element.ALIGN_CENTER
            mDoc.add(heading)
            mDoc.add(title)
            mDoc.add(lineSeparator)
            mDoc.add(Paragraph(" "))
            val customerBody = Paragraph()
            createCustomerTable(customerBody)
            mDoc.add(customerBody)
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

    private fun createCustomerTable(customerBody: Paragraph) {

        val table = PdfPTable(floatArrayOf(5f, 5f))
        table.widthPercentage = 100f
        table.defaultCell.isUseAscender = true

        val nameCell = PdfPCell(Phrase("Name"))
        nameCell.paddingLeft = 8f
        nameCell.paddingBottom = 8f
        val nameDCell = PdfPCell(Phrase(customer.custname))
        nameDCell.paddingLeft = 8f
        nameDCell.paddingBottom = 8f
        val addressCell = PdfPCell(Phrase("Street Address"))
        addressCell.paddingLeft = 8f
        addressCell.paddingBottom = 8f
        val addrDCell = PdfPCell(Phrase(customer.street))
        addrDCell.paddingBottom = 8f
        addrDCell.paddingLeft = 8f
        val countryCell = PdfPCell(Phrase("Country"))
        countryCell.paddingLeft = 8f
        countryCell.paddingBottom = 8f
        val countryDCell = PdfPCell(Phrase(customer.country))
        countryDCell.paddingBottom = 8f
        countryDCell.paddingLeft = 8f
        val postCell = PdfPCell(Phrase("Post Code"))
        val postDCell = PdfPCell(Phrase(customer.postcode))
        postCell.paddingLeft = 8f
        postCell.paddingBottom = 8f
        postDCell.paddingBottom = 8f
        postDCell.paddingLeft = 8f
        val phoneCell = PdfPCell(Phrase("Telephone No"))
        val phoneDCell = PdfPCell(Phrase(customer.telephone))
        val emailCell = PdfPCell(Phrase("Email Address"))
        val emailDCell = PdfPCell(Phrase(customer.customeremail))
        val webCell = PdfPCell(Phrase("Web Address"))
        val webDCell = PdfPCell(Phrase(customer.web))
        phoneCell.paddingLeft = 8f
        phoneDCell.paddingLeft = 8f
        emailCell.paddingLeft = 8f
        emailDCell.paddingLeft = 8f
        webCell.paddingLeft = 8f
        webDCell.paddingLeft = 8f
        webDCell.paddingBottom = 8f
        webCell.paddingBottom = 8f
        phoneCell.paddingBottom = 8f
        phoneDCell.paddingBottom = 8f
        emailCell.paddingBottom = 8f
        emailDCell.paddingBottom = 8f
        table.addCell(nameCell)
        table.addCell(nameDCell)
        table.addCell(addressCell)
        table.addCell(addrDCell)
        table.addCell(countryCell)
        table.addCell(countryDCell)
        table.addCell(postCell)
        table.addCell(postDCell)
        table.addCell(phoneCell)
        table.addCell(phoneDCell)
        table.addCell(emailCell)
        table.addCell(emailDCell)
        table.addCell(webCell)
        table.addCell(webDCell)
        customerBody.add(table)
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
}
