package com.example.jeffaccount.ui.home.quotation

import android.os.Bundle
import android.view.*
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
    private var jobNoList:MutableSet<String> = mutableSetOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        quotationBinding = FragmentQuotationBinding.inflate(inflater, container, false)

        val activity = activity as MainActivity
        activity.setToolbarText("Quotations")

        val fab = quotationBinding.quotationFab
        fab.setOnClickListener {
            findNavController()
                .navigate(
                    QuotationFragmentDirections.actionQuotationFragmentToAddQuotationFragment(
                        null,
                        getString(R.string.add),
                        null,null
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

        //Initializing ViewModel class
        quotationViewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)

        //Get list of quotations
        quotationViewModel.getQuotationList(getString(R.string.api_key)).observe(viewLifecycleOwner, Observer {
            it?.let {
                quotationListAdapter.submitList(it.posts)
                quotationBinding.noQuotationTv.visibility = View.GONE
                quotationViewModel.createJobNoList(it.posts!!)
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

        //Observe if quotation item is clicked
        quotationViewModel.navigateToAddQuotationFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    QuotationFragmentDirections.actionQuotationFragmentToAddQuotationFragment(
                        it,
                        getString(R.string.update),
                        null, null
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

                val searchBottomSheet = SearchCustomerBottomSheetFragment(
                    getString(R.string.quotation_List),jobNoList.toMutableList(),this, this
                ,this, this ,
                    this, this,
                    this, this
                )
                searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
            }
        }
        return true
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost) {

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
