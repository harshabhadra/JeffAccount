package com.example.jeffaccount.ui.home.supplier

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.example.jeffaccount.R
import com.example.jeffaccount.model.SupPost
import com.example.jeffaccount.network.SearchCustomer
import com.example.jeffaccount.network.SearchSupplierPost
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */

private lateinit var supplierListAdapter: SupplierListAdapter
private lateinit var addSupplierViewModel: AddSupplierViewModel
private var supNameList:MutableSet<String> = mutableSetOf()
class SupplierFragment : Fragment(), OnSearchItemClickListener, OnSearchSupplierClickListener,
    OnCustomerNameClickListener, OnSupplierNameClickListener, OnQuotationJobNoClickListener,
    OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener {

    private lateinit var comid:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_supplier, container, false)

        //Initializing ViewModel class
        addSupplierViewModel = ViewModelProvider(this).get(AddSupplierViewModel::class.java)

        val activity = activity as MainActivity
        activity.setToolbarText("Suppliers")
        comid = activity.companyDetails.comid

        val fab = view.findViewById<FloatingActionButton>(R.id.supplier_fab)
        val supplierRecyclerView:RecyclerView = view.findViewById(R.id.supplier_recycler)
        val noSupplierTv:TextView = view.findViewById(R.id.no_supplier_tv)
        supplierListAdapter = SupplierListAdapter(SupplierItemListener {
            addSupplierViewModel.onSupplierItemClick(it)
        })

        supplierRecyclerView.adapter = supplierListAdapter

        //Get list of suppliers
        addSupplierViewModel.getSuppliers(comid).observe(viewLifecycleOwner, Observer {
            it?.let {
                supplierListAdapter.submitList(it.posts)
                noSupplierTv.visibility = View.GONE
                addSupplierViewModel.createSupplierNameList(it.posts)
            }?:let {
                Timber.e("No supplier")
                noSupplierTv.visibility = View.VISIBLE
            }
        })

        addSupplierViewModel.navigateToAddSupplierFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(SupplierFragmentDirections.actionSupplierFragmentToAddSupplierFragment(it,getString(R.string.edit)))
                addSupplierViewModel.doneNavigating()
            }
        })

        //Observe name list
        addSupplierViewModel.nameList.observe(viewLifecycleOwner, Observer {
            it?.let {
                supNameList.addAll(it)
            }
        })

        //observe list after search
        addSupplierViewModel.supplierList.observe(viewLifecycleOwner, Observer {
            it?.let {
                supplierListAdapter.submitList(it)
                supplierListAdapter.notifyDataSetChanged()
            }
        })
        //Add supplier
        fab.setOnClickListener {
            view.findNavController().navigate(SupplierFragmentDirections.actionSupplierFragmentToAddSupplierFragment(
                SupPost(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                ),"add"
            ))
        }
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_search ->{
                if (supNameList.isNotEmpty()){
                    val searchCustomerBottomSheetFragment = SearchCustomerBottomSheetFragment(
                        getString(R.string.supplier),
                        supNameList.toMutableList(),this,this,
                    this, this,
                    this, this,
                    this, this)
                    searchCustomerBottomSheetFragment.show(activity!!.supportFragmentManager, searchCustomerBottomSheetFragment.tag)
                }
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
        addSupplierViewModel.getSearchSupplierList(comid,name,"AngE9676#254r5").observe(viewLifecycleOwner,
            Observer {
                val list = it.posts
                addSupplierViewModel.searchSupplierToSupplier(list!!)
            })
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
