package com.example.jeffaccount.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.JeffApplication
import com.example.jeffaccount.LogInActivity
import com.example.jeffaccount.R
import com.example.jeffaccount.utils.createPreviewDialog
import com.example.jeffaccount.dataBase.LogInCred
import com.example.jeffaccount.databinding.HomeFragmentBinding
import com.example.jeffaccount.ui.MainActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
        private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var loginCred: LogInCred
    private var createDialog = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Request permission
        requestReadPermissions()
        val homeBinding = HomeFragmentBinding.inflate(inflater, container, false)
        //Setting up Home Recycler
        val homeRecyclerAdapter = HomeRecyclerAdapter()
        val layoutManager = GridLayoutManager(context, 2)
        homeBinding.homeRecycler.layoutManager =
             layoutManager as RecyclerView.LayoutManager?
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
                R.drawable.invoice,
                getString(R.string.invoice)
            ),
            Home(
                R.drawable.worksheet,
                getString(R.string.worksheet)
            ),
            Home(
                R.drawable.supplier,
                getString(R.string.about)
            )
        )
        homeBinding.companyDetailsTv.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddCompanyFragment())
        }
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

        val arguments = arguments?.let { HomeFragmentArgs.fromBundle(it) }
        val filePath = arguments?.filepath
        filePath?.let {
            Timber.e("Path: $it")
            if(!filePath.equals("welcome")) {
                viewModel.setShowDialog(DialogStatus.SHOW)
            }else{
                viewModel.setShowDialog(DialogStatus.HIDE)
            }
        }?:let {
            Toast.makeText(context,"Please try again",Toast.LENGTH_SHORT).show()
        }

        //Observe dialog status
        viewModel.showDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == DialogStatus.SHOW){
                    createPreviewDialog(
                        filePath!!,
                        context!!,
                        activity!!
                    )
                    viewModel.setShowDialog(DialogStatus.HIDE)
                }
            }
        })

        val activity = activity as MainActivity
        viewModel.logInCredDeleted.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                        viewModel.loginCredDeleted()
                        val intent = Intent(activity, LogInActivity::class.java)
                        startActivity(intent)
                        activity.finish()
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
            getString(R.string.time_sheet) -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTimeSheetFragment())
            }
            getString(R.string.invoice)->{
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToInvoiceFragment())
            }
            getString(R.string.worksheet)->{
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCreateWorkSheetFragment())
            }
            getString(R.string.about)->{
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAboutFragment())
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
        }else if (itemId == R.id.change_password){

            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToChangePasswordFragment())
        }
        return true
    }


    //Function to request read and write storage
    private fun requestReadPermissions() {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(
                            context,
                            "All permissions are granted by user!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        //openSettingsDialog();
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError) {
                    Toast.makeText(context, "Some Error! ", Toast.LENGTH_SHORT).show()
                }
            })
            .onSameThread()
            .check()
    }
}
