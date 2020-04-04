package com.example.jeffaccount.ui.home.worksheet

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
import com.example.jeffaccount.createPreviewDialog
import com.example.jeffaccount.databinding.CreateWorkSheetFragmentBinding
import com.example.jeffaccount.model.Invoice
import com.example.jeffaccount.model.PurchasePost
import com.example.jeffaccount.model.TimeSheetPost
import com.example.jeffaccount.model.WorkSheet
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.OnItemClickListener
import com.example.jeffaccount.ui.home.quotation.SearchNameAdapter
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateWorkSheetFragment : Fragment(), OnItemClickListener {

    private var jobNoList: ArrayList<String> = ArrayList()
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
    private var invoiceList: MutableList<Invoice> = mutableListOf()
    private var timesheetList: MutableList<TimeSheetPost> = mutableListOf()
    private lateinit var loadingDialog: AlertDialog

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

        viewModel.getPurchaseList("AngE9676#254r5").observe(viewLifecycleOwner, Observer {
            it?.let {
                val purchaselist = it.posts
                Timber.e("Purchase list size is ${purchaselist.size}")
                for (item in purchaselist) {
                    Timber.e("job no: ${item.jobNo}")
                    pjobList.add(item.jobNo!!)
                }
                jobNoList.addAll(pjobList)
                viewModel.addJobNoList(jobNoList)
            }
        })

        viewModel.getInvoiceList("AngE9676#254r5").observe(viewLifecycleOwner, Observer {
            it?.let {
                val invoiceList = it.invoiceList
                for (item in invoiceList) {
                    ijobList.add(item.jobNo!!)
                }
                jobNoList.retainAll(ijobList)
                viewModel.addJobNoList(jobNoList)
            }
        })

        viewModel.getTimeSheetList("AngE9676#254r5").observe(viewLifecycleOwner, Observer {
            it?.let {
                val timeSheetList = it.posts
                for (item in timeSheetList) {
                    tjobList.add(item.jobNo!!)
                }
                jobNoList.retainAll(tjobList)
                viewModel.addJobNoList(jobNoList)

            }
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
        return workSheetBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onItemClick(name: String) {

        loadingDialog = createLoadingDialog()!!
        loadingDialog.show()
        //Search purchase
        viewModel.searchPurchase(name, "AngE9676#254r5").observe(viewLifecycleOwner, Observer {

            it?.let {
                Timber.e("Purchase list size: ${it.posts.size}")
//                viewModel.addPurchase(it.posts)
                purchaseList = it.posts.toMutableList()
            }
        })

        //Search invoice
        viewModel.searchInvoice(name, "AngE9676#254r5").observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("Invoice list size: ${it.invoiceList.size}")
//                viewModel.addInvoice(it.invoiceList)
                invoiceList = it.invoiceList.toMutableList()
            }
        })

        //search time sheet
        viewModel.searchTimeSheet(name, "AngE9676#254r5").observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("time sheet list size: ${it.posts.size}")
//                viewModel.addTimeSheet(it.posts)
                timesheetList = it.posts.toMutableList()
                workSheet = WorkSheet(purchaseList, invoiceList, timesheetList)
                createWorkSheet(workSheet!!)
                purchaseList.clear()
                invoiceList.clear()
                timesheetList.clear()
                workSheet = null
            }
        })
    }

    //Create Work sheet pdf
    private fun createWorkSheet(workSheet: WorkSheet) {
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
            val titleTable = PdfPTable(2)
            titleTable.widthPercentage = 100f
            val addCell = getAddressTable()
            titleTable.addCell(addCell)
            val jobNoCell = PdfPCell(Paragraph("Job no: ${workSheet.invoiceList[0].jobNo}"))
            jobNoCell.border = PdfPCell.NO_BORDER
            titleTable.addCell(jobNoCell)
            doc.add(titleTable)
            doc.add(Paragraph(" "))
            val title = Paragraph("Worksheet", Font(Font.FontFamily.TIMES_ROMAN, 20f, Font.BOLD))
            title.alignment = Element.ALIGN_CENTER
            doc.add(title)
            doc.add(Paragraph(" "))
            val detailTitle =
                Paragraph("Details table", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            detailTitle.alignment = Element.ALIGN_CENTER
            doc.add(detailTitle)
            doc.add(Paragraph(" "))
            val table = createTable(workSheet)
            doc.add(table)
            doc.add(Paragraph(" "))
            val totalTitle =
                Paragraph("Total table", Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD))
            totalTitle.alignment = Element.ALIGN_CENTER
            doc.add(totalTitle)
            doc.add(Paragraph(" "))
            val totalTable = createTotalTable(workSheet)
            doc.add(totalTable)
            doc.add(Paragraph())
            doc.close().let {
//                viewModel.doneCreatingWorkSheet()
                loadingDialog.dismiss()
                Toast.makeText(context, "Pdf saved in $filePath", Toast.LENGTH_SHORT).show()
                findNavController().navigate(CreateWorkSheetFragmentDirections.actionCreateWorkSheetFragmentToHomeFragment(filePath))
            }
        } catch (e: Exception) {
            Timber.e("Error: ${e.message}")
            loadingDialog.dismiss()
        }
    }

    private fun createTable(workSheet: WorkSheet): PdfPTable {
        val table = PdfPTable(5)
        table.setWidths(intArrayOf(1, 1, 1, 1, 1))
        table.widthPercentage = 100f
        val pHeaderCell = PdfPCell(Paragraph("Purchase Amount"))
        pHeaderCell.setPadding(4f)
        val tHeaderCell = PdfPCell(Paragraph("Time sheet Amount"))
        tHeaderCell.setPadding(4f)
        val iAHeaderCell = PdfPCell(Paragraph("Invoice Amount"))
        iAHeaderCell.setPadding(4f)
        val iDHeaderCell = PdfPCell(Paragraph("Invoice Discount"))
        iDHeaderCell.setPadding(4f)
        val iTHeaderCell = PdfPCell(Paragraph("Invoice Total"))
        iTHeaderCell.setPadding(4f)
        table.addCell(pHeaderCell)
        table.addCell(tHeaderCell)
        table.addCell(iAHeaderCell)
        table.addCell(iDHeaderCell)
        table.addCell(iTHeaderCell)

        val pList = workSheet.purchaseList
        val tLisT = workSheet.timesheetList
        val iList = workSheet.invoiceList
        for (items in pList) {
            Timber.e(items.vat)
            pitemList = items.itemDescription
        }
        var pAmountCell = PdfPCell()
        pAmountCell.horizontalAlignment = Element.ALIGN_CENTER
        for (item in pitemList) {
            Timber.e(item.totalAmount.toString())
            val taxAmount = (20.div(100)).times(item.unitAmount!!)
            val amount = item.unitAmount?.plus(taxAmount)?.minus(item.discountAmount!!)
            pAmountCell.addElement(Paragraph(amount.toString()))
        }
        table.addCell(pAmountCell)
        val tAmountCell = PdfPCell()
        tAmountCell.horizontalAlignment = Element.ALIGN_CENTER
        for (item in tLisT) {
            val taxAmount = (20.div(100)).times(item.amount?.toDouble()!!)
            val amount = item.amount?.toDouble()?.plus(taxAmount.toDouble())
                ?.minus(item.discountAmount?.toDouble()!!)
            tAmountCell.addElement(Paragraph(amount.toString()))
        }
        table.addCell(tAmountCell)
        val iAamountCell = PdfPCell()
        val iDamountCell = PdfPCell()
        val iTamountCell = PdfPCell()
        iAamountCell.horizontalAlignment = Element.ALIGN_CENTER
        iDamountCell.horizontalAlignment = Element.ALIGN_CENTER
        iTamountCell.horizontalAlignment = Element.ALIGN_CENTER
        for (items in iList) {
            iitemList = items.itemDescription
        }
        for (item in iitemList) {
            val taxAmount = (20.div(100)).times(item.unitAmount!!)
            val amount = item.unitAmount?.plus(taxAmount)?.minus(item.discountAmount!!)
            iAamountCell.addElement(Paragraph(item.unitAmount.toString()))
            iDamountCell.addElement(Paragraph(item.discountAmount.toString()))
            iTamountCell.addElement(Paragraph(amount.toString()))
        }
        table.addCell(iAamountCell)
        table.addCell(iDamountCell)
        table.addCell(iTamountCell)
        return table
    }

    private fun createTotalTable(workSheet: WorkSheet): PdfPTable {
        val table = PdfPTable(5)
        table.setWidths(intArrayOf(1, 1, 1, 1, 1))
        table.widthPercentage = 100f
        val pHeaderCell = PdfPCell(Paragraph("Purchase Amount"))
        pHeaderCell.setPadding(4f)
        val tHeaderCell = PdfPCell(Paragraph("Time sheet Amount"))
        tHeaderCell.setPadding(4f)
        val iAHeaderCell = PdfPCell(Paragraph("Invoice Amount"))
        iAHeaderCell.setPadding(4f)
        val iDHeaderCell = PdfPCell(Paragraph("Profit"))
        iDHeaderCell.setPadding(4f)
        val iTHeaderCell = PdfPCell(Paragraph("Loss"))
        iTHeaderCell.setPadding(4f)
        table.addCell(pHeaderCell)
        table.addCell(tHeaderCell)
        table.addCell(iAHeaderCell)
        table.addCell(iDHeaderCell)
        table.addCell(iTHeaderCell)

        val pList = workSheet.purchaseList
        val tLisT = workSheet.timesheetList
        val iList = workSheet.invoiceList
        for (items in pList) {
            Timber.e(items.vat)
            pitemList = items.itemDescription
        }
        var pAmountCell = PdfPCell()
        pAmountCell.horizontalAlignment = Element.ALIGN_CENTER
        var totalPAmount = 0.0
        for (item in pitemList) {
            val taxAmount = (20 / 100).times(item.unitAmount!!)
            val amount = taxAmount.plus(item.unitAmount!!).minus(item.discountAmount!!)
            totalPAmount += amount
        }
        pAmountCell.addElement(Paragraph(totalPAmount.toString()))
        table.addCell(pAmountCell)
        val tAmountCell = PdfPCell()
        tAmountCell.horizontalAlignment = Element.ALIGN_CENTER
        for (item in tLisT) {
            tAmountCell.addElement(Paragraph(item.totalAmount.toString()))
        }
        table.addCell(tAmountCell)

        val iTamountCell = PdfPCell()
        iTamountCell.horizontalAlignment = Element.ALIGN_CENTER
        var inTotalAmount = 0.0
        for (items in iList) {
            iitemList = items.itemDescription
        }
        for (item in iitemList) {
            val taxAmount = (20.div(100)).times(item.unitAmount!!)
            val amount = item.unitAmount?.plus(taxAmount)?.minus(item.discountAmount!!)
            inTotalAmount += amount!!
        }
        iTamountCell.addElement(Paragraph(inTotalAmount.toString()))
        var profit = 0.0
        var loss = 0.0
        if (totalPAmount > inTotalAmount) {
            loss = totalPAmount - inTotalAmount
        } else {
            profit = inTotalAmount - totalPAmount
        }
        table.addCell(iTamountCell)
        table.addCell(Paragraph(profit.toString()))
        table.addCell(Paragraph(loss.toString()))
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

    private fun searchList(search: String) {
        searchList.clear()
        for (i in jobNoList.indices) {
            Timber.e("Names: $jobNoList.[i]")
            if (jobNoList[i].toLowerCase().trim().contains(search)) {
                Timber.e("Name is: $jobNoList[i]")
                searchList.add(jobNoList[i])
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
}
