package com.example.jeffaccount.ui.home.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.CustomerListItemBinding
import com.example.jeffaccount.model.Post

class CusomerListAdapter(val clickListener: CustomerItemListener) :
    ListAdapter<Post, CusomerListAdapter.CustomerListViewHolder>(CustomerListDiffUtilCallBack()) {

    class CustomerListViewHolder private constructor(val binding: CustomerListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: CustomerItemListener, item: Post) {
            binding.post = item
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): CustomerListViewHolder {
                val binding = CustomerListItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)

                return CustomerListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerListViewHolder {
        return CustomerListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CustomerListViewHolder, position: Int) {

        val item = getItem(position)
        holder.bind(clickListener, item)

    }
}

class CustomerItemListener(val clickListener: (customer: Post) -> Unit) {
    fun onClick(customer: Post) = clickListener(customer)
}

class CustomerListDiffUtilCallBack : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.custid == newItem.custid
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {

        return oldItem == newItem
    }

}