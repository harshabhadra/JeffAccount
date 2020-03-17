package com.example.jeffaccount.ui.home.timeSheet

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.TimeSheetFragmentBinding

class TimeSheetFragment : Fragment() {

    companion object {
        fun newInstance() = TimeSheetFragment()
    }

    private lateinit var viewModel: TimeSheetViewModel
    private lateinit var timeSheetBinding: TimeSheetFragmentBinding
    private lateinit var timeSheetAdapter: TimeSheetListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Initializing DataBinding
        timeSheetBinding = TimeSheetFragmentBinding.inflate(inflater, container, false)

        //Set on click listener to fab button
        timeSheetBinding.timeSheetFab.setOnClickListener {
            findNavController().navigate(
                TimeSheetFragmentDirections.actionTimeSheetFragmentToAddTimeSheetFragment(
                    null,
                    getString(R.string.add)
                )
            )
        }

        return timeSheetBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TimeSheetViewModel::class.java)

        //Setting up recyclerView with adapter
        val timeSheetRecycler = timeSheetBinding.timeSheetRecycler
        timeSheetAdapter = TimeSheetListAdapter(TimeSheetClickListener {
            viewModel.navigateWithTimeSheet(it)
        })
        timeSheetRecycler.adapter = timeSheetAdapter

        //Observe when to navigate with timeSheet
        viewModel.navigateToAddTimeSheetFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    TimeSheetFragmentDirections.actionTimeSheetFragmentToAddTimeSheetFragment(
                        it,
                        getString(R.string.update)
                    )
                )
                viewModel.doneNavigatingToAddTimeSheet()
            }
        })
        //Getting list of time sheet from ViewModel class
        viewModel.getTimeSheetList(getString(R.string.api_key)).observe(viewLifecycleOwner, Observer {
            it?.let {
                timeSheetAdapter.submitList(it.posts)
                timeSheetBinding.noTimeSheetTv.visibility = View.GONE
            } ?: let {
                timeSheetBinding.noTimeSheetTv.visibility = View.VISIBLE
            }
        })
    }

}
