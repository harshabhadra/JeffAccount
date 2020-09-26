package com.example.jeffaccount.ui.home.worksheet

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.CreateWorkSheetFragmentBinding
import com.example.jeffaccount.model.*
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.PageNumeration
import com.example.jeffaccount.ui.home.quotation.OnItemClickListener
import com.example.jeffaccount.ui.home.quotation.SearchNameAdapter
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateWorkSheetFragment : Fragment(), OnItemClickListener {

    private var jobNoList: Set<String> = setOf()
    val finalList: ArrayList<String> = arrayListOf()
    private var searchList: ArrayList<String> = ArrayList()
    private var pjobList: ArrayList<String> = ArrayList()
    private var tjobList: ArrayList<String> = ArrayList()
    private var ijobList: ArrayList<String> = ArrayList()
    private lateinit var pitemList: List<Item>
    private lateinit var iitemList: List<Item>
    private lateinit var titemList: List<Item>
    private lateinit var workSheetBinding: CreateWorkSheetFragmentBinding
    private lateinit var searchNameAdapter: SearchNameAdapter
    private var workSheet: WorkSheet? = null
    private var purchaseList: MutableList<PurchasePost> = mutableListOf()
    private var quotationList: MutableList<QuotationPost> = mutableListOf()
    private var timesheetList: MutableList<TimeSheetPost> = mutableListOf()
    private var invoiceList:MutableList<Invoice> = mutableListOf()
    private lateinit var loadingDialog: AlertDialog
    private lateinit var comid: String
    private lateinit var companyDetails: CompanyDetails
    private var totalPurchaseAmount = 0.0
    private var totalTimeSheetAmount = 0.0
    private var totalQuotationAmount = 0.0
    private var totalInvoiceAmount = 0.0
    private var logoList: MutableList<Logo> = mutableListOf()
    private var bmpList: MutableList<Bitmap> = mutableListOf()
    private lateinit var companyBitmap: Bitmap

    companion object {
        fun newInstance() = CreateWorkSheetFragment()
    }

    private lateinit var viewModel: CreateWorkSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        workSheetBinding = CreateWorkSheetFragmentBinding.inflate(inflater, container, false)
        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(CreateWorkSheetViewModel::class.java)

        val activity = activity as MainActivity
        activity.setToolbarText("Work Sheet")

        companyDetails = activity.companyDetails
        companyDetails.caomimge?.let {
            viewModel.getCompanyBitmap("https://alphabusinessdesigns.com/wordpress/appproject/jtapp/$it")
        }
        comid = activity.companyDetails.comid

        viewModel.getPurchaseList(comid, "AngE9676#254r5").observe(viewLifecycleOwner, Observer {
            it?.let {
                val purchaselist = it.posts
                Timber.e("Purchase list size is ${purchaselist.size}")
                for (item in purchaselist) {
                    Timber.e("purchase job no: ${item.jobNo}")
                    pjobList.add(item.jobNo!!)
                }
            }
            viewModel.getTimeSheetList(comid, "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        val timeSheetList = it.posts
                        for (item in timeSheetList) {
                            Timber.e("timesheet job no: ${item.jobNo}")
                            tjobList.add(item.jobNo!!)
                        }
                        jobNoList = pjobList.intersect(tjobList)
                    }
                    viewModel.getInvoiceList(comid, "AngE9676#254r5")
                        .observe(viewLifecycleOwner, Observer {
                            it?.let {
                                val invoiceList = it.posts
                                for (item in invoiceList!!) {
                                    Timber.e("invoice job no: ${item.jobNo}")
                                    ijobList.add(item.jobNo!!)
                                }
                                val list = jobNoList.intersect(ijobList)

                                finalList.addAll(list)
                                viewModel.addJobNoList(finalList)
                            }
                        })
                })
        })

        viewModel.jobNoList.observe(viewLifecycleOwner, Observer {

            it?.let {
                //Setting up recyclerView with searchNameAdapter
                searchNameAdapter = SearchNameAdapter(it, this)
                workSheetBinding.jobNoRecycler.adapter = searchNameAdapter
                searchNameAdapter.notifyDataSetChanged()
            }
        })

        workSheetBinding.searchJobNoEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchList(s.toString().toLowerCase())
            }
        })

        viewModel.getLogoList(comid).observe(viewLifecycleOwner, Observer {
            it.logoList?.let {
                logoList.addAll(it)
                if (logoList.isNotEmpty()){
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
        return workSheetBinding.root
    }

    override fun onItemClick(name: String) {

        viewModel.clearJobNoList()
        loadingDialog = createLoadingDialog()!!
        loadingDialog.show()
        //Search purchase
        viewModel.searchPurchase(comid, name, "AngE9676#254r5")
            .observe(viewLifecycleOwner, Observer {

                it?.let {
                    Timber.e("Purchase list size: ${it.posts.size}")

                    purchaseList = it.posts.toMutableList()
                }
                //Search invoice
                viewModel.searchQuottion(comid, name, "AngE9676#254r5")
                    .observe(viewLifecycleOwner, Observer {
                        it?.let {
                            Timber.e("Invoice list size: ${it.posts!!.size}")

                            quotationList = it.posts!!.toMutableList()
                        }
                        //search time sheet
                        viewModel.searchTimeSheet(comid, name, "AngE9676#254r5")
                            .observe(viewLifecycleOwner, Observer {
                                it?.let {

                                    timesheetList = it.posts.toMutableList()
                                    //Search invoice
                                    viewModel.searchInvoice(comid,name,"AngE9676#254r5")
                                        .observe(viewLifecycleOwner, Observer {
                                            it?.let {
                                                invoiceList = it.invoiceList.toMutableList()
                                                workSheet = WorkSheet(purchaseList, quotationList, invoiceList, timesheetList)
                                                createWorkSheet(name,workSheet!!)
                                            }
                                        })
                                }
                            })
                    })
            })
    }

    //Create Work sheet pdf
    private fun createWorkSheet(jobNo:String, workSheet: WorkSheet) {
        val mFileName =
            "jeff_account_worksheet" + SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.getDefault())
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
        val filePath = "$folder/$mFileName.pdf"
        Timber.e("file path: $filePath")
        val doc = Document()
        try {
             PdfWriter.getInstance(doc, FileOutputStream(filePath))
            doc.open()
            val cBitmap = BITMAP_RESIZER(companyBitmap, 80, 80)
            val stream = ByteArrayOutputStream()
            cBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = com.itextpdf.text.Image.getInstance(stream.toByteArray())
            doc.add(image)
            val titleTable = PdfPTable(2)
            titleTable.widthPercentage = 100f
            val addCell = getAddressTable()
            titleTable.addCell(addCell)
            val jobNoCell = PdfPCell()
            jobNoCell.addElement(Paragraph("Job no. : $jobNo"))
            jobNoCell.addElement(Paragraph("Date: ${Calendar.DAY_OF_MONTH}"))
            jobNoCell.addElement(Paragraph("Quotation no. : ${workSheet.invoiceList[0].quotationNo}"))
            jobNoCell.border = PdfPCell.NO_BORDER
            titleTable.addCell(jobNoCell)
            doc.add(titleTable)
            doc.add(Paragraph(" "))
            val title = Paragraph("Worksheet", Font(Font.FontFamily.TIMES_ROMAN, 20f, Font.BOLD))
            title.alignment = Element.ALIGN_CENTER
            doc.add(title)
            doc.add(Paragraph(" "))
            val purchaseTitle = Paragraph("Purchase", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            purchaseTitle.alignment = Element.ALIGN_CENTER
            doc.add(purchaseTitle)
            doc.add(Paragraph(""))
            val purchaselist = workSheet.purchaseList
            for (purchase in purchaselist){
                doc.add(Paragraph("Purchase Quotation no.: ${purchase.quotationNo}"))
                doc.add(Paragraph("Customer name: ${purchase.custname}"))
                doc.add(Paragraph(""))
                val suppliers = purchase.supList
                suppliers?.let {
                    for (supplier in suppliers){
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
            }

            val timesheetlist = workSheet.timesheetList
            timesheetlist?.let {
                doc.add(Paragraph(""))
                val timeSheetTitle = Paragraph("TimeSheet", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
                timeSheetTitle.alignment = Element.ALIGN_CENTER
                doc.add(timeSheetTitle)
                doc.add(Paragraph(""))

                for(timesheet in timesheetlist){
                    doc.add(Paragraph("Timesheet Quotation no.: ${timesheet.quotationNo}"))
                    doc.add(Paragraph("Customer name.: ${timesheet.custname}"))
                    doc.add(Paragraph(" "))
                    doc.add(createTimeSheetTable(timesheet))
                    doc.add(Paragraph(" "))
                }
            }

            doc.add(Paragraph(""))
            val invoiceTitle =
                Paragraph("Invoice", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            invoiceTitle.alignment = Element.ALIGN_CENTER
            doc.add(invoiceTitle)

            val invoiceItemList = workSheet.invoiceItemList
            invoiceItemList?.let {
                for (invoice in invoiceItemList){
                    doc.add(Paragraph("Invoice quotation no. :  ${invoice.quotationNo}"))
                    doc.add(Paragraph("Customer name :  ${invoice.customerName}"))
                    doc.add(Paragraph(" "))
                    doc.add(createInvoiceTable(invoice))
                    doc.add(Paragraph(" "))
                }
            }
            doc.add(Paragraph(" "))
            val totalTitle =
                Paragraph("Total", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            totalTitle.alignment = Element.ALIGN_CENTER
            doc.add(totalTitle)
            doc.add(Paragraph(" "))
            doc.add(createDetailsTable(workSheet))
            doc.add(Paragraph(" "))
            doc.add(Paragraph("Total Purchase Amount: $totalPurchaseAmount"))
            doc.add(Paragraph("Total Time-Sheet Amount: $totalTimeSheetAmount"))
            doc.add(Paragraph("Total Quotation Amount: $totalQuotationAmount"))
            doc.add(Paragraph("Total Invoice Amount: $totalInvoiceAmount"))
            if (totalPurchaseAmount.plus(totalTimeSheetAmount) > totalInvoiceAmount) {
                val loss = DecimalFormat ("##.##").format(totalPurchaseAmount.plus(totalTimeSheetAmount)
                    .minus(totalInvoiceAmount))
                doc.add(
                    Paragraph(
                        "Total Loss: $loss"
                    )
                )
            } else {
                val profit = DecimalFormat("##.##").format(totalInvoiceAmount.minus(
                    totalTimeSheetAmount.plus(
                        totalPurchaseAmount
                    )
                ))
                doc.add(
                    Paragraph(
                        "Total Profit: $profit"
                    )
                )
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
                viewModel.doneCreatingWorkSheet()
                loadingDialog.dismiss()
                Toast.makeText(context, "Pdf saved in $filePath", Toast.LENGTH_SHORT).show()
                findNavController().navigate(
                    CreateWorkSheetFragmentDirections.actionCreateWorkSheetFragmentToHomeFragment(
                        filePath
                    )
                )
            }
        } catch (e: Exception) {
            Timber.e("Pdf creating Error: ${e.message}")
            Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
            loadingDialog.dismiss()
        }
    }

    //Create invoice table
    private fun createInvoiceTable(invoice: Invoice): PdfPTable {
        val table = PdfPTable(5)
        table.setWidths(intArrayOf(4, 2, 2, 2,2))
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

        for (item in invoice.itemDescription) {
            val itemDesCell = PdfPCell(Paragraph(item.itemDes))
            itemDesCell.setPadding(8f)
            val qtyCell = PdfPCell(Paragraph(item.qty.toString() + "  " + item.unit))
            qtyCell.setPadding(8f)
            val unitDCell = PdfPCell(Paragraph(item.unitAmount.toString()))
            unitDCell.setPadding(8f)
            val disDCell = PdfPCell(Paragraph(item.discountAmount.toString()))
            disDCell.setPadding(8f)
            val totalCell = PdfPCell(Paragraph(item.totalAmount.toString()))
            totalCell.setPadding(8f)
            table.addCell(itemDesCell)
            table.addCell(qtyCell)
            table.addCell(unitDCell)
            table.addCell(disDCell)
            table.addCell(totalCell)
        }
        table.widthPercentage = 100f
        return table
    }

    //Create Time Sheet Table
    private fun createTimeSheetTable(timeSheet:TimeSheetPost): PdfPTable {
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

    //Create supplier table
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

    private fun createDetailsTable(workSheet: WorkSheet): PdfPTable {
        val table = PdfPTable(4)
        table.widthPercentage = 100f
        val purchaseHCell = PdfPCell(Paragraph("Purchase Amount"))
        purchaseHCell.setPadding(4f)
        val timeSheetHCell = PdfPCell(Paragraph("TimeSheet Amount"))
        timeSheetHCell.setPadding(4f)
        val quotationHcell = PdfPCell(Paragraph("Quotation Amount"))
        quotationHcell.setPadding(4f)
        val invoiceHCell = PdfPCell(Paragraph("Invoice Amount"))
        invoiceHCell.setPadding(4f)
        table.addCell(purchaseHCell)
        table.addCell(timeSheetHCell)
        table.addCell(quotationHcell)
        table.addCell(invoiceHCell)

        var purchaseCell = PdfPCell()
        val purchaseList = workSheet.purchaseList
        purchaseCell = createPurchaseCell(purchaseList)
        table.addCell(purchaseCell)
        val timeSheetList = workSheet.timesheetList
        var timeSheetCell = PdfPCell()
        timeSheetCell = createTimeSheetCell(timeSheetList)
        table.addCell(timeSheetCell)
        val invoiceList = workSheet.invoiceList
        table.addCell(createInvoiceCell(invoiceList))
        val invoiceItemList = workSheet.invoiceItemList
        table.addCell(createInvoiceItemCell(invoiceItemList))

        return table
    }

    private fun createInvoiceItemCell(invoiceItemList: List<Invoice>): PdfPCell {
        val invoiceCell = PdfPCell()
        for (invoice in invoiceItemList) {
            val items = invoice.itemDescription
            var total = 0.0
            var totalAmount = 0.0
            for (item in items) {
                total += item.totalAmount!!
            }
            totalAmount = (invoice.vat?.toDouble()?.div(100)?.times(total)?.plus(total))!!
            totalInvoiceAmount += totalAmount
            invoiceCell.addElement(Paragraph(totalAmount.toString()))
        }
        return invoiceCell
    }

    private fun createInvoiceCell(invoiceList: List<QuotationPost>): PdfPCell {
        val invoiceCell = PdfPCell()
        for (invoice in invoiceList) {
            val items = invoice.itemDescription
            var total = 0.0
            var totalAmount = 0.0
            for (item in items) {
                total += item.totalAmount!!
            }
            totalAmount = (invoice.vat?.toDouble()?.div(100)?.times(total)?.plus(total))!!
            totalQuotationAmount += totalAmount
            invoiceCell.addElement(Paragraph(totalAmount.toString()))
        }
        return invoiceCell
    }

    private fun createTimeSheetCell(timeSheetList: List<TimeSheetPost>): PdfPCell {
        val timeSheetCell = PdfPCell()
        for (timeSheet in timeSheetList) {
            var totalAmount = 0.0
            val workers = timeSheet.workerList
            workers?.let {
                for (worker in workers) {
                    totalAmount += (worker.vat?.div(100)
                        ?.times(worker.totalAmount!!))?.plus(worker.totalAmount!!)!!
                }
                timeSheetCell.addElement(Paragraph(totalAmount.toString()))
                totalTimeSheetAmount += totalAmount
            }
        }
        return timeSheetCell
    }

    private fun createPurchaseCell(purchaseList: List<PurchasePost>): PdfPCell {
        val purchaseCell = PdfPCell()
        for (purchase in purchaseList) {
            val supplierList = purchase.supList
            var totalAmount = 0.0
            supplierList?.let {
                var total = 0.0
                for (supplier in supplierList) {
                    for (item in supplier.itemList!!) {
                        total += (item.totalAmount!!)
                        Timber.e("Total amount worksheet: $total")
                    }
                    totalAmount += (supplier.vat?.div(100)?.times(total))?.plus(total)!!
                    Timber.e("Total purhase amount worksheet: $totalAmount")
                    total = 0.0
                }
                totalPurchaseAmount += totalAmount
                purchaseCell.addElement(Paragraph(totalAmount.toString()))
            }
        }
        return purchaseCell
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

    private fun searchList(search: String) {
        searchList.clear()
        for (i in jobNoList.indices) {
            Timber.e("Names: $jobNoList.[i]")
            if (finalList[i].toLowerCase().trim().contains(search)) {
                Timber.e("Name is: $jobNoList[i]")
                searchList.add(finalList[i])
            } else {
                Timber.e("Not found")
            }
        }
        searchNameAdapter = SearchNameAdapter(searchList, this)
        workSheetBinding.jobNoRecycler.adapter = searchNameAdapter
        searchNameAdapter.notifyDataSetChanged()
    }

    //Create a loading Dialog
    private fun createLoadingDialog(): androidx.appcompat.app.AlertDialog? {
        val layout = LayoutInflater.from(context).inflate(R.layout.loading_layout, null)
        val builder = context.let { androidx.appcompat.app.AlertDialog.Builder(it!!) }
        builder.setCancelable(false)
        builder.setView(layout)
        return builder.create()
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
