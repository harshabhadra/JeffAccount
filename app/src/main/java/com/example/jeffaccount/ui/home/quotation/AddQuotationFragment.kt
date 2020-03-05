package com.example.jeffaccount.ui.home.quotation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddQuotationFragmentBinding
import com.example.jeffaccount.databinding.FragmentQuotationBinding


class AddQuotationFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddQuotationFragment()
    }

    private lateinit var viewModel: AddQuotationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val quotationBinding = AddQuotationFragmentBinding.inflate(inflater,container,false)

        quotationBinding.saveQuotationButton.setOnClickListener {
            findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
        }
        return quotationBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
