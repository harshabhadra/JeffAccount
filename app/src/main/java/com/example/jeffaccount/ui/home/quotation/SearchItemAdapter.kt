package com.example.jeffaccount.ui.home.quotation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.SearchCustListItemBinding
import com.example.jeffaccount.network.SearchCustomer

class SearchItemAdapter(val clickListneer:SearchClickListener) : ListAdapter<SearchCustomer, SearchItemAdapter.SearchItemViewHolder>(
    SearchCustomerDiffUtilCallBack()
) {

    class SearchItemViewHolder private constructor(val binding: SearchCustListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchCustomer,clickListneer: SearchClickListener) {
            binding.post = item
            binding.clicklistener = clickListneer
        }

        companion object {
            fun from(parent: ViewGroup): SearchItemViewHolder {
                val binding = SearchCustListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SearchItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        return SearchItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,clickListneer)
    }
}

class SearchClickListener(val clickListener: (searchCusotmer: SearchCustomer) -> Unit) {
    fun searchItemClick(searchCusotmer: SearchCustomer) = clickListener(searchCusotmer)
}

class SearchCustomerDiffUtilCallBack : DiffUtil.ItemCallback<SearchCustomer>() {
    override fun areItemsTheSame(oldItem: SearchCustomer, newItem: SearchCustomer): Boolean {
        return oldItem.comid == newItem.comid
    }

    override fun areContentsTheSame(oldItem: SearchCustomer, newItem: SearchCustomer): Boolean {
        return oldItem == newItem
    }

}