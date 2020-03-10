package com.example.jeffaccount.ui.home.quotation

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
import com.example.jeffaccount.databinding.AddQuotationFragmentBinding
import com.example.jeffaccount.databinding.FragmentQuotationBinding
import timber.log.Timber


class AddQuotationFragment : Fragment() {

    companion object {
        fun newInstance() =
            AddQuotationFragment()
    }

    private lateinit var viewModel: AddQuotationViewModel
    private lateinit var quotationBinding: AddQuotationFragmentBinding
    private var vat: Double? = null
    private var unitAmount: Double? = null
    private var advanceAmount: Double? = null
    private var discountAmount: Double? = null
    private var totalAmount: Double? = null
    private var qty = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        quotationBinding = AddQuotationFragmentBinding.inflate(inflater, container, false)

        quotationBinding.saveQuotationButton.setOnClickListener {
            findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
        }

        //Set on click listener to add button
        quotationBinding.quotationPlusButton.setOnClickListener {

            if (qty >= 0) {
                qty++
                viewModel.addQuantity(qty)
            }
        }

        //Set on click listener to minus button
        quotationBinding.quotationMinusButton.setOnClickListener {
            if (qty > 0)
                qty--
            viewModel.removeQuantity(qty)
        }

        //Set on click listener to the quotation save button
        quotationBinding.saveQuotationButton.setOnClickListener {
            val jobNo = quotationBinding.quotationJobTextInput.text.toString()
            val quotationNo = quotationBinding.quotationQuotationoTextInput.text.toString()

            if (quotationBinding.quotationVatTextInput.text.toString().isNotEmpty()) {
                vat = quotationBinding.quotationVatTextInput.toString().toDouble()
            }
            val date = quotationBinding.quotationDateTextInput.text.toString()
            val customerName = quotationBinding.quotationCustomerNameTextInput.text.toString()
            val comment = quotationBinding.quotationCommentTextInput.text.toString()
            val itemDes = quotationBinding.quotationItemdesTextInput.text.toString()
            val paymentMethod = quotationBinding.quotationPayementMethodTextInput.text.toString()
            if (quotationBinding.quotationUnitAmountTextInput.text.toString().isNotEmpty()) {
                unitAmount =
                    quotationBinding.quotationUnitAmountTextInput.text.toString().toDouble()
            }
            if (quotationBinding.quotationAdvanceAmountTextInput.text.toString().isNotEmpty()) {
                advanceAmount =
                    quotationBinding.quotationAdvanceAmountTextInput.text.toString().toDouble()
            }

            if (quotationBinding.quotationDiscountAmountTextInput.text.toString().isNotEmpty()) {
                discountAmount =
                    quotationBinding.quotationDiscountAmountTextInput.text.toString().toDouble()
            }

            if (quotationBinding.quotationTotalAmountTextinput.text.toString().isNotEmpty()) {
                totalAmount =
                    quotationBinding.quotationTotalAmountTextinput.text.toString().toDouble()
            }

            when {
                jobNo.isEmpty() -> {
                    quotationBinding.quotationJobTextInputLayout.error =
                        getString(R.string.enter_job_no)
                }

                quotationNo.isEmpty() -> {
                    quotationBinding.quotationQuotationoTextInputLayout.error =
                        getString(R.string.enter_quotation_no)
                }
                quotationBinding.quotationVatTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationVatTextInputLayout.error =
                        getString(R.string.enter_vat)
                }
                date.isEmpty() -> {
                    quotationBinding.quotationDateTextInputLayout.error =
                        getString(R.string.enter_date)
                }
                customerName.isEmpty() -> {
                    quotationBinding.quotationCustomerNameTextInputLayout.error =
                        getString(R.string.enter_customer_name)
                }
                comment.isEmpty() -> {
                    quotationBinding.quotationCommentTextInputLayout.error =
                        getString(R.string.enter_comment)
                }

                itemDes.isEmpty() -> {
                    quotationBinding.quotationItemDesTextInputLayout.error =
                        getString(R.string.enter_item_des)
                }
                paymentMethod.isEmpty() -> {
                    quotationBinding.quotationPayementMethodTextInputLayout.error =
                        "Select a payment method"
                }
                qty == 0 -> {
                    Toast.makeText(context, "Quantity Cannot be 0", Toast.LENGTH_SHORT).show()
                }
                quotationBinding.quotationUnitAmountTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationUnitAmountTextInputLayout.error = "Enter Unit Amount"
                }
                quotationBinding.quotationAdvanceAmountTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationAdvanceAmountTextInputLayout.error =
                        "Enter Advance Amount"
                }
                quotationBinding.quotationDiscountAmountTextInput.text.toString().isEmpty() -> {
                    quotationBinding.quotationDiscountAmountTextInputLayout.error =
                        "Enter Discount Amount"
                }
                quotationBinding.quotationTotalAmountTextinput.text.toString().isEmpty() -> {
                    quotationBinding.quotationTotalAmountTextinputlayout.error =
                        "Enter Total Amount"
                }
                else ->{
                    viewModel.addQuotaiton(jobNo,quotationNo,vat!!,date,customerName,comment,itemDes,paymentMethod
                    ,qty,unitAmount!!,advanceAmount!!,discountAmount!!,totalAmount!!).observe(viewLifecycleOwner,
                        Observer {
                            it?.let {
                                Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                                findNavController().navigate(AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment())
                            }
                        })
                }
            }
        }

        //Add Text Watcher to job no
        quotationBinding.quotationJobTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationJobTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationJobTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to quotation no
        quotationBinding.quotationQuotationoTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationQuotationoTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationQuotationoTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to vat
        quotationBinding.quotationVatTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationVatTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationVatTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to name
        quotationBinding.quotationCustomerNameTextInput.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationCustomerNameTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationCustomerNameTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to comment
        quotationBinding.quotationCommentTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationCommentTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationCommentTextInputLayout.isErrorEnabled = false
            }
        })

        //Add text watcher to item description
        quotationBinding.quotationItemdesTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationItemDesTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationItemDesTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to unit amount
        quotationBinding.quotationUnitAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationUnitAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationUnitAmountTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text watcher to advance amount
        quotationBinding.quotationAdvanceAmountTextInput.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationAdvanceAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationAdvanceAmountTextInputLayout.isErrorEnabled = false
            }
        })

        //Add Text Watcher to discount amount
        quotationBinding.quotationDiscountAmountTextInput.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationDiscountAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationDiscountAmountTextInputLayout.isErrorEnabled = false
            }
        })

        //Add TExt watcher to total amount
        quotationBinding.quotationTotalAmountTextinput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quotationBinding.quotationTotalAmountTextinputlayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                quotationBinding.quotationTotalAmountTextinputlayout.isErrorEnabled = false
            }
        })
        return quotationBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddQuotationViewModel::class.java)
        // TODO: Use the ViewModel

        viewModel.quotationQuantityValue.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("${it.toString()}")
                quotationBinding.quotationQtyTv.text = it.toString()
            }
        })
    }

}
