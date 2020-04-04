package com.example.jeffaccount.ui.home.invoice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.InvoiceListItemBinding
import com.example.jeffaccount.model.Invoice

class InvoiceListAdapter(val clickListener: InvoiceClickListener):ListAdapter<Invoice,InvoiceListAdapter.InvoiceListViewHolder>(InvoiceDiffUtilCallback()) {

    class InvoiceListViewHolder(val binding: InvoiceListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Invoice, clickListener: InvoiceClickListener) {
            binding.invoice = item
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): InvoiceListViewHolder {
                val binding = InvoiceListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return InvoiceListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceListViewHolder {
        return InvoiceListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: InvoiceListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }
}

class InvoiceClickListener(val clickListener:(invoice:Invoice)->Unit){
    fun invoiceItemClick(invoice: Invoice) = clickListener(invoice)
}

class InvoiceDiffUtilCallback:DiffUtil.ItemCallback<Invoice>(){
    override fun areItemsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
        return oldItem.inid == newItem.inid
    }

}