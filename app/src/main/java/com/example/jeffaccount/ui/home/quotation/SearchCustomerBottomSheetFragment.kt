package com.example.jeffaccount.ui.home.quotation

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.network.SearchCustomer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class SearchCustomerBottomSheetFragment(type:String,val comlist: MutableList<String>, itemClickListener:OnSearchItemClickListener) :
    BottomSheetDialogFragment(),
    TextWatcher, OnItemClickListener {

    private lateinit var searchNameAdapter: SearchNameAdapter
    private lateinit var searchItemAdapter: SearchItemAdapter

    private lateinit var custList: ArrayList<String>
    private lateinit var searchList: ArrayList<String>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var nameRecyclerView: RecyclerView
    private lateinit var nameItemRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var quotaitonViewModel: AddQuotationViewModel
    private var searItemClickListener: OnSearchItemClickListener = itemClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        custList = arrayListOf()
        searchList = arrayListOf()
        custList.addAll(comlist)
        searchList.addAll(comlist)
        searchNameAdapter = SearchNameAdapter(custList, this)
        searchItemAdapter = SearchItemAdapter(SearchClickListener {
            it?.let {
                searItemClickListener.onSearchItemClick(it)
                dismiss()
            }
        })

        //Initializing ViewModel class
        quotaitonViewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val view = View.inflate(context, R.layout.fragment_search_customer_bottom_sheet, null)
        val linearLayout: LinearLayout = view.findViewById(R.id.root_layout)
        val params: LinearLayout.LayoutParams =
            linearLayout.layoutParams as (LinearLayout.LayoutParams)
        params.height = getScreenHeight()
        linearLayout.layoutParams = params

        //Setting up name recyclerView
        nameRecyclerView = view.findViewById(R.id.customer_name_recycler)
        nameRecyclerView.setHasFixedSize(true)
        nameRecyclerView.adapter = searchNameAdapter

        //Setting up name item recyclerView
        nameItemRecyclerView = view.findViewById(R.id.customer_item_recycler)
        nameItemRecyclerView.setHasFixedSize(true)
        nameItemRecyclerView.adapter = searchItemAdapter

        searchEditText = view.findViewById(R.id.editText)
        searchEditText.addTextChangedListener(this)
        bottomSheetDialog.setContentView(view)
        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        searItemClickListener = context as OnSearchItemClickListener
    }

    private fun searchList(search: String) {
        searchList.clear()
        for (i in custList.indices) {
            Timber.e("Names: $custList.[i]")
            if (custList[i].toLowerCase().trim().contains(search)) {
                Timber.e("Name is: $custList[i]")
                searchList.add(custList[i])
            } else {
                Timber.e("Not found")
            }
        }
        searchNameAdapter = SearchNameAdapter(searchList, this)
        nameRecyclerView.adapter = searchNameAdapter
    }

    //Get Screen Height
    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        searchList(s.toString().toLowerCase())
    }

    override fun onItemClick(name: String) {

        quotaitonViewModel.searchCustomer(name, "AngE9676#254r5").observe(this,
            androidx.lifecycle.Observer {
                Timber.e("Search list size: ${it.customerList?.size}")
                nameRecyclerView.visibility = View.GONE
                searchItemAdapter.submitList(it.customerList)
            })
    }
}

public interface OnSearchItemClickListener {
    fun onSearchItemClick(searchCustomer: SearchCustomer)
}
