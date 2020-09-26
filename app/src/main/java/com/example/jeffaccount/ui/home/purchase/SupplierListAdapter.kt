package com.example.jeffaccount.ui.home.purchase

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.network.SupList
import com.example.jeffaccount.ui.home.quotation.ItemAdapter
import com.example.jeffaccount.ui.home.quotation.OnAddedItemClickListener


private lateinit var supplier: SupList
private var itemList: MutableList<Item> = mutableListOf()
private lateinit var itemAdapter: ItemAdapter

class SupplierListAdapter(
    val context: Context,
    val onSupplierClickListener: OnSupplierClickListener,
    private val supplierList: List<SupList>
) :
    RecyclerView.Adapter<SupplierListAdapter.SupplierListViewHolder>() {

    class SupplierListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val supName: EditText = itemView.findViewById(R.id.purchase_supplier_name_editText)
        val supDate: EditText = itemView.findViewById(R.id.purchase_supplier_date_editText)
        val countyName: EditText = itemView.findViewById(R.id.purchase_supplier_county_editText)
        val paymentMethod:EditText = itemView.findViewById(R.id.purchase_supplier_payment_editText)
        val vat:EditText = itemView.findViewById(R.id.supplier_purchase_vat_editText)
        val itemNo: TextView = itemView.findViewById(R.id.purchase_supplier_item_no_tv)

        fun bind(supplier:SupList, clickListener: OnSupplierClickListener){
            supName.setText(supplier.supName)
            supDate.setText(supplier.supDate)
            countyName.setText(supplier.county)
            paymentMethod.setText(supplier.paymentMethod)
            vat.setText(supplier.vat.toString())
            val list = supplier.itemList
            list?.let {
                itemNo.text = "No of items: ${list.size}"
            }
            val position = adapterPosition
            itemView.setOnClickListener { clickListener.onSupplierClick(position,supplier) }
        }
        companion object {
            fun from(parent: ViewGroup): SupplierListViewHolder {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.supplier_list, parent, false)
                return SupplierListViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierListViewHolder {
        return SupplierListViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return supplierList.size
    }

    override fun onBindViewHolder(holder: SupplierListViewHolder, position: Int) {
        supplier = supplierList.get(position)
        holder.bind(supplier,onSupplierClickListener)
    }

}

interface OnSupplierClickListener{
    fun onSupplierClick(position: Int, supList: SupList)
}