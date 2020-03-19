package com.example.jeffaccount.ui.home.quotation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jeffaccount.databinding.FragmentSearchCustomerBottomSheetBinding
import com.example.jeffaccount.model.Company
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SearchCustomerBottomSheetFragment(val comlist:MutableList<String>) : BottomSheetDialogFragment(),
    TextWatcher {

    private lateinit var searchNameAdapter: SearchNameAdapter
    private lateinit var custList: MutableList<String>
    private lateinit var searchList: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val searchBinding =
            FragmentSearchCustomerBottomSheetBinding.inflate(inflater, container, false)

        custList = comlist
        searchList = comlist

        val nameRecycler = searchBinding.customerNameRecycler
        searchNameAdapter = SearchNameAdapter()
        nameRecycler.adapter = searchNameAdapter
        searchNameAdapter.data = custList

        searchBinding.editText.addTextChangedListener(this)
        return searchBinding.root
    }

    private fun searchList(search:String){
        custList.clear()
        for (name in searchList){
            if (name.toLowerCase(Locale.getDefault()).contains(search)){
                custList.add(name)
            }
        }
        searchNameAdapter.notifyDataSetChanged()
    }

    override fun afterTextChanged(s: Editable?) {
        searchList(s.toString().toLowerCase(Locale.getDefault()))
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}
