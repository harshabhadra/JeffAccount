package com.example.jeffaccount.ui.home.timeSheet

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import kotlinx.android.synthetic.main.search_choice_list_item_layout.view.*

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
    private var quotationNoList:MutableSet<String> = mutableSetOf()
    private var customerList:MutableSet<String> = mutableSetOf()
    private var isJobNo:Boolean = false
    private var isQuotationNo:Boolean = false
    private var isCustomerName:Boolean = false

    private lateinit var comid:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Initializing DataBinding
        timeSheetBinding = TimeSheetFragmentBinding.inflate(inflater, container, false)

        val activity = activity as MainActivity
        activity.setToolbarText("Time sheets")
        comid = activity.companyDetails.comid

        //Set on click listener to fab button
        timeSheetBinding.timeSheetFab.setOnClickListener {
            findNavController().navigate(
                TimeSheetFragmentDirections.actionTimeSheetFragmentToAddTimeSheetFragment(
                    null,
                    getString(R.string.add),
                null,null
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
                        getString(R.string.update), null, null
                    )
                )
                viewModel.doneNavigatingToAddTimeSheet()
            }
        })
        //Getting list of time sheet from ViewModel class
        viewModel.getTimeSheetList(comid,getString(R.string.api_key)).observe(viewLifecycleOwner, Observer {
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

        //Observe quotation no list
        viewModel.quotationList.observe(viewLifecycleOwner, Observer {
            it?.let {
                quotationNoList.addAll(it)
            }
        })

        //Observe customer name list
        viewModel.customerList.observe(viewLifecycleOwner, Observer {
            it?.let {
                customerList.addAll(it)
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
                val layout = LayoutInflater.from(requireContext())
                    .inflate(R.layout.search_choice_list_item_layout, null)
                val builder = AlertDialog.Builder(requireContext())
                builder.setView(layout)
                val jobnoTv: TextView = layout.search_by_jobno_tv
                val quotationnoTv: TextView = layout.search_by_quotationno_tv
                val customerNameTv: TextView = layout.search_by_name_tv

                val dialog = builder.create();
                dialog.show()

                //Set on click listener to textViews
                jobnoTv.setOnClickListener {
                    isJobNo = true
                    isQuotationNo = false
                    isCustomerName = false
                    val searchBottomSheet = SearchCustomerBottomSheetFragment(
                        getString(R.string.time_sheet_job_no),jobNoList.toMutableList(),this, this,
                        this, this,
                        this, this,
                        this ,this
                    )
                    searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
                    dialog.dismiss()
                }

                quotationnoTv.setOnClickListener {
                    isJobNo = false
                    isQuotationNo = true
                    isCustomerName = false
                    val searchBottomSheet = SearchCustomerBottomSheetFragment(
                        getString(R.string.time_sheet_quotation_no),quotationNoList.toMutableList(),this, this,
                        this, this,
                        this, this,
                        this ,this
                    )
                    searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
                    dialog.dismiss()
                }

                customerNameTv.setOnClickListener {
                    isJobNo = false
                    isQuotationNo = false
                    isCustomerName = true
                    val searchBottomSheet = SearchCustomerBottomSheetFragment(
                        getString(R.string.time_sheet_customer_name),customerList.toMutableList(),this, this,
                        this, this,
                        this, this,
                        this ,this
                    )
                    searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
                    dialog.dismiss()
                }
            }
        }
        return true
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost,action:String) {
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

        if (isJobNo) {
            viewModel.searchTimeSheet(comid, name, null , null, "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        timeSheetAdapter.submitList(it.posts)
                        timeSheetAdapter.notifyDataSetChanged()
                    }
                })
        }else if (isQuotationNo){
            viewModel.searchTimeSheet(comid, null, name,null, "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        timeSheetAdapter.submitList(it.posts)
                        timeSheetAdapter.notifyDataSetChanged()
                    }
                })
        }else if (isCustomerName){
            viewModel.searchTimeSheet(comid,null , null, name, "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        timeSheetAdapter.submitList(it.posts)
                        timeSheetAdapter.notifyDataSetChanged()
                    }
                })
        }
    }
}
