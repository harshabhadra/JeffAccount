package com.example.jeffaccount.ui.home.supplier

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.CustomerListItemBinding
import com.example.jeffaccount.databinding.SupplierListItemBinding
import com.example.jeffaccount.model.SupPost

class SupplierListAdapter(val clickListener:SupplierItemListener)
    :ListAdapter<SupPost,SupplierListAdapter.SupplierViewHolder>(SupplierDiffUtilCallBack()){

    class SupplierViewHolder private constructor(val binding:SupplierListItemBinding):
            RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: SupplierItemListener, item:SupPost){
            binding.clicklistener = clickListener
            binding.supPost = item
        }

        companion object {
            fun from(parent: ViewGroup): SupplierViewHolder {
                val binding
                        = SupplierListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return SupplierViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierViewHolder {
        return SupplierViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SupplierViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener,item)
    }
}

class SupplierItemListener(val clickListener:(supplier:SupPost)->Unit){
    fun onSupplierClick(supplier: SupPost) = clickListener(supplier)
}

class SupplierDiffUtilCallBack:DiffUtil.ItemCallback<SupPost>(){
    override fun areItemsTheSame(oldItem: SupPost, newItem: SupPost): Boolean {
        return oldItem.supid == newItem.supid
    }

    override fun areContentsTheSame(oldItem: SupPost, newItem: SupPost): Boolean {
        return oldItem == newItem
    }

}