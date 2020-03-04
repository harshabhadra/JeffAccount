package com.example.jeffaccount.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.HomeFragmentBinding


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeBinding = HomeFragmentBinding.inflate(inflater,container,false)

        val homeRecyclerAdapter = HomeRecyclerAdapter()
        homeBinding.homeRecycler.layoutManager = GridLayoutManager(context,2)
        homeBinding.homeRecycler.adapter = homeRecyclerAdapter

        val homeList = listOf(
            Home(
                R.drawable.ic_launcher_background,
                getString(R.string.add_customer)
            ),
            Home(
                R.drawable.ic_launcher_background,
                getString(R.string.add_supplier)
            ),
            Home(
                R.drawable.ic_launcher_foreground,
                getString(R.string.quotation)
            ),
            Home(
                R.drawable.ic_launcher_background,
                getString(R.string.purchase)
            ),
            Home(
                R.drawable.ic_launcher_background,
                getString(R.string.invoice)
            ),
            Home(
                R.drawable.ic_launcher_background,
                getString(R.string.worksheet)
            )
        )
        homeRecyclerAdapter.submitList(homeList)
        homeRecyclerAdapter.onItemClick ={
            pos, view ->
            val home = homeRecyclerAdapter.getItemName(pos)
            navigateTo(home.title)
        }
        return homeBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    //Function to Navigate to destination
    fun navigateTo(name:String){
        when(name){
             getString(R.string.add_customer)->{
                 findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddCustomer())
             }
            getString(R.string.add_supplier)->{

                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddSupplierFragment())
            }
            getString(R.string.quotation)->{
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddQuotationFragment())
            }
        }
    }
}
