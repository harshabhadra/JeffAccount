package com.example.jeffaccount.ui.home.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.CompanyFragmentBinding
import com.example.jeffaccount.ui.MainActivity

class CompanyFragment : Fragment() {

    private lateinit var companyBinding: CompanyFragmentBinding

    companion object {
        fun newInstance() = CompanyFragment()
    }

    private lateinit var viewModel: CompanyViewModel
    private lateinit var companyListAdapter: CompanyListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        //Initializing DataBinding
        companyBinding = CompanyFragmentBinding.inflate(inflater, container, false)

        //Set on click listener to fab button
        companyBinding.companyFab.setOnClickListener {
            findNavController().navigate(
                CompanyFragmentDirections
                    .actionCompanyFragmentToAddCompanyFragment(null, getString(R.string.add))
            )
        }

        //Setting up company recycler
        val companyRecycler = companyBinding.companyRecycler
        companyListAdapter = CompanyListAdapter(CompanyClickListener {
            viewModel.navigateToAddCompany(it)
        })
        companyRecycler.adapter = companyListAdapter

        return companyBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(CompanyViewModel::class.java)

        val activity = activity as MainActivity
        activity.setToolbarText(getString(R.string.company))
        //Getting list of companies
        viewModel.getCompanyList().observe(viewLifecycleOwner, Observer {
            it?.let {
                companyListAdapter.submitList(it.posts)
                companyBinding.noCompanyTv.visibility = View.GONE
            } ?: let {
                companyBinding.noCompanyTv.visibility = View.VISIBLE
            }
        })

        //Observe when to addCompany Fragment
        viewModel.navigateToAddFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    CompanyFragmentDirections
                        .actionCompanyFragmentToAddCompanyFragment(it, getString(R.string.update))
                )
                viewModel.doneNavigating()
            }
        })
    }

}
