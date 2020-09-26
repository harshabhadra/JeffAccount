package com.example.jeffaccount.ui.home.purchase

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.model.CompanyDetails
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.network.SupList
import com.example.jeffaccount.ui.MainActivity
import com.example.jeffaccount.ui.home.quotation.ItemAdapter
import com.example.jeffaccount.ui.home.quotation.OnAddedItemClickListener
import com.example.jeffaccount.ui.home.quotation.OnStartDragListener
import com.example.jeffaccount.utils.ItemMoveCallbackListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddSupplierBottomSheetFragment(
    val supList: SupList?,
    val onSaveClickListener: onSaveSupplierListener,
    val position: Int?
) :
    BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener, OnAddedItemClickListener,
    OnStartDragListener, AdapterView.OnItemSelectedListener {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var companyDetails: CompanyDetails
    private lateinit var comid: String
    private lateinit var itemAdapter: ItemAdapter
    private var itemList: MutableList<Item> = mutableListOf()
    private lateinit var viewModel: AddPurchaseViewModel
    private var qty = 0
    private lateinit var addItemTv: TextView
    private lateinit var dateEd: EditText
    private lateinit var touchHelper: ItemTouchHelper
    private lateinit var unit:String
    private var unitPosition:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_supplier_bottom_sheet, container, false)

        viewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)
        addItemTv = view.findViewById(R.id.supplier_add_item_tv_bottom_sheet)
        //Create add item dialog on click on add item tv
        addItemTv.setOnClickListener {
            createItemDialog(null, null)
        }

        dateEd = view.findViewById(R.id.supplier_date_editText)
        val saveButton: Button = view.findViewById(R.id.save_supplier_button_sheet_button)
        val paymentMethodEd: EditText = view.findViewById(R.id.supplier_payment_method)
        val vatEd: EditText = view.findViewById(R.id.supplier_vat)


        val itemRecyclerView: RecyclerView = view.findViewById(R.id.bottom_sheet_item_list_recycler)
        itemAdapter = ItemAdapter(this, this)
        val callback: ItemTouchHelper.Callback = ItemMoveCallbackListener(itemAdapter)

        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(itemRecyclerView)
        itemRecyclerView.adapter = itemAdapter

        supList?.let {
            dateEd.setText(supList.supDate)
            paymentMethodEd.setText(supList.paymentMethod)
            vatEd.setText(supList.vat.toString())
            supList.itemList?.let {
                itemList.addAll(supList.itemList!!)
                itemAdapter.submitList(supList.itemList!!)
            }
        }

        //Create a date picker on date ed click
        dateEd.setOnClickListener {
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
        //Set on Click listener to save button
        saveButton.setOnClickListener {
            val date = dateEd.text.toString()
            val paymentMethod = paymentMethodEd.text.toString()
            val vat = vatEd.text.toString()
            if (date.isEmpty()) {
                dateEd.error = context!!.getString(R.string.enter_date)
            } else if (paymentMethod.isEmpty()) {
                paymentMethodEd.error = context!!.getString(R.string.enter_payment_method)
            } else if (vat.isEmpty()) {
                vatEd.error = context!!.getString(R.string.enter_vat)
            } else {
                supList?.let {
                    val supplier = SupList(
                        supList.supName,
                        date,
                        supList.county,
                        paymentMethod,
                        vat.toDouble(),
                        itemList
                    )
                    onSaveClickListener.onSaveSupplier(supplier,position)
                }
                dismiss()
            }
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        viewModel = ViewModelProvider(this).get(AddPurchaseViewModel::class.java)

        val activity = activity as MainActivity
        companyDetails = activity.companyDetails
        comid = companyDetails.comid

        val view = View.inflate(context, R.layout.fragment_add_supplier_bottom_sheet, null)
        val rootLayout: LinearLayout = view.findViewById(R.id.add_supplier_root)
        val params: LinearLayout.LayoutParams = rootLayout.layoutParams as LinearLayout.LayoutParams
        params.height = getScreenHeight()
        rootLayout.layoutParams = params

        bottomSheetDialog.setContentView(view)
        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    //Get Screen Height
    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }


    private fun createItemDialog(item: Item?, position: Int?) {

        val layout = LayoutInflater.from(context).inflate(R.layout.fragment_add_item, null)
        val builder = context.let { androidx.appcompat.app.AlertDialog.Builder(it!!) }
        builder.setView(layout)
        val dialog = builder.create()
        dialog.show()

        val itemDestv: TextView = layout.findViewById(R.id.item_des_editText)
        val qtytv: TextView = layout.findViewById(R.id.item_qty_editText)
        val unitAmountTv: TextView = layout.findViewById(R.id.item_unitamount_editText)
        val discountAmountTv: TextView = layout.findViewById(R.id.discount_amount_editText)
        val totalAmountTv: TextView = layout.findViewById(R.id.total_editText)

        val addButton: Button = layout.findViewById(R.id.add_item_button)
        val negButton: ImageButton = layout.findViewById(R.id.neg_qty_button)
        val posButton: ImageButton = layout.findViewById(R.id.pos_qty_button)
        val unitEditText:EditText = layout.findViewById(R.id.item_unit_editText)

        var amount = 0.0
        var discountAmount = 0.0

        unitAmountTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isNotEmpty()) {
                        amount = s.toString().toDouble()
                        if (qtytv.text.isNotEmpty()) {
                            viewModel.calculateAmount(qty, amount, discountAmount)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        discountAmountTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (s.isNotEmpty()) {
                        discountAmount = s.toString().toDouble()
                        if (qtytv.text.isNotEmpty() && unitAmountTv.text.isNotEmpty()) {
                            viewModel.calculateAmount(qty, amount, discountAmount)
                        }
                    }
                }
            }
        })

        qtytv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (s.isNotEmpty()) {
                        qty = s.toString().toInt()
                        if (unitAmountTv.text.isNotEmpty()) {
                            viewModel.calculateAmount(qty, amount, discountAmount)
                        }
                    }
                }
            }
        })
        viewModel.totalAmount.observe(viewLifecycleOwner, Observer {
            it?.let {
                totalAmountTv.text = it.toString()
            }
        })
        item?.let {
            qty = item.qty!!
            itemDestv.text = item.itemDes
            qtytv.text = item.qty.toString()
            unitAmountTv.text = item.unitAmount.toString()
            discountAmountTv.text = item.discountAmount.toString()
            discountAmount = item.discountAmount!!
            totalAmountTv.text = item.totalAmount.toString()
            addButton.text = context!!.getString(R.string.update)
            unitEditText.setText(item.unit)
        }
        var qty = 0
        posButton.setOnClickListener {
            if (qty >= 0) {
                qty++
                qtytv.setText(qty.toString())
            }
        }
        negButton.setOnClickListener {
            if (qty > 0) {
                qty--
                qtytv.setText(qty.toString())
            }
        }
        if (item == null) {
            addButton.setOnClickListener {
                viewModel.setDefaultAmount()
                val itemDes = itemDestv.text.toString()
                val qty = qtytv.text.toString()
                val unitAmount = unitAmountTv.text.toString()
                val totalAmount = totalAmountTv.text.toString()
                val unit = unitEditText.text.toString()

                when {
                    itemDes.isEmpty() -> Toast.makeText(
                        context,
                        "Add item description",
                        Toast.LENGTH_SHORT
                    ).show()
                    qty.isEmpty() -> Toast.makeText(context, "Add Quantity", Toast.LENGTH_SHORT)
                        .show()
                    unitAmount.isEmpty() -> Toast.makeText(
                        context,
                        "Enter Unit Amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    totalAmount.isEmpty() -> Toast.makeText(
                        context,
                        "Enter Total Amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    unit.isEmpty()->Toast.makeText(requireContext(),"Enter Unit", Toast.LENGTH_SHORT).show()
                    else -> {
                        val item = Item(
                            itemDes,
                            qty.toInt(),
                            unit,
                            unitAmount.toDouble(),
                            discountAmount.toDouble(),
                            totalAmount.toDouble()
                        )
                        itemList.add(item)
                        Timber.e("Item list size in bottom sheet ${itemList.size}")
                        itemAdapter.submitList(itemList)
                        itemAdapter.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                }
            }
        } else {
            addButton.setOnClickListener {
                viewModel.setDefaultAmount()
                val itemDes = itemDestv.text.toString()
                val qty = qtytv.text.toString()
                val unitAmount = unitAmountTv.text.toString()
                val totalAmount = totalAmountTv.text.toString()
                val unit = unitEditText.text.toString()

                when {
                    itemDes.isEmpty() -> Toast.makeText(
                        context,
                        "Add item description",
                        Toast.LENGTH_SHORT
                    ).show()
                    qty.isEmpty() -> Toast.makeText(context, "Add Quantity", Toast.LENGTH_SHORT)
                        .show()
                    unitAmount.isEmpty() -> Toast.makeText(
                        context,
                        "Enter Unit Amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    totalAmount.isEmpty() -> Toast.makeText(
                        context,
                        "Enter Total Amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    unit.isEmpty()->Toast.makeText(requireContext(),"Enter Unit", Toast.LENGTH_SHORT).show()
                    else -> {
                        val uItem = Item(
                            itemDes,
                            qty.toInt(),
                            unit,
                            unitAmount.toDouble(),
                            discountAmount.toDouble(),
                            totalAmount.toDouble()
                        )
                        itemList.removeAt(position!!)
                        itemList.add(position,uItem)
                        itemAdapter.submitList(itemList)
                        itemAdapter.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    private fun createChoiceDialog(item: Item, position: Int) {
        val layout = LayoutInflater.from(context).inflate(R.layout.choose_layout, null)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(layout)
        val dialog = builder.create()
        dialog.show()
        val editButton: MaterialButton = layout.findViewById(R.id.choice_edit_button)
        val deleteButton: MaterialButton = layout.findViewById(R.id.choice_delete_button)
        deleteButton.setOnClickListener {
            itemList.remove(item)
            itemAdapter.submitList(itemList)
            itemAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        editButton.setOnClickListener {
            createItemDialog(item, position)
            dialog.dismiss()
        }

    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = "$dayOfMonth/${monthOfYear + 1}/$year"
        dateEd.setText(date)
    }

    override fun itemClick(item: Item, position: Int) {
        createChoiceDialog(item, position)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedText = parent?.getChildAt(0) as TextView
        selectedText.setTextColor(Color.WHITE)
        unit = parent.selectedItem as String
        unitPosition = position
    }
}

interface onSaveSupplierListener {
    fun onSaveSupplier(
        supList: SupList, position: Int?
    )
}
