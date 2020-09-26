package com.example.jeffaccount.ui.home.timeSheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.AddWorkerLayoutBinding
import com.example.jeffaccount.network.WorkerList
import com.example.jeffaccount.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddWorkerBottomSheetFragment(
    private val worker: WorkerList?,
    private val workerSaveClickListener: OnWorkerSaveClickListener
) : BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var addWorkerBinding: AddWorkerLayoutBinding
    private lateinit var addWorkerViewModel: AddWorkerViewModel
    private lateinit var comId: String
    private var hours: Int = 0
    private var advanceAmount: Double = 0.0
    private var amount: Double = 0.0
    private var vat: Double = 0.0
    private var totalAmount = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Initializing DataBinding
        addWorkerBinding = AddWorkerLayoutBinding.inflate(inflater, container, false)

        //Initializing ViewModel Class
        addWorkerViewModel = ViewModelProvider(this).get(AddWorkerViewModel::class.java)

        worker?.let {
            totalAmount = worker.totalAmount!!
            Timber.e("Total amount: $totalAmount")
            addWorkerBinding.worker = worker
            worker.advanceAmount?.let {
                advanceAmount = worker.advanceAmount!!
            }
            worker.vat?.let {
                vat = worker.vat!!
            }
        }

        val activity = activity as MainActivity
        activity.setToolbarText("Add Worker")

        comId = activity.companyDetails.comid

        //Add TextWatcher to hours, advance amount, amount and Total Amount TextInputLayout
        addWorkerBinding.workerHourEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (!s.isNullOrEmpty()) {
                        hours = s.toString().toInt()
                        addWorkerViewModel.calculateTotalAmount(hours, amount, advanceAmount, vat)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        addWorkerBinding.workerAdvanceAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                s?.let {
                    if (!s.isNullOrEmpty()) {
                        advanceAmount = s.toString().toDouble()
                        addWorkerViewModel.calculateTotalAmount(hours, amount, advanceAmount, vat)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        addWorkerBinding.workerAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                s?.let {
                    if (!s.isNullOrEmpty()) {
                        amount = s.toString().toDouble()
                        addWorkerViewModel.calculateTotalAmount(hours, amount, advanceAmount, vat)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        addWorkerBinding.workerTotalAmountTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addWorkerBinding.workerTotalAmountTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addWorkerBinding.workerTotalAmountTextInputLayout.isErrorEnabled = false
            }
        })

        addWorkerBinding.workerVatTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                s?.let {
                    if (!s.isNullOrEmpty()) {
                        vat = s.toString().toDouble()
                        addWorkerViewModel.calculateTotalAmount(hours, amount, advanceAmount, vat)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        //Set on click listener to hour plus button
        addWorkerBinding.hourPlusButton.setOnClickListener {
            hours++
            addWorkerViewModel.setHour(hours)
        }

        addWorkerBinding.hourMinusButton.setOnClickListener {
            if (hours>0){
                hours--
                addWorkerViewModel.setHour(hours)
            }
        }

        addWorkerViewModel.hours.observe(viewLifecycleOwner, Observer {
            it?.let {
                addWorkerBinding.workerHourEditText.setText(it.toString())
            }
        })

        //Observe total amount
        addWorkerViewModel.totalAmount.observe(viewLifecycleOwner, Observer {
            it?.let {
                totalAmount = it
                addWorkerBinding.workerTotalAmountTextInput.setText(it.toString())
            }
        })

        //Set on click listener to save button
        addWorkerBinding.saveWorkerButton.setOnClickListener {
            val name = addWorkerBinding.workerNameTextinput.text.toString()
            val telephone = addWorkerBinding.workerTelephoneTextInput.text.toString()
            val hourt = addWorkerBinding.workerHourEditText.text.toString()
            val advanceAmountT = addWorkerBinding.workerAdvanceAmountTextInput.text.toString()
            val amountT = addWorkerBinding.workerAmountTextInput.text.toString()
            val vatT = addWorkerBinding.workerVatTextInput.text.toString()
            val totalAmountT = addWorkerBinding.workerTotalAmountTextInput.text.toString()
            val paymentMethod = addWorkerBinding.workerPaymentmethodTextInput.text.toString()
            val date = addWorkerBinding.workerDateTextInput.text.toString()
            val comment = addWorkerBinding.workerCommentTextInput.text.toString()

            when {
                name.isEmpty() -> {
                    addWorkerBinding.workerNameTextinputlayout.error =
                        getString(R.string.enter_name)
                }
                totalAmountT.isEmpty() -> {
                    addWorkerBinding.workerTotalAmountTextInputLayout.error =
                        getString(R.string.enter_total_amount)
                }
                hourt.isEmpty() -> {
                    addWorkerBinding.workerHourEditText.error = "Add hour"
                }
                amountT.isEmpty() -> {
                    addWorkerBinding.workerAmountTextInputLayout.error =
                        getString(R.string.enter_amount)
                }
                else -> {

                    val nWorker = WorkerList(
                        name,
                        hourt.toInt(),
                        amountT.toDouble(),
                        advanceAmount,
                        totalAmountT.toDouble(),
                        date,
                        telephone,
                        paymentMethod,
                        comment,
                        vat,
                        null
                    )

                    workerSaveClickListener.onWorkerSave(nWorker)
                    dismiss()
                }
            }
        }

        //Set on click listener to date textinputlayout
        addWorkerBinding.workerDateTextInput.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd =
                DatePickerDialog.newInstance(
                    this,
                    now[Calendar.YEAR],  // Initial year selection
                    now[Calendar.MONTH],  // Initial month selection
                    now[Calendar.DAY_OF_MONTH] // Inital day selection
                )
            dpd.show(activity?.supportFragmentManager!!, "Datepickerdialog")
        }
        return addWorkerBinding.root
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = "$dayOfMonth/${monthOfYear+1}/$year"
        addWorkerBinding.workerDateTextInput.setText(date)
    }
}

interface OnWorkerSaveClickListener {
    fun onWorkerSave(worker: WorkerList)
}
