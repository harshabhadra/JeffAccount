package com.example.jeffaccount.ui.home.quotation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.QuotationListItemBinding
import com.example.jeffaccount.model.QuotationPost

class QuotationListAdapter(val clickListener: QuotationClickListener) :
    ListAdapter<QuotationPost, QuotationListAdapter.QuotationViewHolder>(
        QuotationListDiffUtilCallback()
    ) {

    class QuotationViewHolder private constructor(val binding: QuotationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: QuotationClickListener, item: QuotationPost) {
            binding.quotation = item
            binding.quotationClick = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): QuotationViewHolder {
                val binding = QuotationListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return QuotationViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotationViewHolder {

        return QuotationViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: QuotationViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }
}

class QuotationClickListener(val clickListener: (quotation: QuotationPost) -> Unit) {
    fun quotationItemClick(quotation: QuotationPost) = clickListener(quotation)
}

class QuotationListDiffUtilCallback : DiffUtil.ItemCallback<QuotationPost>() {
    override fun areItemsTheSame(oldItem: QuotationPost, newItem: QuotationPost): Boolean {
        return oldItem.qid == newItem.qid
    }

    override fun areContentsTheSame(oldItem: QuotationPost, newItem: QuotationPost): Boolean {
        return oldItem == newItem
    }

}