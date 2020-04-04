package com.example.jeffaccount.ui.home.company

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.CompanyListItemBinding
import com.example.jeffaccount.model.ComPost

class CompanyListAdapter(val clickListener: CompanyClickListener) :
    ListAdapter<ComPost, CompanyListAdapter.CompanyViewHolder>(CompanyListDiffUtilCallBack()) {

    class CompanyViewHolder private constructor(val binding: CompanyListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: CompanyClickListener,item: ComPost) {
            binding.company = item
            binding.clicklistener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): CompanyViewHolder {
                val binding = CompanyListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CompanyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {

        return CompanyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener,item)
    }
}

class CompanyClickListener(val clickListener: (company: ComPost) -> Unit) {
    fun onClick(company: ComPost) = clickListener(company)
}

class CompanyListDiffUtilCallBack : DiffUtil.ItemCallback<ComPost>() {
    override fun areItemsTheSame(oldItem: ComPost, newItem: ComPost): Boolean {
        return oldItem.comid == newItem.comid
    }

    override fun areContentsTheSame(oldItem: ComPost, newItem: ComPost): Boolean {
        return oldItem == newItem
    }

}