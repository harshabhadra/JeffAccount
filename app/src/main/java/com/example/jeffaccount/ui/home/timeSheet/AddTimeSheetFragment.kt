package com.example.jeffaccount.ui.home.timeSheet

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentAddTimeSheetBinding
import com.example.jeffaccount.model.CompanyDetails
import com.example.jeffaccount.model.Logo
import com.example.jeffaccount.model.TimeSheetPost
import com.example.jeffaccount.network.*
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.*
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

/**
 * A simple [Fragment] subclass.
 */
class AddTimeSheetFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    OnWorkerSaveClickListener, OnSearchItemClickListener, OnSearchSupplierClickListener,
    OnCustomerNameClickListener, OnSupplierNameClickListener, OnQuotationJobNoClickListener,
    OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener {

    private lateinit var addTimeSheetBinding: FragmentAddTimeSheetBinding
    private lateinit var viewModel: TimeSheetViewModel
    private lateinit var filePath: String
    private lateinit var workerListAdapter: WorkerListAdapter
    private lateinit var worker: WorkerList
    private lateinit var action: String
    private var custNameList: MutableList<String> = mutableListOf()
    private var listWorker: WorkerList? = null
    private var workersList: MutableList<WorkerList> = mutableListOf()
    private lateinit var comId: String
    private var noOfWorker: Int = 0
    private lateinit var timeSheet: TimeSheetPost
    private lateinit var companyDetails: CompanyDetails
    private var logoList: MutableList<Logo> = mutableListOf()
    private var bmpList: MutableList<Bitmap> = mutableListOf()
    private lateinit var companyBitmap: Bitmap
    private var customerName: String = ""
    private var customerStreet: String = ""
    private var customerPostCode: String = ""
    private var customerTelephone: String = ""
    private var customerEmail: String = ""
    private var customerWeb: String = ""
    private var customerCountry: String = ""
    private var customerCounty: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Timber.e("OnCreateView")
        // Inflate the layout for this fragment
        addTimeSheetBinding = FragmentAddTimeSheetBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(TimeSheetViewModel::class.java)

        requestReadPermissions()
        val activity = activity as MainActivity
        activity.setToolbarText("Add Time sheet")
        companyDetails = activity.companyDetails
        companyDetails.caomimge?.let {
            viewModel.getCompanyBitmap("https://alphabusinessdesigns.com/wordpress/appproject/jtapp/$it")
        }
        comId = activity.companyDetails.comid


        //Initializing worker adapter
        workerListAdapter = WorkerListAdapter(WorkerItemClickListener {
            listWorker = it
            createChoiceDialog(it)
        })

        //set up worker recycler with the adapter
        addTimeSheetBinding.workerListRecyclerView.adapter = workerListAdapter

        //Getting arguments
        val arguments = AddTimeSheetFragmentArgs.fromBundle(getArguments()!!)
        action = arguments.action!!
        if (action == getString(R.string.update)) {
            timeSheet = arguments.timeSheetItem!!
            addTimeSheetBinding.timeSheet = timeSheet
            addTimeSheetBinding.timeSheetJobNoTextInput.setText(timeSheet.jobNo)
            addTimeSheetBinding.tsQuotationNoTextInput.setText(timeSheet.quotationNo)
            addTimeSheetBinding.tsSaveButton.visibility = View.GONE
            addTimeSheetBinding.tsUpdateButton.visibility = View.VISIBLE
            val list = timeSheet.workerList
            list?.let {
                for (worker in list) {
                    viewModel.modifyWorkerList(worker)
                }
            }

            if (timeSheet.custname != "") {
                customerName = timeSheet.custname
                customerStreet = timeSheet.street
                customerPostCode = timeSheet.postcode
                customerTelephone = timeSheet.telephone
                customerEmail = timeSheet.customeremail
                customerWeb = timeSheet.web
                customerCountry = timeSheet.country
                customerCounty = timeSheet.county
                addTimeSheetBinding.timeshetCustomerGroup.visibility = View.VISIBLE
                addTimeSheetBinding.timesheetAddCustomerTextView.text = "Name: $customerName"
                addTimeSheetBinding.timesheetCustomerStreetTextView.text = "Street: $customerStreet"
                addTimeSheetBinding.timesheetCustomerPostCodeTextView.text =
                    "Postcode: $customerPostCode"
                addTimeSheetBinding.timesheetCustomerPhoneTextView.text =
                    "Telephone: $customerTelephone"
                addTimeSheetBinding.timesheetCustomerEmailTextView.text = "Email: $customerEmail"
                addTimeSheetBinding.timesheetCustomerWebTextView.text = "Web: $customerWeb"
                addTimeSheetBinding.timesheetCustomerCountyTextView.text = "County: $customerCounty"
            }
        } else if (action == getString(R.string.purchase) || action == getString(R.string.quotation)) {
            val jobNo = arguments.jobno!!
            val quotationNo = arguments.quotationno!!
            addTimeSheetBinding.timeSheetJobNoTextInput.setText(jobNo)
            addTimeSheetBinding.tsQuotationNoTextInput.setText(quotationNo)
        }

        //Set on Click listener to add worker textView
        addTimeSheetBinding.addWorkerTv.setOnClickListener {

            val addWorkerBottomSheet = AddWorkerBottomSheetFragment(null, this)
            addWorkerBottomSheet.show(activity.supportFragmentManager, addWorkerBottomSheet.tag)
        }

        //Set on click listener to add customer textView
        addTimeSheetBinding.timesheetAddCustomerTextView.setOnClickListener {
            val searchNameBottomSheet = SearchCustomerBottomSheetFragment(
                getString(R.string.quotation), custNameList, this
                , this, this,
                this, this, this,
                this, this
            )
            searchNameBottomSheet.show(activity!!.supportFragmentManager, searchNameBottomSheet.tag)
        }

        //Observe Worker List
        viewModel.workerList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                workersList.clear()
                workersList.addAll(it)
                noOfWorker = workersList.size
                workerListAdapter.submitList(it)
                workerListAdapter.notifyDataSetChanged()
            }
        })

        //Observe customer list
        viewModel.getCustomerList(comId).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it.posts != null) {
                    for (item in it.posts) {
                        custNameList.add(item.custname!!)
                    }
                }
            }
        })
        //Set on Click listener to save button
        addTimeSheetBinding.tsSaveButton.setOnClickListener {
            val jobNo = addTimeSheetBinding.timeSheetJobNoTextInput.text.toString()
            val quotationNo = addTimeSheetBinding.tsQuotationNoTextInput.text.toString()

            when {
                jobNo.isEmpty() -> {
                    addTimeSheetBinding.timeSheetJobNoTextInputLayout.error =
                        getString(R.string.enter_job_no)
                }
                quotationNo.isEmpty() -> {
                    addTimeSheetBinding.tsQuotationNoTextInputLayout.error =
                        getString(R.string.enter_quotation_no)
                }
                else -> {
                    val timeSheetAdd =
                        TimeSheetAdd(
                            comId,
                            "AngE9676#254r5",
                            jobNo,
                            quotationNo,
                            workersList,
                            customerName,
                            customerStreet,
                            customerPostCode,
                            customerTelephone,
                            customerEmail,
                            customerWeb,
                            customerCountry,
                            customerCounty
                        )
                    viewModel.addTimeSheet(timeSheetAdd)
                        .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            it?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(
                                    AddTimeSheetFragmentDirections
                                        .actionAddTimeSheetFragmentToTimeSheetFragment()
                                )
                                viewModel.clearWorkerList()
                            }
                        })

                }
            }
        }

        //Set on Click listener to updte button
        addTimeSheetBinding.tsUpdateButton.setOnClickListener {
            val jobNo = addTimeSheetBinding.timeSheetJobNoTextInput.text.toString()
            val quotationNo = addTimeSheetBinding.tsQuotationNoTextInput.text.toString()
            var sWorkersList = workersList

            when {
                jobNo.isEmpty() -> {
                    addTimeSheetBinding.timeSheetJobNoTextInputLayout.error =
                        getString(R.string.enter_job_no)
                }
                quotationNo.isEmpty() -> {
                    addTimeSheetBinding.tsQuotationNoTextInputLayout.error =
                        getString(R.string.enter_quotation_no)
                }
                else -> {
                    val timeSheetUpdate =
                        TimeSheetUpdate(
                            comId,
                            timeSheet.tid,
                            "AngE9676#254r5",
                            jobNo,
                            quotationNo,
                            sWorkersList,
                            customerName,
                            customerStreet,
                            customerPostCode,
                            customerTelephone,
                            customerEmail,
                            customerWeb,
                            customerCountry,
                            customerCounty
                        )
                    viewModel.updateTimeSheet(timeSheetUpdate)
                        .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            it?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(
                                    AddTimeSheetFragmentDirections
                                        .actionAddTimeSheetFragmentToTimeSheetFragment()
                                )
                                viewModel.clearWorkerList()
                            }
                        })

                }
            }
        }
        setHasOptionsMenu(true)

        viewModel.getLogoList(comId).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
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
        viewModel.imageBitmap.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {

                bmpList.add(it)
                Timber.e("Bitmap list size: ${bmpList.size}")
            }
        })

        //Observe to get company logo bitmap
        viewModel.companyBitmap.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                companyBitmap = it
                Timber.e("Company bitmap size: ${companyBitmap.height}, ${companyBitmap.width}")
            }
        })
        return addTimeSheetBinding.root
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (action == getString(R.string.update)) {
            inflater.inflate(R.menu.timesheet_menu, menu)
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
                    dialog?.dismiss()
                    viewModel.deleteTimeSheet(timeSheet.tid.toInt())
                        .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            it?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(
                                    AddTimeSheetFragmentDirections
                                        .actionAddTimeSheetFragmentToTimeSheetFragment()
                                )
                            }
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
                    AddTimeSheetFragmentDirections.actionAddTimeSheetFragmentToAddQuotationFragment(
                        null,
                        getString(R.string.time_sheet),
                        null,
                        null,
                        timeSheet.jobNo,
                        timeSheet.quotationNo
                    )
                )
            }
            R.id.action_purchase -> {
                findNavController().navigate(
                    AddTimeSheetFragmentDirections.actionAddTimeSheetFragmentToAddPurchaseFragment(
                        null,
                        getString(R.string.time_sheet),
                        null,
                        null,
                        timeSheet.jobNo,
                        timeSheet.quotationNo
                    )
                )
            }
            R.id.worksheet_action -> {
                findNavController().navigate(AddTimeSheetFragmentDirections.actionAddTimeSheetFragmentToCreateWorkSheetFragment())
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

                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        //openSettingsDialog();
                        Toast.makeText(
                            context,
                            "We need To access your storage to create Pdf",
                            Toast.LENGTH_SHORT
                        )
                            .show()
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
            headTable.setWidths(intArrayOf(2, 2))
            headTable.widthPercentage = 100f
            val addressCell = getAddressTable()
            val dateCell = PdfPCell()
            dateCell.border = PdfPCell.NO_BORDER
            dateCell.addElement(Paragraph("Date: ${Calendar.DAY_OF_MONTH}"))
            dateCell.addElement(Paragraph("Job No. : ${timeSheet.jobNo}"))
            dateCell.addElement(Paragraph("Quotation No. : ${timeSheet.quotationNo}"))
            dateCell.horizontalAlignment = Element.ALIGN_RIGHT
            headTable.addCell(addressCell)
            headTable.addCell(dateCell)
            doc.add(headTable)
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

            val customerDetailsTitle =
                Paragraph(
                    getString(R.string.customer_data),
                    Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
                )
            customerDetailsTitle.alignment = Element.ALIGN_CENTER
            doc.add(customerDetailsTitle)
            doc.add(Paragraph(""))
            doc.add(Paragraph("Customer name: ${timeSheet.custname}"))
            doc.add(Paragraph("Street Address: ${timeSheet.street}"))
            doc.add(Paragraph("Postcode: ${timeSheet.postcode}"))
            doc.add(Paragraph("Email: ${timeSheet.customeremail}"))
            doc.add(Paragraph("Telephone no.: ${timeSheet.telephone}"))
            doc.add(Paragraph("Country: ${timeSheet.country}"))
            doc.add(Paragraph("County : ${timeSheet.county}"))

            doc.add(Paragraph(" "))
            val invoiceTitle =
                Paragraph("TIME SHEET", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            invoiceTitle.alignment = Element.ALIGN_CENTER
            doc.add(invoiceTitle)
            doc.add(Paragraph(" "))
            doc.add(createTimeSheetTable())
            doc.add(Paragraph(" "))

            val workList = timeSheet.workerList
            var totalHours = 0
            var totalAmount = 0.0
            workList?.let {
                for (work in workList) {
                    totalHours += work.hours!!
                    totalAmount += work.totalAmount!!
                }
                doc.add(Paragraph("Total hours worked: $totalHours"))
                doc.add(Paragraph("Total Amount: $totalAmount"))
            }
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

    //Create Time Sheet Table
    private fun createTimeSheetTable(): PdfPTable {
        val table = PdfPTable(7)
        table.widthPercentage = 100f
        table.setWidths(intArrayOf(2, 1, 1, 1, 1, 1, 1))
        val workerList = timeSheet.workerList
        val nameCell = PdfPCell(Paragraph("Worker Name"))
        nameCell.setPadding(8f)
        val dateCell = PdfPCell(Paragraph("Date"))
        dateCell.setPadding(8f)
        val hourCell = PdfPCell(Paragraph("Hours"))
        hourCell.setPadding(8f)
        val amountCell = PdfPCell(Paragraph("Amount/Hr"))
        amountCell.setPadding(8f)
        val advanceCell = PdfPCell(Paragraph("Advance Amount"))
        advanceCell.setPadding(8f)
        val vatCell = PdfPCell(Paragraph("Vat%"))
        vatCell.setPadding(8f)
        val totalAmountCell = PdfPCell(Paragraph("Total Amount"))
        totalAmountCell.setPadding(8f)
        table.addCell(nameCell)
        table.addCell(dateCell)
        table.addCell(hourCell)
        table.addCell(amountCell)
        table.addCell(advanceCell)
        table.addCell(vatCell)
        table.addCell(totalAmountCell)
        workerList?.let {
            for (worker in workerList) {
                val nameVCell = PdfPCell(Paragraph(worker.name))
                nameVCell.setPadding(8f)
                val dateVCell = PdfPCell(Paragraph(worker.date))
                dateVCell.setPadding(8f)
                val hourVCell = PdfPCell(Paragraph(worker.hours.toString()))
                hourVCell.setPadding(8f)
                val amountVCell = PdfPCell(Paragraph(worker.amount.toString()))
                amountVCell.setPadding(8f)
                val advanceVCell = PdfPCell(Paragraph(worker.advanceAmount.toString()))
                advanceVCell.setPadding(8f)
                val vatVCell = PdfPCell(Paragraph(worker.vat.toString()))
                vatVCell.setPadding(8f)
                val totalVCell = PdfPCell(Paragraph(worker.totalAmount.toString()))
                totalVCell.setPadding(8f)
                table.addCell(nameVCell)
                table.addCell(dateVCell)
                table.addCell(hourVCell)
                table.addCell(amountVCell)
                table.addCell(advanceVCell)
                table.addCell(vatVCell)
                table.addCell(totalVCell)
            }
        }

        return table
    }

    override fun onWorkerSave(worker: WorkerList) {

        listWorker?.let {
            if (listWorker != null) {
                viewModel.removeWorker(listWorker!!)
            }
            listWorker = null
        }
        viewModel.modifyWorkerList(worker)
    }

    private fun createChoiceDialog(item: WorkerList) {
        val layout = LayoutInflater.from(context).inflate(R.layout.choose_layout, null)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(layout)
        val dialog = builder.create()
        dialog.show()
        val editButton: MaterialButton = layout.findViewById(R.id.choice_edit_button)
        val deleteButton: MaterialButton = layout.findViewById(R.id.choice_delete_button)
        deleteButton.setOnClickListener {
            viewModel.removeWorker(item)
            dialog.dismiss()
        }
        editButton.setOnClickListener {
            val addWorkerBottomSheetFragment = AddWorkerBottomSheetFragment(item, this)
            addWorkerBottomSheetFragment.show(
                activity!!.supportFragmentManager,
                addWorkerBottomSheetFragment.tag
            )
            dialog.dismiss()
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

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {

        addTimeSheetBinding.timeshetCustomerGroup.visibility = View.VISIBLE
        customerName = searchCustomer.custname
        customerStreet = searchCustomer.street
        customerPostCode = searchCustomer.postcode
        customerTelephone = searchCustomer.telephone
        customerEmail = searchCustomer.customerEmail
        customerWeb = searchCustomer.web
        customerCountry = searchCustomer.country
        customerCounty = searchCustomer.county
        addTimeSheetBinding.timesheetAddCustomerTextView.text = "Name: $customerName"
        addTimeSheetBinding.timesheetCustomerStreetTextView.text = "Street: $customerStreet"
        addTimeSheetBinding.timesheetCustomerPostCodeTextView.text = "Postcode: $customerPostCode"
        addTimeSheetBinding.timesheetCustomerPhoneTextView.text = "Telephone: $customerTelephone"
        addTimeSheetBinding.timesheetCustomerEmailTextView.text = "Email: $customerEmail"
        addTimeSheetBinding.timesheetCustomerWebTextView.text = "Web: $customerWeb"
        addTimeSheetBinding.timesheetCustomerCountyTextView.text = "County: $customerCounty"

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
}
