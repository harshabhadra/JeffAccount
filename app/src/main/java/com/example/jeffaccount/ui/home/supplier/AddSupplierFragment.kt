package com.example.jeffaccount.ui.home.supplier

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddSupplierFragmentBinding


class AddSupplierFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddSupplierFragment()
    }

    private lateinit var viewModel: AddSupplierViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val addsupplierBinding = AddSupplierFragmentBinding.inflate(inflater, container, false)

        val arguments= AddSupplierFragmentArgs.fromBundle(arguments!!)
        val supplier = arguments.supplier
        val action = arguments.update

        if (action.equals(getString(R.string.edit))){
            addsupplierBinding.supplier = supplier
            addsupplierBinding.saveSupplierButton.visibility = View.GONE
            addsupplierBinding.supplierUpdateButton.visibility = View.VISIBLE
            addsupplierBinding.supplierPrintButton.visibility = View.VISIBLE
            addsupplierBinding.supplierDeleteButton.visibility = View.VISIBLE
        }

        //Adding Text Watcher to name
        addsupplierBinding.supplierNameTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierNameTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierNameTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to the street address
        addsupplierBinding.supplierAddressTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierAddressTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierAddressTextInputLayout.isErrorEnabled = false
            }
        })

        //Adding Text watcher to post code
        addsupplierBinding.supplierPostCodeTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierPostCodeTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierPostCodeTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to telephone no.
        addsupplierBinding.supplierTelephoneTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierTelephoneTextInputLayout.isErrorEnabled = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierTelephoneTextInputLayout.isErrorEnabled = true
            }
        })

        //Add Text Watcher to email
        addsupplierBinding.supplierEmailTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierEmailTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierEmailTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to web address
        addsupplierBinding.supplierWebTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addsupplierBinding.supplierWebTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addsupplierBinding.supplierWebTextInputLayout.isErrorEnabled = false
            }
        })

        //Adding Text Watcher to street
        addsupplierBinding.saveSupplierButton.setOnClickListener {

            val name = addsupplierBinding.supplierNameTextInput.text.toString()
            val street = addsupplierBinding.supplierAddressTextInput.text.toString()
            val country = addsupplierBinding.supplierCountryTv.text.toString()
            val postCode = addsupplierBinding.supplierPostCodeTextInput.text.toString()
            val telephone = addsupplierBinding.supplierTelephoneTextInput.text.toString()
            val email = addsupplierBinding.supplierEmailTextInput.text.toString()
            val web = addsupplierBinding.supplierWebTextInput.text.toString()

            when{
                name.isEmpty()->{
                    addsupplierBinding.supplierNameTextInputLayout.error = getString(R.string.add_name)
                }
                street.isEmpty()->{
                    addsupplierBinding.supplierAddressTextInputLayout.error = getString(R.string.add_address)
                }
                postCode.isEmpty()->{
                    addsupplierBinding.supplierPostCodeTextInputLayout.error = getString(R.string.enter_post_code)
                }
                telephone.isEmpty()->{
                    addsupplierBinding.supplierTelephoneTextInputLayout.error = getString(R.string.add_phone_no)
                }
                email.isEmpty()->{
                    addsupplierBinding.supplierEmailTextInputLayout.error = getString(R.string.add_email)
                }
                web.isEmpty()->{
                    addsupplierBinding.supplierWebTextInputLayout.error = getString(R.string.add_web_address)
                }
                else ->{
                    addSupplier(name, street, country, postCode, telephone, email, web)
                }
            }

        }

        //Set on click listener to update button
        addsupplierBinding.supplierUpdateButton.setOnClickListener {
            val name = addsupplierBinding.supplierNameTextInput.text.toString()
            val street = addsupplierBinding.supplierAddressTextInput.text.toString()
            val country = addsupplierBinding.supplierCountryTv.text.toString()
            val postCode = addsupplierBinding.supplierPostCodeTextInput.text.toString()
            val telephone = addsupplierBinding.supplierTelephoneTextInput.text.toString()
            val email = addsupplierBinding.supplierEmailTextInput.text.toString()
            val web = addsupplierBinding.supplierWebTextInput.text.toString()

            when{
                name.isEmpty()->{
                    addsupplierBinding.supplierNameTextInputLayout.error = getString(R.string.add_name)
                }
                street.isEmpty()->{
                    addsupplierBinding.supplierAddressTextInputLayout.error = getString(R.string.add_address)
                }
                postCode.isEmpty()->{
                    addsupplierBinding.supplierPostCodeTextInputLayout.error = getString(R.string.enter_post_code)
                }
                telephone.isEmpty()->{
                    addsupplierBinding.supplierTelephoneTextInputLayout.error = getString(R.string.add_phone_no)
                }
                email.isEmpty()->{
                    addsupplierBinding.supplierEmailTextInputLayout.error = getString(R.string.add_email)
                }
                web.isEmpty()->{
                    addsupplierBinding.supplierWebTextInputLayout.error = getString(R.string.add_web_address)
                }
                else ->{
                    updateSupplier(supplier.supid!!,name, street, country, postCode, telephone, email, web)
                }
            }
        }
        return addsupplierBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddSupplierViewModel::class.java)
        // TODO: Use the ViewModel
    }

    //Add Supplier
    private fun addSupplier(
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        viewModel.addSupplier(supplierName, streetAdd, coutry, postCode, telephone, email, web)
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(AddSupplierFragmentDirections.actionAddSupplierFragmentToSupplierFragment())
                }
            })
    }

    //Update supplier
    private fun updateSupplier(
        supplierId:String,
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ){
        viewModel.updateSupplier(supplierId, supplierName, streetAdd, coutry, postCode, telephone, email, web).observe(viewLifecycleOwner,
            Observer {
                it?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(AddSupplierFragmentDirections.actionAddSupplierFragmentToSupplierFragment())
                }
            })
    }
    
}
