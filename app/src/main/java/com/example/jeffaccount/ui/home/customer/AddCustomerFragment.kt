package com.example.jeffaccount.ui.home.customer

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddCustomerFragmentBinding
import com.example.jeffaccount.model.Post
import timber.log.Timber
import java.lang.NumberFormatException

private var loadingDialog: androidx.appcompat.app.AlertDialog? = null

class AddCustomerFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddCustomerFragment()
    }

    private lateinit var viewModel: CustomerViewModel
    private lateinit var customer:Post
    private lateinit var action:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val addCustomerBinding = AddCustomerFragmentBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
//        Getting arguments
        val arguments = AddCustomerFragmentArgs.fromBundle(arguments!!)
        customer = arguments.post
        action = arguments.add

        if (action == getString(R.string.edit)) {
            addCustomerBinding.customer = customer
            addCustomerBinding.updateCustomerButton.visibility = View.VISIBLE
            addCustomerBinding.customerSaveButton.visibility = View.GONE

        }

        //Set on click listener to save button
        addCustomerBinding.customerSaveButton.setOnClickListener {

            val name = addCustomerBinding.customerNameTextInput.text.toString()
            val street = addCustomerBinding.customerAddressTextInput.text.toString()
            val country = addCustomerBinding.customerCountryTv.text.toString()
            val postCode = addCustomerBinding.customerPostCodeTextInput.text.toString()
            val phone = addCustomerBinding.customerTelephoneTextInput.text.toString()
            val email = addCustomerBinding.customerEmailTextInput.text.toString()
            val web = addCustomerBinding.customerWebTextInput.text.toString()

            when {
                name.isEmpty() -> {
                    addCustomerBinding.customerNameTextInputLayout.error =
                        getString(R.string.add_name)
                }
                street.isEmpty() -> {
                    addCustomerBinding.customerAddressTextInputLayout.error =
                        getString(R.string.add_address)
                }
                postCode.isEmpty() -> {
                    addCustomerBinding.customerPostCodeTextInputLayout.error =
                        getString(R.string.enter_post_code)
                }
                phone.isEmpty() -> {
                    addCustomerBinding.customerTelephoneTextInputLayout.error =
                        getString(R.string.add_phone_no)
                }
                email.isEmpty() -> {
                    addCustomerBinding.customerEmailTextInputLayout.error =
                        getString(R.string.add_email)
                }
                web.isEmpty() -> {
                    addCustomerBinding.customerWebTextInputLayout.error =
                        getString(R.string.add_web_address)
                }
                else -> {
                    loadingDialog = createLoadingDialog()
                    loadingDialog?.show()
                    addCustomer(name, street, country, postCode, phone, email, web)
                }
            }
        }

        //Set OnClick listener to Update Button
        addCustomerBinding.updateCustomerButton.setOnClickListener {

            val name = addCustomerBinding.customerNameTextInput.text.toString()
            val street = addCustomerBinding.customerAddressTextInput.text.toString()
            val country = addCustomerBinding.customerCountryTv.text.toString()
            val postCode = addCustomerBinding.customerPostCodeTextInput.text.toString()
            val phone = addCustomerBinding.customerTelephoneTextInput.text.toString()
            val email = addCustomerBinding.customerEmailTextInput.text.toString()
            val web = addCustomerBinding.customerWebTextInput.text.toString()

            when {
                name.isEmpty() -> {
                    addCustomerBinding.customerNameTextInputLayout.error =
                        getString(R.string.add_name)
                }
                street.isEmpty() -> {
                    addCustomerBinding.customerAddressTextInputLayout.error =
                        getString(R.string.add_address)
                }
                postCode.isEmpty() -> {
                    addCustomerBinding.customerPostCodeTextInputLayout.error =
                        getString(R.string.enter_post_code)
                }
                phone.isEmpty() -> {
                    addCustomerBinding.customerTelephoneTextInputLayout.error =
                        getString(R.string.add_phone_no)
                }
                email.isEmpty() -> {
                    addCustomerBinding.customerEmailTextInputLayout.error =
                        getString(R.string.add_email)
                }
                web.isEmpty() -> {
                    addCustomerBinding.customerWebTextInputLayout.error =
                        getString(R.string.add_web_address)
                }
                else -> {
                    loadingDialog = createLoadingDialog()
                    loadingDialog?.show()
                    updateCustomer(
                        customer.custid!!,
                        name,
                        street,
                        country,
                        postCode,
                        phone,
                        email,
                        web
                    )
                }
            }
        }

        //Adding text watcher to customer name
        addCustomerBinding.customerNameTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerNameTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerNameTextInputLayout.isErrorEnabled = false
            }

        })

        //Adding text watcher to street address
        addCustomerBinding.customerAddressTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerAddressTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerAddressTextInputLayout.isErrorEnabled = false
            }

        })

        //Add Text watcher to post code
        addCustomerBinding.customerPostCodeTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerPostCodeTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerPostCodeTextInputLayout.isErrorEnabled = false
            }

        })

        //Add Text watcher to telephone no
        addCustomerBinding.customerTelephoneTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerTelephoneTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerTelephoneTextInputLayout.isErrorEnabled = false
            }

        })

        //Add Text Watcher to email input
        addCustomerBinding.customerEmailTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                addCustomerBinding.customerEmailTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerEmailTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to web
        addCustomerBinding.customerWebTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCustomerBinding.customerWebTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCustomerBinding.customerWebTextInputLayout.isErrorEnabled = false
            }
        })

        return addCustomerBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (action.equals(getString(R.string.edit))) {
            inflater.inflate(R.menu.main_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.delete_item){
            val layout = LayoutInflater.from(context).inflate(R.layout.delete_confirmation,null)
            val builder = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
            builder?.setCancelable(false)
            builder?.setView(layout)
            val dialog = builder?.create()
            dialog?.show()

            val delButton = layout.findViewById<Button>(R.id.delete_button)
            val canButton:Button = layout.findViewById(R.id.cancel_del_button)

            delButton.setOnClickListener {
                loadingDialog = createLoadingDialog()
                loadingDialog?.show()
                deleteUser(customer.custid!!)
                dialog?.dismiss()
            }

            canButton.setOnClickListener {
                dialog?.dismiss()
            }

        }else if (id == R.id.convert_pdf_item){
            Timber.e("Pdf click")
            this.findNavController().navigate(AddCustomerFragmentDirections.actionAddCustomerToCustomerPdfFragment(customer))
        }
        return true
    }

    //Add Customer
    private fun addCustomer(
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        viewModel.addCustomer(customerName, streetAdd, coutry, postCode, telephone, email, web)
            .observe(viewLifecycleOwner,
                Observer {
                    it?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        loadingDialog?.dismiss()
                        findNavController().navigate(AddCustomerFragmentDirections.actionAddCustomerToCustomerFragment())
                    }
                })
    }

    //Update Customer
    private fun updateCustomer(
        customerId: String,
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        viewModel.updateCustomer(
            customerId, customerName,
            streetAdd, coutry, postCode, telephone, email, web
        ).observe(viewLifecycleOwner,
            Observer {
                it?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    loadingDialog?.dismiss()
                    findNavController().navigate(AddCustomerFragmentDirections.actionAddCustomerToCustomerFragment())
                }
            })
    }

    //Delete User
    private fun deleteUser(customerId: String){
        viewModel.deleteUser(customerId).observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                loadingDialog?.dismiss()
                findNavController().navigate(AddCustomerFragmentDirections.actionAddCustomerToCustomerFragment())
            }?:let {
                loadingDialog?.dismiss()
            }
        })
    }

    //Create a loading Dialog
    private fun createLoadingDialog(): androidx.appcompat.app.AlertDialog? {
        val layout = LayoutInflater.from(context).inflate(R.layout.loading_layout, null)
        val builder = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
        builder?.setCancelable(false)
        builder?.setView(layout)
        return builder?.create()
    }
}
