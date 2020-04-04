package com.example.jeffaccount.ui.home.company

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
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.example.jeffaccount.R
import com.example.jeffaccount.createPreviewDialog
import com.example.jeffaccount.databinding.FragmentAddCompanyBinding
import com.example.jeffaccount.model.ComPost
import com.example.jeffaccount.ui.MainActivity
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

/**
 * A simple [Fragment] subclass.
 */
class AddCompanyFragment : Fragment() {

    private lateinit var addCompanyBinding: FragmentAddCompanyBinding
    private lateinit var addCompanyViewModel: CompanyViewModel
    private lateinit var action: String
    private lateinit var companyItem: ComPost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addCompanyBinding = FragmentAddCompanyBinding.inflate(inflater, container, false)

        //Initialize ViewModel class
        addCompanyViewModel = ViewModelProvider(this).get(CompanyViewModel::class.java)

        setHasOptionsMenu(true)

        requestReadPermissions()

        val activity = activity as MainActivity
        activity.setToolbarText("Add Company")

        val arguments = AddCompanyFragmentArgs.fromBundle(arguments!!)
        action = arguments.action
        if (action.equals(getString(R.string.update))) {
            addCompanyBinding.companySaveButton.visibility = View.GONE
            addCompanyBinding.companyUpdateButton.visibility = View.VISIBLE
            companyItem = arguments.companyItem!!
            addCompanyBinding.company = companyItem
        }

        //Set on click listener to save button
        addCompanyBinding.companySaveButton.setOnClickListener {
            val name = addCompanyBinding.companyNameTextInput.text.toString()
            val street = addCompanyBinding.companyStreetTextInput.text.toString()
            val country = addCompanyBinding.companyCountryTv.text.toString()
            val postCode = addCompanyBinding.companyPostCodeTextInput.text.toString()
            val telephone = addCompanyBinding.companyTelephoneTextInput.text.toString()
            val email = addCompanyBinding.companyEmailTextInput.text.toString()
            val web = addCompanyBinding.companyWebTextInput.text.toString()

            when {
                name.isEmpty() -> addCompanyBinding.companyNameTextInputLayout.error =
                    getString(R.string.enter_company_name)
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Save this item?")
                    builder.setPositiveButton("Save",DialogInterface.OnClickListener{dialog, which ->
                        addCompanyViewModel.addCompany(
                            name,
                            street,
                            country,
                            postCode,
                            telephone,
                            email,
                            web
                        )
                            .observe(viewLifecycleOwner,
                                Observer {
                                    it?.let {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(AddCompanyFragmentDirections.actionAddCompanyFragmentToCompanyFragment())
                                    }
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
        addCompanyBinding.companyUpdateButton.setOnClickListener {
            val name = addCompanyBinding.companyNameTextInput.text.toString()
            val street = addCompanyBinding.companyStreetTextInput.text.toString()
            val country = addCompanyBinding.companyCountryTv.text.toString()
            val postCode = addCompanyBinding.companyPostCodeTextInput.text.toString()
            val telephone = addCompanyBinding.companyTelephoneTextInput.text.toString()
            val email = addCompanyBinding.companyEmailTextInput.text.toString()
            val web = addCompanyBinding.companyWebTextInput.text.toString()

            when {
                name.isEmpty() -> addCompanyBinding.companyNameTextInputLayout.error =
                    getString(R.string.enter_company_name)
                else -> {
                    addCompanyViewModel.updateCompany(
                            companyItem.comid!!.toInt(),
                            name,
                            street,
                            country,
                            postCode,
                            telephone,
                            email,
                            web
                        )
                        .observe(viewLifecycleOwner,
                            Observer {
                                it?.let {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(AddCompanyFragmentDirections.actionAddCompanyFragmentToCompanyFragment())
                                }
                            })
                }
            }
        }

        //Adding text watcher to all input fields
        addCompanyBinding.companyNameTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyNameTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyNameTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyStreetTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyStreetTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyStreetTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyPostCodeTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyPostCodeTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyPostCodeTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyTelephoneTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyTelephoneTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyTelephoneTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyEmailTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyEmailTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyEmailTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyWebTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyWebTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyWebTextInputLayout.isErrorEnabled = false
            }
        })
        return addCompanyBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (action.equals(getString(R.string.update))) {
            inflater.inflate(R.menu.company_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId
        when (itemId) {
            R.id.company_delete -> {
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
                    addCompanyViewModel.deleteCompany(companyItem.comid!!.toInt())
                        .observe(viewLifecycleOwner,
                            Observer {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(AddCompanyFragmentDirections.actionAddCompanyFragmentToCompanyFragment())
                            })
                }

                canButton.setOnClickListener {
                    dialog?.dismiss()
                }
            }
            R.id.convert_pdf_item->{
                createPdf()
            }
//            R.id.company_purchase->{
//                findNavController().navigate(AddCompanyFragmentDirections.actionAddCompanyFragmentToAddPurchaseFragment(
//                    null,getString(R.string.company_details),companyItem,null
//                ))
//            }
        }
        return true
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
                getString(R.string.company)
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
             createPreviewDialog(filePath,context!!, activity!!)
            }
        } catch (e: Exception) {

            Timber.e(e)
        }
    }

    private fun createCustomerTable(companyBody: Paragraph) {

        val table = PdfPTable(floatArrayOf(5f, 5f))
        table.widthPercentage = 100f
        table.defaultCell.isUseAscender = true

        val nameCell = PdfPCell(Phrase("Name"))
        nameCell.paddingLeft = 8f
        nameCell.paddingBottom = 8f
        val nameDCell = PdfPCell(Phrase(companyItem.comname))
        nameDCell.paddingLeft = 8f
        nameDCell.paddingBottom = 8f
        val addressCell = PdfPCell(Phrase("Street Address"))
        addressCell.paddingLeft = 8f
        addressCell.paddingBottom = 8f
        val addrDCell = PdfPCell(Phrase(companyItem.street))
        addrDCell.paddingBottom = 8f
        addrDCell.paddingLeft = 8f
        val countryCell = PdfPCell(Phrase("Country"))
        countryCell.paddingLeft = 8f
        countryCell.paddingBottom = 8f
        val countryDCell = PdfPCell(Phrase(companyItem.country))
        countryDCell.paddingBottom = 8f
        countryDCell.paddingLeft = 8f
        val postCell = PdfPCell(Phrase("Post Code"))
        val postDCell = PdfPCell(Phrase(companyItem.postcode))
        postCell.paddingLeft = 8f
        postCell.paddingBottom = 8f
        postDCell.paddingBottom = 8f
        postDCell.paddingLeft = 8f
        val phoneCell = PdfPCell(Phrase("Telephone No"))
        val phoneDCell = PdfPCell(Phrase(companyItem.telephone))
        val emailCell = PdfPCell(Phrase("Email Address"))
        val emailDCell = PdfPCell(Phrase(companyItem.comemail))
        val webCell = PdfPCell(Phrase("Web Address"))
        val webDCell = PdfPCell(Phrase(companyItem.web))
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
        companyBody.add(table)
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
