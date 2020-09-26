package com.example.jeffaccount.ui.home.invoice

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.InvoiceFragmentBinding
import com.example.jeffaccount.network.SearchCustomer
import com.example.jeffaccount.network.SearchSupplierPost
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.*
import kotlinx.android.synthetic.main.search_choice_layout.view.*
import kotlinx.android.synthetic.main.search_choice_list_item_layout.view.*

class InvoiceFragment : Fragment(), OnSearchSupplierClickListener, OnSearchItemClickListener,
    OnCustomerNameClickListener, OnSupplierNameClickListener, OnQuotationJobNoClickListener,
    OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener {

    companion object {
        fun newInstance() = InvoiceFragment()
    }

    private lateinit var viewModel: InvoiceViewModel
    private lateinit var invoiceAdapter: InvoiceListAdapter
    private lateinit var invoiceBinding: InvoiceFragmentBinding
    private var jobNoList: MutableSet<String> = mutableSetOf()
    private var quotationNoList: MutableSet<String> = mutableSetOf()
    private var customerNameList: MutableSet<String> = mutableSetOf()
    private lateinit var comid: String
    private var isJobNo:Boolean = false
    private var isQuotationNo:Boolean = false
    private var isCustomerName:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        invoiceBinding = InvoiceFragmentBinding.inflate(inflater, container, false)

        //Set on click listener to fab button
        invoiceBinding.addInvoiceFab.setOnClickListener {
            findNavController().navigate(
                InvoiceFragmentDirections.actionInvoiceFragmentToAddInvoiceFragment(
                    getString(R.string.add),
                    null,
                    null,
                    null
                    , null, null
                )
            )
        }
        setHasOptionsMenu(true)
        return invoiceBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InvoiceViewModel::class.java)

        //Setting up recyclerView with adapter
        invoiceAdapter = InvoiceListAdapter(InvoiceClickListener {
            it?.let {
                viewModel.invoiceItemClick(it)
            }
        })
        invoiceBinding.invoiceRecycler.adapter = invoiceAdapter

        val activity = activity as MainActivity
        activity.setToolbarText(getString(R.string.invoice))

        comid = activity.companyDetails.comid

        //observe when to navigate to add invoice fragment
        viewModel.navigateToAddInvoiceFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    InvoiceFragmentDirections.actionInvoiceFragmentToAddInvoiceFragment(
                        getString(R.string.update), it, null, null, null, null
                    )
                )
                viewModel.doneNavigating()
            }
        })

        //Getting invoice list from network and set list to the recyclerView
        viewModel.getInvoiceList(comid, "AngE9676#254r5").observe(viewLifecycleOwner, Observer {
            it?.let {
                invoiceAdapter.submitList(it.invoiceList)
                viewModel.createJobNoList(it.invoiceList)
            }
        })

        //Observe job no list
        viewModel.jobNoList.observe(viewLifecycleOwner, Observer {
            it?.let {
                jobNoList.addAll(it)
            }
        })

        //Observe no of quotation
        viewModel.quotationNoList.observe(viewLifecycleOwner, Observer {
            it?.let {
                quotationNoList.addAll(it)
            }
        })

        //Observe customer name list
        viewModel.customerNameList.observe(viewLifecycleOwner, Observer {
            it?.let {
                customerNameList.addAll(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_search -> {
                val layout = LayoutInflater.from(requireContext())
                    .inflate(R.layout.search_choice_list_item_layout, null)
                val builder = AlertDialog.Builder(requireContext())
                builder.setView(layout)
                val jobnoTv:TextView = layout.search_by_jobno_tv
                val quotationnoTv:TextView = layout.search_by_quotationno_tv
                val customerNameTv:TextView = layout.search_by_name_tv

                val dialog = builder.create();
                dialog.show()

                //Set on click listener to textViews
                jobnoTv.setOnClickListener {
                    isJobNo = true
                    isQuotationNo = false
                    isCustomerName = false
                    val searchBottomSheet = SearchCustomerBottomSheetFragment(
                        getString(R.string.invoice_list_jobno), jobNoList.toMutableList(), this, this,
                        this, this,
                        this, this, this,
                        this
                    )
                    searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
                    dialog.dismiss()
                }

                quotationnoTv.setOnClickListener {
                    isJobNo = false
                    isQuotationNo = true
                    isCustomerName = false
                    val searchBottomSheet = SearchCustomerBottomSheetFragment(
                        getString(R.string.invoice_list_quotationno), quotationNoList.toMutableList(), this, this,
                        this, this,
                        this, this, this,
                        this
                    )
                    searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
                    dialog.dismiss()
                }

                customerNameTv.setOnClickListener {
                    isJobNo = false
                    isQuotationNo = false
                    isCustomerName = true
                    val searchBottomSheet = SearchCustomerBottomSheetFragment(
                        getString(R.string.invoice_list_customername), customerNameList.toMutableList(), this, this,
                        this, this,
                        this, this, this,
                        this
                    )
                    searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
                    dialog.dismiss()
                }
            }
        }
        return true
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost, action: String) {
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {
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

        if (isJobNo) {
            viewModel.searchInvoice(comid, name, null, null, "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        val list = it.invoiceList
                        invoiceAdapter.submitList(list)
                        invoiceAdapter.notifyDataSetChanged()
                    }
                })
        }else if (isQuotationNo){
            viewModel.searchInvoice(comid, null, name, null, "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        val list = it.invoiceList
                        invoiceAdapter.submitList(list)
                        invoiceAdapter.notifyDataSetChanged()
                    }
                })
        }else if (isCustomerName){
            viewModel.searchInvoice(comid, null, null, name, "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        val list = it.invoiceList
                        invoiceAdapter.submitList(list)
                        invoiceAdapter.notifyDataSetChanged()
                    }
                })
        }
    }

    override fun onTimeSheetJobClick(name: String) {

    }
}
