package com.example.jeffaccount.ui.home.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.databinding.AddCustomerFragmentBinding


class AddCustomerFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddCustomerFragment()
    }

    private lateinit var viewModel: AddCustomerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val addCustomerBinding = AddCustomerFragmentBinding.inflate(inflater,container,false)

        addCustomerBinding.customerSaveButton.setOnClickListener{
            findNavController().navigate(AddCustomerFragmentDirections.actionAddCustomerToCustomerFragment())
        }
        return addCustomerBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddCustomerViewModel::class.java)
        // TODO: Use the ViewModel
    }



}
