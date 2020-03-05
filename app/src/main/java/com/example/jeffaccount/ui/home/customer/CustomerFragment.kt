package com.example.jeffaccount.ui.home.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentCustomerBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */
class CustomerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.add_customer_fab)

        fab.setOnClickListener {
            view.findNavController().navigate(CustomerFragmentDirections.actionCustomerFragmentToAddCustomer())
        }
        return view
    }

}
