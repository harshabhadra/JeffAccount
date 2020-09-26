package com.example.jeffaccount.ui.home.purchase

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import kotlinx.android.synthetic.main.search_choice_list_item_layout.view.*

/**
 * A simple [Fragment] subclass.
 */
class PurchaseFragment : Fragment(), OnSearchItemClickListener, OnSearchSupplierClickListener,
    OnCustomerNameClickListener, OnSupplierNameClickListener, OnQuotationJobNoClickListener,
    OnPurchaseJobNoClickListener, OnInvoiceJobNoClickListener, OnTimeSheetJobNoClickListener {

    private lateinit var purchaseBinding: FragmentPurchaseBinding
    private lateinit var purchaseViewModel: AddPurchaseViewModel
    private lateinit var purchaseLisAdapter: PurchaseListAdapter
    private lateinit var comid:String
    private var jobNoList:MutableSet<String> = mutableSetOf()
    private var quotationNoList:MutableSet<String> = mutableSetOf()
    private var customerList:MutableSet<String> = mutableSetOf()
    private var isJobNo:Boolean = false
    private var isQuotationNo:Boolean = false
    private var isCustomerName:Boolean = false

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
                    getString(R.string.add), null,null, null, null
                )
            )
        }


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

        val activity = activity as MainActivity
        activity.setToolbarText("Purchase")
        comid = activity.companyDetails.comid

        //Initializing ViewModel class
        purchaseViewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)

        //Getting list of purchase from view model class
        purchaseViewModel.getPurchaseList(comid,"AngE9676#254r5").observe(viewLifecycleOwner, Observer {
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

        //Observe quotation no list
        purchaseViewModel.quotationList.observe(viewLifecycleOwner, Observer {
            it?.let {
                quotationNoList.addAll(it)
            }
        })

        //Observe customer name list
        purchaseViewModel.customerNameList.observe(viewLifecycleOwner, Observer {
            it?.let {
                customerList.addAll(it)
            }
        })

        //Observe when to navigate to add purchase fragment
        purchaseViewModel.navigateToAddPurchaseFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    PurchaseFragmentDirections.actionPurchaseFragmentToAddPurchaseFragment(
                        it,
                        getString(R.string.update), null,null, null, null
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
                        getString(R.string.purchase_list_job_no),jobNoList.toMutableList(),this, this
                        ,this, this, this ,
                        this, this, this
                    )
                    searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
                    dialog.dismiss()
                }

                quotationnoTv.setOnClickListener {
                    isJobNo = false
                    isQuotationNo = true
                    isCustomerName = false
                    val searchBottomSheet = SearchCustomerBottomSheetFragment(
                        getString(R.string.purchase_list_quotation_no),quotationNoList.toMutableList(),this, this
                        ,this, this, this ,
                        this, this, this
                    )
                    searchBottomSheet.show(activity!!.supportFragmentManager, searchBottomSheet.tag)
                    dialog.dismiss()
                }

                customerNameTv.setOnClickListener {
                    isJobNo = false
                    isQuotationNo = false
                    isCustomerName = true
                    val searchBottomSheet = SearchCustomerBottomSheetFragment(
                        getString(R.string.purchase_list_customer_name),customerList.toMutableList(),this, this
                        ,this, this, this ,
                        this, this, this
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

    override fun onSearchSupplierClick(serchSupplierPost: SearchSupplierPost, action:String) {
    }

    override fun onCustomerNameClick(name: String) {

    }

    override fun onSupplierNameClick(name: String) {

    }

    override fun onQuotationNameClick(name: String) {

    }

    override fun onPurchaseNameClick(name: String) {

        if (isJobNo) {
            purchaseViewModel.searchPurchase(comid, name, null, null, "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        purchaseLisAdapter.submitList(it.posts)
                        purchaseLisAdapter.notifyDataSetChanged()
                    }
                })
        }else if (isQuotationNo){
            purchaseViewModel.searchPurchase(comid,null, name, null,  "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        purchaseLisAdapter.submitList(it.posts)
                        purchaseLisAdapter.notifyDataSetChanged()
                    }
                })
        }else if (isCustomerName){
            purchaseViewModel.searchPurchase(comid, null, null, name, "AngE9676#254r5")
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        purchaseLisAdapter.submitList(it.posts)
                        purchaseLisAdapter.notifyDataSetChanged()
                    }
                })
        }
    }

    override fun onInvoiceJobNoClick(name: String) {

    }

    override fun onTimeSheetJobClick(name: String) {

    }
}
