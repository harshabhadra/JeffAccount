package com.example.jeffaccount.ui.home.supplier

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddSupplierFragmentBinding


class AddSupplierFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddSupplierFragment()
    }

    private lateinit var viewModel: AddSupplierViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val addsupplierBinding = AddSupplierFragmentBinding.inflate(inflater,container,false)

        addsupplierBinding.saveSupplierButton.setOnClickListener {
            findNavController().navigate(AddSupplierFragmentDirections.actionAddSupplierFragmentToSupplierFragment())
        }
        return  addsupplierBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddSupplierViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
