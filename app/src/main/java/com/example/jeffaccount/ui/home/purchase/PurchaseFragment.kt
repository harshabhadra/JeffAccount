package com.example.jeffaccount.ui.home.purchase

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentPurchaseBinding
import com.example.jeffaccount.network.SearchCustomer
import com.example.jeffaccount.network.SearchSupplierPost
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.HomeRecyclerAdapter
import com.example.jeffaccount.ui.home.quotation.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */
class PurchaseFragment : Fragment(), OnSearchItemClickListener, OnSearchSupplierClickListener,
    OnCustomerNameClickListener, OnSupplierNameClickListener, OnQuotationJobNoClickListener,
    OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener {

    private lateinit var purchaseBinding: FragmentPurchaseBinding
    private lateinit var purchaseViewModel: AddPurchaseViewModel
    private lateinit var purchaseLisAdapter: PurchaseListAdapter
    private var jobNoList:MutableSet<String> = mutableSetOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        purchaseBinding = FragmentPurchaseBinding.inflate(inflater, container, false)
        val fab = purchaseBinding.purchaseFab
        fab.setOnClickListener {
            findNavController().navigate(
                PurchaseFragmentDirections.actionPurchaseFragmentToAddPurchaseFragment(
                    null,
                    getString(R.string.add), null,null
                )
            )
        }

        val activity = activity as MainActivity
        activity.setToolbarText("Purchase")
        //Setting up recyclerView
        val purchaseRecyclerView = purchaseBinding.purchaseRecycler
        purchaseLisAdapter = PurchaseListAdapter(PurchaseClickListener {
            purchaseViewModel.navigateToAddPurchaseFragment(it)
        })
        purchaseRecyclerView.adapter = purchaseLisAdapter

        setHasOptionsMenu(true)
        return purchaseBinding.root;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Initializing ViewModel class
        purchaseViewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)

        //Getting list of purchase from view model class
        purchaseViewModel.getPurchaseList("AngE9676#254r5").observe(viewLifecycleOwner, Observer {
            it?.let {
                purchaseLisAdapter.submitList(it.posts)
                purchaseBinding.purchaseNoTv.visibility = View.GONE
                purchaseViewModel.createJobNoList(it.posts)
            } ?: let {
                purchaseBinding.purchaseNoTv.visibility = View.VISIBLE
            }
        })

        //observe job no list
        purchaseViewModel.jobNoList.observe(viewLifecycleOwner, Observer {
            it?.let {
                jobNoList.addAll(it)
            }
        })

        //Observe when to navigate to add purchase fragment
        purchaseViewModel.navigateToAddPurchaseFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    PurchaseFragmentDirections.actionPurchaseFragmentToAddPurchaseFragment(
                        it,
                        getString(R.string.update), null,null
                    )
                )
                purchaseViewModel.doneNavigating()
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
                    getString(R.string.purchase),jobNoList.toMutableList(),this, this
                ,this, this, this ,
                    this, this, this
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
