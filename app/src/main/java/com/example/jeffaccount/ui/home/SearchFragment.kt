package com.example.jeffaccount.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentSearchBinding
import com.example.jeffaccount.network.asSupplierPost
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.purchase.AddPurchaseViewModel
import com.example.jeffaccount.ui.home.purchase.SearchSupplierAdapter
import com.example.jeffaccount.ui.home.purchase.SearchSupplierClickListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener

class SearchFragment : BottomSheetDialogFragment(), OnItemSelectedListener {

    private lateinit var searchFragmentBinding: FragmentSearchBinding
    private var namesList: MutableList<String> = mutableListOf()
    private lateinit var purchaseViewModel: AddPurchaseViewModel
    private lateinit var comid: String
    private lateinit var searchSupplierAdapter: SearchSupplierAdapter

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        searchFragmentBinding = FragmentSearchBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        purchaseViewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)

        val activity = activity as MainActivity
        activity.setToolbarText(getString(R.string.search_supplier))

        comid = activity.companyDetails.comid

        val arguments = SearchFragmentArgs.fromBundle(arguments!!)
        val names = arguments.names

        //Setting up the spinner
        names?.let {
            namesList.addAll(names.names)
            val searchAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, namesList)
            searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            searchFragmentBinding.searchableSpinner.setAdapter(searchAdapter)
        }

        val searchableSpinner = searchFragmentBinding.searchableSpinner
        searchableSpinner.setOnItemSelectedListener(this)

        //Setting up the recyclerView
        searchSupplierAdapter = SearchSupplierAdapter(SearchSupplierClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("What you'd like to do?")
            builder.setPositiveButton("Select", DialogInterface.OnClickListener() { dialog,which->
                dialog.dismiss()
                dismiss()
                
            }).setNegativeButton("Modify", DialogInterface.OnClickListener { dialog, which ->
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToAddSupplierFragment(
                        it.asSupplierPost(),
                        getString(R.string.edit)
                    )
                )
            })
           val dialog =  builder.create()
            dialog.show()
        })

        searchFragmentBinding.searchRecyclerView.adapter = searchSupplierAdapter

        return searchFragmentBinding.root
    }

    override fun onNothingSelected() {

    }

    override fun onItemSelected(view: View?, position: Int, id: Long) {

        val name = namesList[position]
        getSupplierList(name)
    }

    private fun getSupplierList(name: String) {

        purchaseViewModel.getSearchSupplierList(comid, name, "AngE9676#254r5")
            .observe(viewLifecycleOwner,
                Observer {

                    it?.let {
                        searchSupplierAdapter.submitList(it.posts)
                    }
                })
    }
}
