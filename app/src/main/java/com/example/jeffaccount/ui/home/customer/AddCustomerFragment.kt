package com.example.jeffaccount.ui.home.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jeffaccount.databinding.AddCustomerFragmentBinding


class AddCustomerFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddCustomerFragment()
    }

    private lateinit var viewModel: CustomerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val addCustomerBinding = AddCustomerFragmentBinding.inflate(inflater,container,false)

        addCustomerBinding.customerSaveButton.setOnClickListener{

            val name = addCustomerBinding.customerNameTextInput.text.toString()
            val street = addCustomerBinding.customerAddressTextInput.text.toString()
            val country = addCustomerBinding.customerCountryTv.text.toString()
            val postCode = addCustomerBinding.customerPostCodeTextInput.text.toString().toInt()
            val phone = addCustomerBinding.customerTelephoneTextInput.text.toString().toInt()
            val email = addCustomerBinding.customerEmailTextInput.text.toString()
            val web = addCustomerBinding.customerWebTextInput.text.toString()
            addCustomer(name,street,country,postCode,phone,email,web)
        }
        return addCustomerBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)
    }

    //Add Customer
    private fun addCustomer(customerName: String,
                    streetAdd: String,
                    coutry: String,
                    postCode: Int,
                    telephone: Int,
                    email: String,
                    web: String){
        viewModel.addCustomer(customerName, streetAdd, coutry, postCode, telephone, email, web).observe(viewLifecycleOwner,
            Observer {
                it?.let {
                    Toast.makeText(context,it,Toast.LENGTH_LONG).show()
                }
            })
    }
}
