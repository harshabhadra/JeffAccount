package com.example.jeffaccount.ui.home.quotation

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
import com.example.jeffaccount.databinding.FragmentQuotationBinding
import com.example.jeffaccount.model.QuotationPost
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */
class QuotationFragment : Fragment() {

    private lateinit var quotationBinding: FragmentQuotationBinding
    private lateinit var quotationListAdapter: QuotationListAdapter
    private lateinit var quotationViewModel: AddQuotationViewModel
    private lateinit var quotaitonPost: QuotationPost
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        quotationBinding = FragmentQuotationBinding.inflate(inflater, container, false)

        val fab = quotationBinding.quotationFab
        fab.setOnClickListener {
            findNavController()
                .navigate(
                    QuotationFragmentDirections.actionQuotationFragmentToAddQuotationFragment(
                        null,
                        getString(R.string.add)
                    )
                )
        }

        //Setting up quotation recycler
        val quotationRecycler = quotationBinding.quotationRecycler
        quotationListAdapter = QuotationListAdapter(QuotationClickListener {
            it?.let {
                quotationViewModel.onQuotationItemClick(it)
            }
        })
        quotationRecycler.adapter = quotationListAdapter

        //Setting up RecyclerView
        return quotationBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Initializing ViewModel class
        quotationViewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)

        //Get list of quotations
        quotationViewModel.getQuotationList().observe(viewLifecycleOwner, Observer {
            it?.let {
                quotationListAdapter.submitList(it.posts)
                quotationBinding.noQuotationTv.visibility = View.GONE
            } ?: let {
                quotationBinding.noQuotationTv.visibility = View.VISIBLE
            }
        })

        //Observe if quotation item is clicked
        quotationViewModel.navigateToAddQuotationFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    QuotationFragmentDirections.actionQuotationFragmentToAddQuotationFragment(
                        it,
                        getString(R.string.update)
                    )
                )
                quotationViewModel.doneNavigating()
            }
        })
    }
}
