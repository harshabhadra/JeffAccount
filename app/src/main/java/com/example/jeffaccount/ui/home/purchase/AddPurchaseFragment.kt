package com.example.jeffaccount.ui.home.purchase

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddPurchaseFragmentBinding


class AddPurchaseFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddPurchaseFragment()
    }

    private lateinit var viewModel: AddPurchaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val purchaseBinding = AddPurchaseFragmentBinding.inflate(inflater,container,false)

        purchaseBinding.purchaseSaveButton.setOnClickListener {
            view?.findNavController()?.navigate(AddPurchaseFragmentDirections.actionAddPurchaseFragmentToPurchaseFragment())
        }
        return purchaseBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
