package com.example.jeffaccount

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider


class AddQuotationFragment : Fragment() {

    companion object {
        fun newInstance() = AddQuotationFragment()
    }

    private lateinit var viewModel: AddQuotationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_quotation_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
