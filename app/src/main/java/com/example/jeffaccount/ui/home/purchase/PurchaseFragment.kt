package com.example.jeffaccount.ui.home.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController

import com.example.jeffaccount.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */
class PurchaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_purchase, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.purchase_fab)
        fab.setOnClickListener {
            view.findNavController().navigate(PurchaseFragmentDirections.actionPurchaseFragmentToAddPurchaseFragment())
        }
        return view;
    }

}
