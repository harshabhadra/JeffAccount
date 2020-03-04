package com.example.jeffaccount

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider


class AddSupplierFragment : Fragment() {

    companion object {
        fun newInstance() = AddSupplierFragment()
    }

    private lateinit var viewModel: AddSupplierViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_supplier_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddSupplierViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
