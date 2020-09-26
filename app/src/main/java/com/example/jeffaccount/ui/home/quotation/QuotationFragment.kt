package com.example.jeffaccount.ui.home.quotation

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentQuotationBinding
import com.example.jeffaccount.model.QuotationPost
import com.example.jeffaccount.network.SearchCustomer
import com.example.jeffaccount.network.SearchSupplierPost
import com.example.jeffaccount.ui.MainActivity

/**
 * A simple [Fragment] subclass.
 */
class QuotationFragment : Fragment(), OnSearchItemClickListener, OnSearchSupplierClickListener,
    OnCustomerNameClickListener, OnSupplierNameClickListener, OnQuotationJobNoClickListener,
    OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener {

    private lateinit var quotationBinding: FragmentQuotationBinding
    private lateinit var quotationListAdapter: QuotationListAdapter
    private lateinit var quotationViewModel: AddQuotationViewModel
    private lateinit var quotaitonPost: QuotationPost
    private lateinit var comid:String
    private var jobNoList:MutableSet<String> = mutableSetOf()
    private var quotationSet:MutableSet<String> = mutableSetOf()
    private var custNameSet:MutableSet<String> = mutableSetOf()
    private var isJobNo = false
    private var isQuotationNo = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        quotationBinding = FragmentQuotationBinding.inflate(inflater, container, false)

        val fab = quotationBinding.quotationFab
        fab.setOnClickListener {
            findNavController()
                .navigate(
                    QuotationFragmentDirections.actionQuotationFragmentToAddQuotationFragment(
                        null,
                        getString(R.string.add),
                        null,null, null, null
                    )
                )
        }

        //Setting up quotation recycler
        val quotationRecycler = quotationBinding.quotationRecycler
        quotationListAdapter = QuotationListAdapter(QuotationClickListener {
            it?.let {
                quotationViewModel.onQuotationItemClick(it)
            }
        })
        quotationRecycler.adapter = quotationListAdapter

        setHasOptionsMenu(true)
        //Setting up RecyclerView
        return quotationBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = activity as MainActivity
        activity.setToolbarText("Quotations")
        comid = activity.companyDetails.comid

        //Initializing ViewModel class
        quotationViewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)

        //Get list of quotations
        quotationViewModel.getQuotationList(comid,getString(R.string.api_key)).observe(viewLifecycleOwner, Observer {
            it?.let {
                quotationListAdapter.submitList(it.posts)
                quotationBinding.noQuotationTv.visibility = View.GONE
                quotationViewModel.createJobNoList(it.posts!!)
                quotationViewModel.createQuotationNoList(it.posts!!)
                quotationViewModel.createCustomerNameList(it.posts!!)
            } ?: let {
                quotationBinding.noQuotationTv.visibility = View.VISIBLE
            }
        })

        //Observe job no list
        quotationViewModel.jobNoList.observe(viewLifecycleOwner, Observer {
            it?.let {
                jobNoList.addAll(it)
            }
        })

        //Observe quotation no list
        quotationViewModel.quotationNoList.observe(viewLifecycleOwner, Observer {
            it?.let {
                quotationSet.addAll(it)
            }
        })

        //Observe customer name list
        quotationViewModel.custNameSet.observe(viewLifecycleOwner, Observer {
            it?.let {
                custNameSet.addAll(it)
            }
        })

        //Observe if quotation item is clicked
        quotationViewModel.navigateToAddQuotationFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    QuotationFragmentDirections.actionQuotationFragmentToAddQuotationFragment(
                        it,
                        getString(R.string.update),
                        null, null, null, null
                    )
                )
                quotationViewModel.doneNavigating()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_search ->{

              createSearchChoiceDialog()
            }
        }
        return true
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost, action:String) {

    }

    override fun onCustomerNameClick(name: String) {

    }

    override fun onSupplierNameClick(name: String) {

    }

    override fun onQuotationNameClick(name: String) {

        quotationViewModel.searchQuotationBy.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it) {
                    SearchBy.JOB_NO -> {
                        quotationViewModel.searchQuotation(comid, name, "AngE9676#254r5")
                            .observe(viewLifecycleOwner,
                                Observer {
                                    it?.let {
                                        quotationListAdapter.submitList(it.posts)
                                        quotationListAdapter.notifyDataSetChanged()
                                    }
                                })
                    }
                    SearchBy.QUOTATION_NO -> {
                        quotationViewModel.searchQuotationByQuotation(comid, name, "AngE9676#254r5")
                            .observe(viewLifecycleOwner, Observer {
                                it?.let {
                                    quotationListAdapter.submitList(it.posts)
                                    quotationListAdapter.notifyDataSetChanged()
                                }
                            })
                    }
                    SearchBy.CUSTOMER_NAME -> {
                        quotationViewModel.searchQuotationByCustomer(comid, name, "AngE9676#254r5")
                            .observe(viewLifecycleOwner, Observer {
                                it?.let {
                                    quotationListAdapter.submitList(it.posts)
                                    quotationListAdapter.notifyDataSetChanged()
                                }
                            })
                    }
                }
            }
        })
    }

    override fun onPurchaseNameClick(name: String) {

    }

    override fun onInvoiceJobNoClick(name: String) {

    }

    override fun onTimeSheetJobClick(name: String) {

    }

   private fun createSearchChoiceDialog(){
        val layout = LayoutInflater.from(context).inflate(R.layout.search_choice_layout,null)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(layout)

        val jobTv:TextView = layout.findViewById(R.id.job_no_search_tv)
        val quotationTv:TextView = layout.findViewById(R.id.quotation_no_search_tv)
        val nameTv:TextView = layout.findViewById(R.id.customer_search_tv)

        val dialog = builder.create()
        dialog.show()

        jobTv.setOnClickListener {
            quotationViewModel.setSearchBy(SearchBy.JOB_NO)
            val searchBottomSheet = SearchCustomerBottomSheetFragment(
                getString(R.string.quotation_List),jobNoList.toMutableList(),this, this
                ,this, this ,
                this, this,
                this, this
            )
            searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
            dialog.dismiss()
        }

       quotationTv.setOnClickListener {
           quotationViewModel.setSearchBy(SearchBy.QUOTATION_NO)
           isJobNo = true
           isQuotationNo = false
           val searchBottomSheet = SearchCustomerBottomSheetFragment(
               getString(R.string.quotation_List),quotationSet.toMutableList(),this, this
               ,this, this ,
               this, this,
               this, this
           )
           searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
           dialog.dismiss()
       }

       nameTv.setOnClickListener {
           quotationViewModel.setSearchBy(SearchBy.CUSTOMER_NAME)
           isJobNo = false
           isQuotationNo = false
           val searchBottomSheet = SearchCustomerBottomSheetFragment(
               getString(R.string.quotation_List),custNameSet.toMutableList(),this, this
               ,this, this ,
               this, this,
               this, this
           )
           searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
           dialog.dismiss()
       }
    }
}
