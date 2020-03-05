package com.example.jeffaccount.ui.home.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.example.jeffaccount.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */

private lateinit var customerLisAdapter: CusomerListAdapter
private lateinit var noCusTv:TextView
class CustomerFragment : Fragment() {

    private lateinit var customerViewModel: CustomerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.add_customer_fab)
        val customerRecycler = view.findViewById<RecyclerView>(R.id.customer_recyclerView)
         noCusTv = view.findViewById<TextView>(R.id.no_customer_tv)

        //Setting up customer list
        customerLisAdapter = CusomerListAdapter()
        customerRecycler.adapter = customerLisAdapter

        //Initializing ViewModel class
        customerViewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)

        //Call to get list of customers
        getCustomerList()

        //Set onClick listener to the add fab button
        fab.setOnClickListener {
            view.findNavController().navigate(CustomerFragmentDirections.actionCustomerFragmentToAddCustomer())
        }
        return view
    }

    //Get CustomerList
    private fun getCustomerList(){

        customerViewModel.getCustomerList().observe(viewLifecycleOwner, Observer {
            it?.let {

                customerLisAdapter.submitList(it.posts)
                noCusTv.visibility = View.INVISIBLE
            }?:let {
                noCusTv.visibility = View.VISIBLE
            }
        })
    }
}
