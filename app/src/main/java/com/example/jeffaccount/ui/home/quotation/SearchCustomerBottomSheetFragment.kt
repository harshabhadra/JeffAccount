package com.example.jeffaccount.ui.home.quotation

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentSearchCustomerBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class SearchCustomerBottomSheetFragment(val comlist:MutableList<String>) : BottomSheetDialogFragment(),
    TextWatcher {

    private lateinit var searchNameAdapter: SearchNameAdapter
    private lateinit var custList: ArrayList<String>
    private lateinit var searchList: ArrayList<String>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var nameRecyclerView: RecyclerView
    private lateinit var searchEditText:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        custList = arrayListOf()
        searchList = arrayListOf()
        custList.addAll(comlist)
        searchList.addAll(comlist)
        searchNameAdapter = SearchNameAdapter(custList)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val view = View.inflate(context,R.layout.fragment_search_customer_bottom_sheet,null)
        val linearLayout:LinearLayout = view.findViewById(R.id.root_layout)
        val params:LinearLayout.LayoutParams = linearLayout.layoutParams as (LinearLayout.LayoutParams)
        params.height = getScreenHeight()
        linearLayout.layoutParams = params

        nameRecyclerView = view.findViewById(R.id.customer_name_recycler)
        nameRecyclerView.setHasFixedSize(true)
        nameRecyclerView.adapter = searchNameAdapter

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

    private fun searchList(search:String){
        searchList.clear()
        for (i in custList.indices){
            Timber.e("Names: $custList.[i]")
            if (custList[i].toLowerCase().trim().contains(search)){
                Timber.e("Name is: $custList[i]")
                searchList.add(custList[i])
            }else{
                Timber.e("Not found")
            }
        }
        searchNameAdapter = SearchNameAdapter(searchList)
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
}
