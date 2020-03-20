package com.example.jeffaccount.ui.home.purchase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.SearchSuppListBinding
import com.example.jeffaccount.network.SearchSupplierPost

class SearchSupplierAdapter(val clickListener: SearchSupplierClickListener):ListAdapter<SearchSupplierPost,SearchSupplierAdapter.SearchSupplierViewHolder>(SearchSupplierDiffUtilCallBack()) {

    class SearchSupplierViewHolder private constructor(val binding: SearchSuppListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchSupplierPost,clickListener: SearchSupplierClickListener) {
            binding.post = item
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): SearchSupplierViewHolder {
                val binding = SearchSuppListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SearchSupplierViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSupplierViewHolder {

        return SearchSupplierViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchSupplierViewHolder, position: Int) {

        val item = getItem(position)
        holder.bind(item,clickListener)
    }
}

class SearchSupplierClickListener(val clickListener:(searchSupplierPost:SearchSupplierPost)->Unit){
    fun onSearchSuppClick(searchSupplierPost: SearchSupplierPost) = clickListener(searchSupplierPost)
}

class SearchSupplierDiffUtilCallBack:DiffUtil.ItemCallback<SearchSupplierPost>(){
    override fun areItemsTheSame(
        oldItem: SearchSupplierPost,
        newItem: SearchSupplierPost
    ): Boolean {
        return oldItem.supid == newItem.supid
    }

    override fun areContentsTheSame(
        oldItem: SearchSupplierPost,
        newItem: SearchSupplierPost
    ): Boolean {
        return oldItem == newItem
    }
}