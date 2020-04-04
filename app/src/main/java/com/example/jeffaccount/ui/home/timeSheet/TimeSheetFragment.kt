package com.example.jeffaccount.ui.home.timeSheet

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.TimeSheetFragmentBinding
import com.example.jeffaccount.network.SearchCustomer
import com.example.jeffaccount.network.SearchSupplierPost
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.*

class TimeSheetFragment : Fragment(), OnSearchItemClickListener, OnSearchSupplierClickListener,
    OnCustomerNameClickListener, OnSupplierNameClickListener, OnQuotationJobNoClickListener,
    OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener {

    companion object {
        fun newInstance() = TimeSheetFragment()
    }

    private lateinit var viewModel: TimeSheetViewModel
    private lateinit var timeSheetBinding: TimeSheetFragmentBinding
    private lateinit var timeSheetAdapter: TimeSheetListAdapter
    private var jobNoList:MutableSet<String> = mutableSetOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Initializing DataBinding
        timeSheetBinding = TimeSheetFragmentBinding.inflate(inflater, container, false)

        val activity = activity as MainActivity
        activity.setToolbarText("Time sheets")
        //Set on click listener to fab button
        timeSheetBinding.timeSheetFab.setOnClickListener {
            findNavController().navigate(
                TimeSheetFragmentDirections.actionTimeSheetFragmentToAddTimeSheetFragment(
                    null,
                    getString(R.string.add), null
                )
            )
        }

        setHasOptionsMenu(true)
        return timeSheetBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TimeSheetViewModel::class.java)

        //Setting up recyclerView with adapter
        val timeSheetRecycler = timeSheetBinding.timeSheetRecycler
        timeSheetAdapter = TimeSheetListAdapter(TimeSheetClickListener {
            viewModel.navigateWithTimeSheet(it)
        })
        timeSheetRecycler.adapter = timeSheetAdapter

        //Observe when to navigate with timeSheet
        viewModel.navigateToAddTimeSheetFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    TimeSheetFragmentDirections.actionTimeSheetFragmentToAddTimeSheetFragment(
                        it,
                        getString(R.string.update), null
                    )
                )
                viewModel.doneNavigatingToAddTimeSheet()
            }
        })
        //Getting list of time sheet from ViewModel class
        viewModel.getTimeSheetList(getString(R.string.api_key)).observe(viewLifecycleOwner, Observer {
            it?.let {
                timeSheetAdapter.submitList(it.posts)
                timeSheetBinding.noTimeSheetTv.visibility = View.GONE
                viewModel.createJobNoList(it.posts)
            } ?: let {
                timeSheetBinding.noTimeSheetTv.visibility = View.VISIBLE
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
        inflater.inflate(R.menu.search_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_search->{
                val searchBottomSheet = SearchCustomerBottomSheetFragment(
                    getString(R.string.time_sheet),jobNoList.toMutableList(),this, this,
                    this, this,
                    this, this,
                    this ,this
                )
                searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
            }
        }
        return true
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {
        TODO("Not yet implemented")
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost) {
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
