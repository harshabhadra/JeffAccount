package com.example.jeffaccount.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.JeffApplication
import com.example.jeffaccount.LogInActivity
import com.example.jeffaccount.R
import com.example.jeffaccount.dataBase.LogInCred
import com.example.jeffaccount.databinding.HomeFragmentBinding
import com.example.jeffaccount.ui.MainActivity
import timber.log.Timber


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
        private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var loginCred: LogInCred

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeBinding = HomeFragmentBinding.inflate(inflater, container, false)
        //Setting up Home Recycler
        val homeRecyclerAdapter = HomeRecyclerAdapter()
        homeBinding.homeRecycler.layoutManager =
            GridLayoutManager(context, 2) as RecyclerView.LayoutManager?
        homeBinding.homeRecycler.adapter = homeRecyclerAdapter

        setHasOptionsMenu(true)

        //Creating the list of Item in Home
        val homeList = listOf(
            Home(
                R.drawable.customer,
                getString(R.string.customer)
            ),
            Home(
                R.drawable.supplier,
                getString(R.string.supplier)
            ),
            Home(
                R.drawable.quote,
                getString(R.string.quotation)
            ),
            Home(
                R.drawable.purchase,
                getString(R.string.purchase)
            ),
            Home(
                R.drawable.worksheet,
                getString(R.string.time_sheet)
            ),
            Home(
                R.drawable.com,
                getString(R.string.company)
            )
        )
        homeRecyclerAdapter.submitList(homeList)
        homeRecyclerAdapter.onItemClick = { pos, view ->
            val home = homeRecyclerAdapter.getItemName(pos)
            navigateTo(home.title)
        }
        return homeBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireNotNull(this.activity).application as JeffApplication
        val viewModelFactory = HomeViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel

        viewModel.logInCredDeleted.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    val intent = Intent(activity, LogInActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }
        })

    }

    //Function to Navigate to destination
    private fun navigateTo(name: String) {
        when (name) {
            getString(R.string.customer) -> {
                this.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToCustomerFragment())
            }
            getString(R.string.supplier) -> {

                this.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToSupplierFragment())
            }
            getString(R.string.quotation) -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToQuotationFragment())
            }
            getString(R.string.purchase) -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPurchaseFragment())
            }
            getString(R.string.company) -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCompanyFragment())
            }
            getString(R.string.time_sheet) -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTimeSheetFragment())
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val activity = context as MainActivity
        loginCred = activity.loginCred!!
        Timber.e("username: ${loginCred.userName}")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId
        if (itemId == R.id.log_out) {
            viewModel.deleteLogInCred(loginCred)
        }
        return true
    }
}
