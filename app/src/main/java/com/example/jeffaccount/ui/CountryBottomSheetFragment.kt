package com.example.jeffaccount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jeffaccount.databinding.CountryBottomSheetFragmentBinding
import com.example.jeffaccount.model.Country
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CountryBottomSheetFragment(val countryItemClickListener: OnCountryItemClickListener) : BottomSheetDialogFragment() {

    private lateinit var countryBinding: CountryBottomSheetFragmentBinding
    private lateinit var countryListAdapter: CountryListAdapter


    private lateinit var viewModel: CountryBottomSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        countryBinding = CountryBottomSheetFragmentBinding.inflate(inflater, container, false)

        //Setting up recyclerView with adapter
        countryListAdapter = CountryListAdapter(CountryClickListener {
            countryItemClickListener.onCountryItemClick(it)
            dismiss()
        })
        countryBinding.countryListRecycler.adapter = countryListAdapter

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(CountryBottomSheetViewModel::class.java)

        viewModel.getCountryList().observe(viewLifecycleOwner, Observer {
            it?.let {
                countryListAdapter.submitList(it)
            }
        })
        return countryBinding.root
    }

}

interface OnCountryItemClickListener{
    fun onCountryItemClick(country:Country)
}
