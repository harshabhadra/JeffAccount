package com.example.jeffaccount.ui.home.supplier

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.LogInViewModel

import com.example.jeffaccount.R
import com.example.jeffaccount.model.SupPost
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */

private lateinit var supplierListAdapter: SupplierListAdapter
private lateinit var addSupplierViewModel: AddSupplierViewModel
class SupplierFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_supplier, container, false)

        //Initializing ViewModel class
        addSupplierViewModel = ViewModelProvider(this).get(AddSupplierViewModel::class.java)

        val fab = view.findViewById<FloatingActionButton>(R.id.supplier_fab)
        val supplierRecyclerView:RecyclerView = view.findViewById(R.id.supplier_recycler)
        val noSupplierTv:TextView = view.findViewById(R.id.no_supplier_tv)
        supplierListAdapter = SupplierListAdapter(SupplierItemListener {
            addSupplierViewModel.onSupplierItemClick(it)
        })

        supplierRecyclerView.adapter = supplierListAdapter

        //Get list of suppliers
        addSupplierViewModel.getSuppliers().observe(viewLifecycleOwner, Observer {
            it?.let {
                supplierListAdapter.submitList(it.posts)
                noSupplierTv.visibility = View.GONE
            }?:let {
                Timber.e("No supplier")
                noSupplierTv.visibility = View.VISIBLE
            }
        })

        addSupplierViewModel.navigateToAddSupplierFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(SupplierFragmentDirections.actionSupplierFragmentToAddSupplierFragment(it,getString(R.string.edit)))
                addSupplierViewModel.doneNavigating()
            }
        })
        //Add supplier
        fab.setOnClickListener {
            view.findNavController().navigate(SupplierFragmentDirections.actionSupplierFragmentToAddSupplierFragment(
                SupPost("","","","","","","",""),"add"
            ))
        }
        return view
    }

}
