package com.example.jeffaccount.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.CountryListItemBinding
import com.example.jeffaccount.model.Country

class CountryListAdapter(val clickListener: CountryClickListener) :
    ListAdapter<Country, CountryListAdapter.CountryListViewHolder>(CountryDiffUtilCallBack()) {

    class CountryListViewHolder(val binding: CountryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country,clickListener: CountryClickListener) {
            binding.country = country
            binding.clicklistener =clickListener
        }

        companion object {

            fun from(parent: ViewGroup): CountryListViewHolder {

                val binding = CountryListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CountryListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryListViewHolder {
        return CountryListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CountryListViewHolder, position: Int) {
        val country = getItem(position)
        holder.bind(country,clickListener)
    }
}

class CountryClickListener(val clickListener: (country: Country) -> Unit) {
    fun onCountryClick(country: Country) = clickListener(country)
}

class CountryDiffUtilCallBack : DiffUtil.ItemCallback<Country>() {
    override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
        return oldItem.code == newItem.code
    }

}