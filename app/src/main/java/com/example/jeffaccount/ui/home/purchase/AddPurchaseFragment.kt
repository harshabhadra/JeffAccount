package com.example.jeffaccount.ui.home.purchase

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddPurchaseFragmentBinding


class AddPurchaseFragment : Fragment() {

    private lateinit var purchaseBinding:AddPurchaseFragmentBinding
    companion object {
        fun newInstance() =
            AddPurchaseFragment()
    }

    private lateinit var viewModel: AddPurchaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        purchaseBinding = AddPurchaseFragmentBinding.inflate(inflater,container,false)

        purchaseBinding.purchaseSaveButton.setOnClickListener {
            val jobNo = purchaseBinding.purchaseJobnoTextInput.text.toString()
            val quotationNo = purchaseBinding.purchaseQuotationTextInput.text.toString()
            val vat = purchaseBinding.purchaseVatTextInput.text.toString()
            if (vat.isNotEmpty()){
                vat.toDouble()
            }
            val date = purchaseBinding.purchaseDateTextInput.text.toString()
            val supplierName = purchaseBinding.purchaseSupplierTextInput.text.toString()
            val comment = purchaseBinding.purchaseCommentTextInput.text.toString()
            val itemDes = purchaseBinding.purchaseItemdesTextInput.text.toString()
            val advanceAmount = purchaseBinding.purchaseAdvanceAmountTextInput.text.toString()
            if (advanceAmount.isNotEmpty()){
                advanceAmount.toDouble()
            }
            val unitAmount = purchaseBinding.purchaseUnitAmountTextInput.text.toString()
            if(unitAmount.isNotEmpty()){
                unitAmount.toDouble()
            }
            val discountAmount = purchaseBinding.purchaseDiscountAmountTextInput.text.toString()
            if (discountAmount.isNotEmpty()){
                discountAmount.toDouble()
            }
            val totalAmount = purchaseBinding.purchaseTotalAmountTextinput.text.toString()
            if (totalAmount.isNotEmpty()){
                totalAmount.toDouble()
            }

            when{
                jobNo.isEmpty()->purchaseBinding.purchaseJobnoTextInputLayout.error = getString(R.string.enter_job_no)
                quotationNo.isEmpty()->purchaseBinding.purchaseQuotationTextInputLayout.error = getString(R.string.enter_quotation_no)
                vat.isEmpty()->purchaseBinding.purchaseVatTextInputLayout.error = getString(R.string.enter_vat)
                date.isEmpty()->purchaseBinding.purchaseDateTextInputLayout.error = getString(R.string.enter_date)
                supplierName.isEmpty()->purchaseBinding.purchaseSupplierTextInputLayout.error = getString(R.string.enter_supplier_name)
                comment.isEmpty()->purchaseBinding.purchaseCommentTextInputLayout.error = getString(R.string.enter_comment)
                itemDes.isEmpty()->purchaseBinding.purchaseItemdesTextInputLayout.error = getString(R.string.enter_item_des)
                
            }
        }

        //Adding Text watcher to all input fields
        purchaseBinding.purchaseJobnoTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseJobnoTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseJobnoTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseQuotationTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseQuotationTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseQuotationTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseVatTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseVatTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseVatTextInputLayout.isErrorEnabled =false
            }
        })

        purchaseBinding.purchaseSupplierTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                purchaseBinding.purchaseSupplierTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseSupplierTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseCommentTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseCommentTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseCommentTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseItemdesTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseItemdesTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseItemdesTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchasePaymentMethodTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchasePaymentMethodTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchasePaymentMethodTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseUnitAmountTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseUnitAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseUnitAmountTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseAdvanceAmountTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            purchaseBinding.purchaseAdvanceAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseAdvanceAmountTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseDiscountAmountTextInput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseDiscountAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseDiscountAmountTextInputLayout.isErrorEnabled = false
            }
        })

        purchaseBinding.purchaseTotalAmountTextinput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                purchaseBinding.purchaseTotalAmountTextinputlayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                purchaseBinding.purchaseTotalAmountTextinputlayout.isErrorEnabled = false
            }
        })



        return purchaseBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
