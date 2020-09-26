package com.example.jeffaccount.ui.home.customer

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.model.Post
import com.example.jeffaccount.network.SearchCustomer
import com.example.jeffaccount.network.SearchSupplierPost
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */

private lateinit var customerLisAdapter: CusomerListAdapter
private lateinit var noCusTv: TextView
private var custNameList: MutableSet<String> = mutableSetOf()
private var customerList:MutableList<Post> = mutableListOf()
private lateinit var comid:String
class CustomerFragment : Fragment(), OnSearchItemClickListener, OnSearchSupplierClickListener,
    OnCustomerNameClickListener, OnSupplierNameClickListener, OnQuotationJobNoClickListener,
    OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener {

    private lateinit var customerViewModel: CustomerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.add_customer_fab)
        val customerRecycler = view.findViewById<RecyclerView>(R.id.customer_recyclerView)
        noCusTv = view.findViewById<TextView>(R.id.no_customer_tv)

        //Setting up customer list
        customerLisAdapter = CusomerListAdapter(CustomerItemListener { customer ->
            customerViewModel.onCustomerItemClick(customer)
        })
        customerRecycler.adapter = customerLisAdapter

        //Initializing ViewModel class
        customerViewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)

        //Set onClick listener to the add fab button
        fab.setOnClickListener {
            this.findNavController().navigate(
                CustomerFragmentDirections.actionCustomerFragmentToAddCustomer(
                    Post(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                    ""
                    ), getString(R.string.add)
                )
            )
        }

        //Observe when to move to Add Task fragment with customer values
        customerViewModel.navigateToAddCustomerFragment.observe(
            viewLifecycleOwner,
            Observer { customer ->
                customer?.let {
                    this.findNavController().navigate(
                        CustomerFragmentDirections
                            .actionCustomerFragmentToAddCustomer(customer, getString(R.string.edit))
                    )
                    customerViewModel.doneNavigating()
                }
            })

        //Observe name List
        customerViewModel.nameList.observe(viewLifecycleOwner, Observer {
            it?.let {
                custNameList.addAll(it)
            }
        })

        customerViewModel.custList.observe(viewLifecycleOwner, Observer {
            it?.let {
                customerLisAdapter.submitList(it)
                customerLisAdapter.notifyDataSetChanged()

            }
        })

        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = activity as MainActivity
        activity.setToolbarText(getString(R.string.customer))
        comid = activity.companyDetails.comid
        //Call to get list of customers
        getCustomerList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_search -> {
                if (custNameList.isNotEmpty()) {
                    val searchCustomerBottomSheetFragment = SearchCustomerBottomSheetFragment(
                        getString(R.string.customer),
                        custNameList.toMutableList(), this, this,
                        this, this, this,
                        this, this,
                        this
                    )
                    searchCustomerBottomSheetFragment.show(
                        activity!!.supportFragmentManager,
                        searchCustomerBottomSheetFragment.tag
                    )
                }
            }
        }
        return true
    }

    //Get CustomerList
    private fun getCustomerList() {

        customerViewModel.getCustomerList(comid).observe(viewLifecycleOwner, Observer {
            it?.let {
                customerLisAdapter.submitList(it.posts)
                noCusTv.visibility = View.INVISIBLE
                customerViewModel.createCustomerNameList(it.posts)
            } ?: let {
                noCusTv.visibility = View.VISIBLE
            }
        })
    }

    override fun onSearchItemClick(searchCustomer: SearchCustomer) {
    }

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost,action:String) {

    }

    override fun onCustomerNameClick(name: String) {

       name?.let {
           customerViewModel.searchCustomer(comid,name,"AngE9676#254r5").observe(viewLifecycleOwner, Observer {
               it?.let {
                   val list = it.customerList
                   customerViewModel.convertSearchCustomerToCustomer(list!!)
               }
           })
       }
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
