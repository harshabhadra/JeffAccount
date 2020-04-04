package com.example.jeffaccount.ui.home.invoice

import android.os.Bundle
import android.view.*
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
            viewModel.invoiceItemClick(it)
        })
        invoiceBinding.invoiceRecycler.adapter = invoiceAdapter

        val activity = activity as MainActivity
        activity.setToolbarText(getString(R.string.invoice))

        //observe when to navigate to add invoice fragment
        viewModel.navigateToAddInvoiceFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    InvoiceFragmentDirections.actionInvoiceFragmentToAddInvoiceFragment(
                        getString(R.string.update), it, null, null
                    )
                )
                viewModel.doneNavigating()
            }
        })

        //Getting invoice list from network and set list to the recyclerView
        viewModel.getInvoiceList("AngE9676#254r5").observe(viewLifecycleOwner, Observer {
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_search -> {
                val searchBottomSheet = SearchCustomerBottomSheetFragment(
                    getString(R.string.invoice_list),jobNoList.toMutableList(),this, this,
                this, this,
                    this, this, this,
                this
                )
                searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
            }
        }
        return true
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost) {
        TODO("Not yet implemented")
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {
        TODO("Not yet implemented")
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
