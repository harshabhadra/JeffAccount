package com.example.jeffaccount.ui.home.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentPurchaseBinding
import com.example.jeffaccount.ui.home.HomeRecyclerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */
class PurchaseFragment : Fragment() {

    private lateinit var purchaseBinding: FragmentPurchaseBinding
    private lateinit var purchaseViewModel: AddPurchaseViewModel
    private lateinit var purchaseLisAdapter: PurchaseListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        purchaseBinding = FragmentPurchaseBinding.inflate(inflater, container, false)
        val fab = purchaseBinding.purchaseFab
        fab.setOnClickListener {
            findNavController().navigate(
                PurchaseFragmentDirections.actionPurchaseFragmentToAddPurchaseFragment(
                    null,
                    getString(R.string.add)
                )
            )
        }

        //Setting up recyclerView
        val purchaseRecyclerView = purchaseBinding.purchaseRecycler
        purchaseLisAdapter = PurchaseListAdapter(PurchaseClickListener {
            purchaseViewModel.navigateToAddPurchaseFragment(it)
        })
        purchaseRecyclerView.adapter = purchaseLisAdapter

        return purchaseBinding.root;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Initializing ViewModel class
        purchaseViewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)

        //Getting list of purchase from view model class
        purchaseViewModel.getPurchaseList().observe(viewLifecycleOwner, Observer {
            it?.let {
                purchaseLisAdapter.submitList(it.posts)
                purchaseBinding.purchaseNoTv.visibility = View.GONE
            } ?: let {
                purchaseBinding.purchaseNoTv.visibility = View.VISIBLE
            }
        })

        //Observe when to navigate to add purchase fragment
        purchaseViewModel.navigateToAddPurchaseFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    PurchaseFragmentDirections.actionPurchaseFragmentToAddPurchaseFragment(
                        it,
                        getString(R.string.update)
                    )
                )
                purchaseViewModel.doneNavigating()
            }
        })
    }
}
