package com.example.jeffaccount.ui.home.supplier

import android.Manifest
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddSupplierFragmentBinding
import com.example.jeffaccount.model.SupPost
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


class AddSupplierFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddSupplierFragment()
    }

    private lateinit var viewModel: AddSupplierViewModel
    private lateinit var supplier:SupPost
    private lateinit var action:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val addsupplierBinding = AddSupplierFragmentBinding.inflate(inflater, container, false)

        val arguments= AddSupplierFragmentArgs.fromBundle(arguments!!)
        supplier = arguments.supplier
        action = arguments.update

        setHasOptionsMenu(true)

        if (action.equals(getString(R.string.edit))){
            addsupplierBinding.supplier = supplier
            addsupplierBinding.saveSupplierButton.visibility = View.GONE
            addsupplierBinding.supplierUpdateButton.visibility = View.VISIBLE
        }

        //Adding Text Watcher to name
        addsupplierBinding.supplierNameTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierNameTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierNameTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to the street address
        addsupplierBinding.supplierAddressTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierAddressTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierAddressTextInputLayout.isErrorEnabled = false
            }
        })

        //Adding Text watcher to post code
        addsupplierBinding.supplierPostCodeTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierPostCodeTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierPostCodeTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to telephone no.
        addsupplierBinding.supplierTelephoneTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierTelephoneTextInputLayout.isErrorEnabled = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierTelephoneTextInputLayout.isErrorEnabled = true
            }
        })

        //Add Text Watcher to email
        addsupplierBinding.supplierEmailTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierEmailTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierEmailTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to web address
        addsupplierBinding.supplierWebTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierWebTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierWebTextInputLayout.isErrorEnabled = false
            }
        })

        //Adding Text Watcher to street
        addsupplierBinding.saveSupplierButton.setOnClickListener {

            val name = addsupplierBinding.supplierNameTextInput.text.toString()
            val street = addsupplierBinding.supplierAddressTextInput.text.toString()
            val country = addsupplierBinding.supplierCountryTv.text.toString()
            val postCode = addsupplierBinding.supplierPostCodeTextInput.text.toString()
            val telephone = addsupplierBinding.supplierTelephoneTextInput.text.toString()
            val email = addsupplierBinding.supplierEmailTextInput.text.toString()
            val web = addsupplierBinding.supplierWebTextInput.text.toString()

            when{
                name.isEmpty()->{
                    addsupplierBinding.supplierNameTextInputLayout.error = getString(R.string.add_name)
                }
                street.isEmpty()->{
                    addsupplierBinding.supplierAddressTextInputLayout.error = getString(R.string.add_address)
                }
                postCode.isEmpty()->{
                    addsupplierBinding.supplierPostCodeTextInputLayout.error = getString(R.string.enter_post_code)
                }
                telephone.isEmpty()->{
                    addsupplierBinding.supplierTelephoneTextInputLayout.error = getString(R.string.add_phone_no)
                }
                email.isEmpty()->{
                    addsupplierBinding.supplierEmailTextInputLayout.error = getString(R.string.add_email)
                }
                web.isEmpty()->{
                    addsupplierBinding.supplierWebTextInputLayout.error = getString(R.string.add_web_address)
                }
                else ->{
                    addSupplier(name, street, country, postCode, telephone, email, web)
                }
            }

        }

        //Set on click listener to update button
        addsupplierBinding.supplierUpdateButton.setOnClickListener {
            val name = addsupplierBinding.supplierNameTextInput.text.toString()
            val street = addsupplierBinding.supplierAddressTextInput.text.toString()
            val country = addsupplierBinding.supplierCountryTv.text.toString()
            val postCode = addsupplierBinding.supplierPostCodeTextInput.text.toString()
            val telephone = addsupplierBinding.supplierTelephoneTextInput.text.toString()
            val email = addsupplierBinding.supplierEmailTextInput.text.toString()
            val web = addsupplierBinding.supplierWebTextInput.text.toString()

            when{
                name.isEmpty()->{
                    addsupplierBinding.supplierNameTextInputLayout.error = getString(R.string.add_name)
                }
                street.isEmpty()->{
                    addsupplierBinding.supplierAddressTextInputLayout.error = getString(R.string.add_address)
                }
                postCode.isEmpty()->{
                    addsupplierBinding.supplierPostCodeTextInputLayout.error = getString(R.string.enter_post_code)
                }
                telephone.isEmpty()->{
                    addsupplierBinding.supplierTelephoneTextInputLayout.error = getString(R.string.add_phone_no)
                }
                email.isEmpty()->{
                    addsupplierBinding.supplierEmailTextInputLayout.error = getString(R.string.add_email)
                }
                web.isEmpty()->{
                    addsupplierBinding.supplierWebTextInputLayout.error = getString(R.string.add_web_address)
                }
                else ->{
                    updateSupplier(supplier.supid!!,name, street, country, postCode, telephone, email, web)
                }
            }
        }
        return addsupplierBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddSupplierViewModel::class.java)
        // TODO: Use the ViewModel
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
        if (id == R.id.delete_item){

            val layout = LayoutInflater.from(context).inflate(R.layout.delete_confirmation,null)
            val builder = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
            builder?.setCancelable(false)
            builder?.setView(layout)
            val dialog = builder?.create()
            dialog?.show()

            val delButton = layout.findViewById<Button>(R.id.delete_button)
            val canButton: Button = layout.findViewById(R.id.cancel_del_button)

            delButton.setOnClickListener {
                dialog?.dismiss()
                supplier.supid?.let {
                    viewModel.deleteSupplier(supplier.supid!!).observe(viewLifecycleOwner, Observer {
                        Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                        findNavController().navigate(AddSupplierFragmentDirections.actionAddSupplierFragmentToSupplierFragment())
                    })
                }
            }

            canButton.setOnClickListener {
                dialog?.dismiss()
            }

        }else if(id == R.id.convert_pdf_item){
            createPdf()
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
        val mFileName = "jeff_account_." + SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val filePath = folder.absolutePath + "/" + mFileName + ".pdf"

        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(filePath))
            mDoc.open()
            val lineSeparator = LineSeparator()
            lineSeparator.lineColor = BaseColor.WHITE

            val mChunk = Chunk(
                getString(R.string.supplier)
                , Font(Font.FontFamily.TIMES_ROMAN, 24.0f)
            )
            val title = Paragraph(mChunk)
            title.alignment = Element.ALIGN_CENTER
            mDoc.add(title)
            mDoc.add(lineSeparator)
            mDoc.add(Paragraph(" "))
            val supplierBody = Paragraph()
            createSupplierTable(supplierBody)
            mDoc.add(supplierBody)
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

    private fun createSupplierTable(supplierBody: Paragraph) {

        val table = PdfPTable(floatArrayOf(5f,5f))
        table.widthPercentage = 100f
        table.defaultCell.isUseAscender = true

        val nameCell = PdfPCell(Phrase("Name"))
        nameCell.paddingLeft = 8f
        nameCell.paddingBottom = 8f
        val nameDCell = PdfPCell(Phrase(supplier.supname))
        nameDCell.paddingLeft = 8f
        nameDCell.paddingBottom = 8f
        val addressCell = PdfPCell(Phrase("Street Address"))
        addressCell.paddingLeft = 8f
        addressCell.paddingBottom = 8f
        val addrDCell = PdfPCell(Phrase(supplier.street))
        addrDCell.paddingBottom = 8f
        addrDCell.paddingLeft = 8f
        val countryCell = PdfPCell(Phrase("Country"))
        countryCell.paddingLeft = 8f
        countryCell.paddingBottom = 8f
        val countryDCell = PdfPCell(Phrase(supplier.country))
        countryDCell.paddingBottom = 8f
        countryDCell.paddingLeft = 8f
        val postCell = PdfPCell(Phrase("Post Code"))
        val postDCell = PdfPCell(Phrase(supplier.postcode))
        postCell.paddingLeft = 8f
        postCell.paddingBottom = 8f
        postDCell.paddingBottom = 8f
        postDCell.paddingLeft = 8f
        val phoneCell = PdfPCell(Phrase("Telephone No"))
        val phoneDCell = PdfPCell(Phrase(supplier.telephone))
        val emailCell = PdfPCell(Phrase("Email Address"))
        val emailDCell = PdfPCell(Phrase(supplier.supemail))
        val webCell = PdfPCell(Phrase("Web Address"))
        val webDCell = PdfPCell(Phrase(supplier.web))
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
        supplierBody.add(table)
    }

    //Add Supplier
    private fun addSupplier(
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        viewModel.addSupplier(supplierName, streetAdd, coutry, postCode, telephone, email, web)
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(AddSupplierFragmentDirections.actionAddSupplierFragmentToSupplierFragment())
                }
            })
    }

    //Update supplier
    private fun updateSupplier(
        supplierId:String,
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ){
        viewModel.updateSupplier(supplierId, supplierName, streetAdd, coutry, postCode, telephone, email, web).observe(viewLifecycleOwner,
            Observer {
                it?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(AddSupplierFragmentDirections.actionAddSupplierFragmentToSupplierFragment())
                }
            })
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
