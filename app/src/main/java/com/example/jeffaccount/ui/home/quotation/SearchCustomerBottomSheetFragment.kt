package com.example.jeffaccount.ui.home.quotation

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.model.SupPost
import com.example.jeffaccount.network.SearchCustomer
import com.example.jeffaccount.network.SearchSupplierPost
import com.example.jeffaccount.network.asSupplierPost
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.purchase.AddPurchaseViewModel
import com.example.jeffaccount.ui.home.purchase.SearchSupplierAdapter
import com.example.jeffaccount.ui.home.purchase.SearchSupplierClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class SearchCustomerBottomSheetFragment(
    var type: String,
    var comlist: MutableList<String>,
    itemClickListener: OnSearchItemClickListener,
    supplierItemClickListenr: OnSearchSupplierClickListener,
    customerNameClickListener: OnCustomerNameClickListener,
    supplierNameClickListener: OnSupplierNameClickListener,
    quotationJobNoClickListener: OnQuotationJobNoClickListener,
    purchaseJobNoClickListener: OnPurchaseJobNoClickListener,
    invoiceNameClickListener: OnInvoiceJobNoClickListener,
    timeSheetJobNoClickListener: OnTimeSheetJobNoClickListener
) :
    BottomSheetDialogFragment(),
    TextWatcher, OnItemClickListener {


    private lateinit var searchNameAdapter: SearchNameAdapter
    private lateinit var searchItemAdapter: SearchItemAdapter
    private lateinit var searchSupplierAdater: SearchSupplierAdapter

    private lateinit var custList: ArrayList<String>
    private lateinit var searchList: ArrayList<String>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var nameRecyclerView: RecyclerView
    private lateinit var nameItemRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchHeader: TextView

    private lateinit var quotaitonViewModel: AddQuotationViewModel
    private lateinit var purchaseViewModel: AddPurchaseViewModel

    private var searItemClickListener: OnSearchItemClickListener = itemClickListener
    private var searchSupplierClickListener: OnSearchSupplierClickListener =
        supplierItemClickListenr

    private var customerNameClickListener: OnCustomerNameClickListener = customerNameClickListener
    private var supplierNameClickListener: OnSupplierNameClickListener = supplierNameClickListener

    private var quotationJobNoClickListener: OnQuotationJobNoClickListener =
        quotationJobNoClickListener
    private var invoiceJobNoClickListener: OnInvoiceJobNoClickListener = invoiceNameClickListener
    private var purchaseJobNoClickListener: OnPurchaseJobNoClickListener =
        purchaseJobNoClickListener
    private var timeSheetJobNoClickListener: OnTimeSheetJobNoClickListener =
        timeSheetJobNoClickListener
    private lateinit var comid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        custList = arrayListOf()
        searchList = arrayListOf()
        custList.addAll(comlist)
        searchList.addAll(comlist)

        searchNameAdapter = SearchNameAdapter(custList, this)
        searchItemAdapter = SearchItemAdapter(SearchClickListener {
            it?.let {
                searItemClickListener.onSearchItemClick(it)
                dismiss()
            }
        })

        searchSupplierAdater = SearchSupplierAdapter(SearchSupplierClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("What you'd like to do?")
            builder.setPositiveButton(getString(R.string.select), DialogInterface.OnClickListener { dialog, which ->
                searchSupplierClickListener.onSearchSupplierClick(it,getString(R.string.select))
                dismiss()
            }).setNegativeButton(getString(R.string.modify), DialogInterface.OnClickListener { dialog, which ->
                searchSupplierClickListener.onSearchSupplierClick(it,getString(R.string.modify))
                dismiss()
            })
            val dialog = builder.create()
            dialog.show()
        })

        //Initializing ViewModel class
        quotaitonViewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)

        //Initializing purchase view model class
        purchaseViewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val activity = activity as MainActivity
        comid = activity.companyDetails.comid

        val view = View.inflate(context, R.layout.fragment_search_customer_bottom_sheet, null)
        val linearLayout: LinearLayout = view.findViewById(R.id.root_layout)
        val params: LinearLayout.LayoutParams =
            linearLayout.layoutParams as (LinearLayout.LayoutParams)
        params.height = getScreenHeight()
        linearLayout.layoutParams = params

        //Setting up name recyclerView
        nameRecyclerView = view.findViewById(R.id.customer_name_recycler)
        nameRecyclerView.setHasFixedSize(true)
        nameRecyclerView.adapter = searchNameAdapter

        //Setting up name item recyclerView
        nameItemRecyclerView = view.findViewById(R.id.customer_item_recycler)
        nameItemRecyclerView.setHasFixedSize(true)
        if (type.equals(getString(R.string.quotation)) || type.equals(getString(R.string.invoice))) {
            nameItemRecyclerView.adapter = searchItemAdapter
        } else {
            nameItemRecyclerView.adapter = searchSupplierAdater
        }

        searchEditText = view.findViewById(R.id.search_editText)
        searchHeader = view.findViewById(R.id.search_header_tv)
        searchEditText.addTextChangedListener(this)
        decoratDialog(type)
        bottomSheetDialog.setContentView(view)
        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun searchList(search: String) {
        searchList.clear()
        for (i in custList.indices) {
            Timber.e("Names: $custList.[i]")
            if (custList[i].toLowerCase().trim().contains(search)) {
                Timber.e("Name is: $custList[i]")
                searchList.add(custList[i])
            } else {
                Timber.e("Not found")
            }
        }
        searchNameAdapter = SearchNameAdapter(searchList, this)
        nameRecyclerView.adapter = searchNameAdapter
    }

    //Get Screen Height
    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        searchList(s.toString().toLowerCase())
    }

    override fun onItemClick(name: String) {

        when {
            type.equals(getString(R.string.quotation)) || type.equals(getString(R.string.invoice)) -> {
                quotaitonViewModel.searchCustomer(comid, name, "AngE9676#254r5").observe(this,
                    androidx.lifecycle.Observer {
                        Timber.e("Search list size: ${it.customerList?.size}")
                        nameRecyclerView.visibility = View.GONE
                        searchItemAdapter.submitList(it.customerList)
                    })
            }
            type.equals(getString(R.string.customer)) -> {
                customerNameClickListener.onCustomerNameClick(name)
                dismiss()
            }
            type.equals(getString(R.string.supplier)) -> {
                searchEditText.hint = "Search by name"
                searchHeader.text = "Supplier List"
                supplierNameClickListener.onSupplierNameClick(name)
                dismiss()
            }
            type.equals(getString(R.string.quotation_List)) -> {
                quotationJobNoClickListener.onQuotationNameClick(name)
                dismiss()
            }
            type.equals(getString(R.string.invoice_list_jobno)) -> {

                invoiceJobNoClickListener.onInvoiceJobNoClick(name)
                dismiss()
            }
            type.equals(getString(R.string.invoice_list_quotationno)) -> {

                invoiceJobNoClickListener.onInvoiceJobNoClick(name)
                dismiss()
            }
            type.equals(getString(R.string.invoice_list_customername)) -> {

                invoiceJobNoClickListener.onInvoiceJobNoClick(name)
                dismiss()
            }
            type.equals(getString(R.string.purchase_list_job_no)) -> {

                purchaseJobNoClickListener.onPurchaseNameClick(name)
                dismiss()
            }
            type.equals(getString(R.string.purchase_list_quotation_no)) -> {

                purchaseJobNoClickListener.onPurchaseNameClick(name)
                dismiss()
            }
            type.equals(getString(R.string.purchase_list_customer_name)) -> {

                purchaseJobNoClickListener.onPurchaseNameClick(name)
                dismiss()
            }
            type.equals(getString(R.string.time_sheet_job_no)) -> {
                timeSheetJobNoClickListener.onTimeSheetJobClick(name)
                dismiss()
            }
            type.equals(getString(R.string.time_sheet_quotation_no)) -> {
                timeSheetJobNoClickListener.onTimeSheetJobClick(name)
                dismiss()
            } type.equals(getString(R.string.time_sheet_customer_name)) -> {
            timeSheetJobNoClickListener.onTimeSheetJobClick(name)
            dismiss()
        }
            else -> {
                purchaseViewModel.getSearchSupplierList(comid, name, "AngE9676#254r5").observe(this,
                    Observer {
                        Timber.e("Searchlist supplier size: ${it.posts?.size}")
                        nameRecyclerView.visibility = View.GONE
                        searchSupplierAdater.submitList(it.posts)
                    })
            }
        }
    }

    fun decoratDialog(type: String) {
        when {
            type.equals(getString(R.string.quotation)) || type.equals(getString(R.string.invoice)) -> {
                searchEditText.hint = "Search by name"
                searchHeader.text = "Customer List"

            }
            type.equals(getString(R.string.customer)) -> {
                searchEditText.hint = "Search by name"
                searchHeader.text = "Customer List"
            }
            type.equals(getString(R.string.supplier)) -> {
                searchEditText.hint = "Search by name"
                searchHeader.text = "Supplier List"
            }
            type.equals(getString(R.string.quotation_List)) -> {
                searchEditText.hint = "Search by job no"
                searchHeader.text = "Job no List"
            }
            type.equals(getString(R.string.invoice_list_jobno)) -> {
                searchEditText.hint = "Search by job no"
                searchHeader.text = "Job no List"
            }
            type.equals(getString(R.string.invoice_list_quotationno)) -> {
                searchEditText.hint = "Search by Quotation no"
                searchHeader.text = "Quotation no List"
            }
            type.equals(getString(R.string.invoice_list_customername)) -> {
                searchEditText.hint = "Search by Customer name"
                searchHeader.text = "Customer name List"
            }
            type.equals(getString(R.string.purchase_list_job_no)) -> {
                searchEditText.hint = "Search by job no"
                searchHeader.text = "Job no List"
            }
            type.equals(getString(R.string.purchase_list_quotation_no)) -> {
                searchEditText.hint = "Search by Quotation no"
                searchHeader.text = "Quotation no List"
            }
            type.equals(getString(R.string.purchase_list_customer_name)) -> {
                searchEditText.hint = "Search by Customer name"
                searchHeader.text = "Customer name List"
            }
            type.equals(getString(R.string.time_sheet_job_no)) -> {
                searchEditText.hint = "Search by job no"
                searchHeader.text = "Job no List"
            }
            type.equals(getString(R.string.time_sheet_quotation_no)) -> {
                searchEditText.hint = "Search by quotation no"
                searchHeader.text = "Quotation no List"
            }
            type.equals(getString(R.string.time_sheet_customer_name)) -> {
                searchEditText.hint = "Search by Customer name"
                searchHeader.text = "Customer name List"
            }
            else -> {
                searchEditText.hint = "Search by name"
                searchHeader.text = "Supplier List"
            }
        }
    }
}


interface OnSearchItemClickListener {
    fun onSearchItemClick(searchCustomer: SearchCustomer)
}

interface OnSearchSupplierClickListener {
    fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost,action:String)
}

interface OnCustomerNameClickListener {
    fun onCustomerNameClick(name: String)
}

interface OnSupplierNameClickListener {
    fun onSupplierNameClick(name: String)
}

interface OnQuotationJobNoClickListener {
    fun onQuotationNameClick(name: String)
}

interface OnPurchaseJobNoClickListener {
    fun onPurchaseNameClick(name: String)
}

interface OnInvoiceJobNoClickListener {
    fun onInvoiceJobNoClick(name: String)
}

interface OnTimeSheetJobNoClickListener {
    fun onTimeSheetJobClick(name: String)
}
