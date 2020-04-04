package com.example.jeffaccount.ui.home.purchase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.PurchaseListItemBinding
import com.example.jeffaccount.model.PurchasePost

class PurchaseListAdapter(val clickListener: PurchaseClickListener) :ListAdapter<PurchasePost,PurchaseListAdapter.PurchaseViewHolder>(PurchaseListDiffUtilCallBack()){

    class PurchaseViewHolder private constructor(val binding: PurchaseListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: PurchaseClickListener, item: PurchasePost) {
            binding.purchase = item
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): PurchaseViewHolder {
                val binding = PurchaseListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PurchaseViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {

        return PurchaseViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PurchaseViewHolder, position: Int) {

        val item = getItem(position)
        holder.bind(clickListener,item)
    }
}

class PurchaseClickListener(val clickListener:(purchase: PurchasePost)->Unit){
    fun purchaseItemClick(purchase: PurchasePost) = clickListener(purchase)
}
class PurchaseListDiffUtilCallBack : DiffUtil.ItemCallback<PurchasePost>() {
    override fun areItemsTheSame(oldItem: PurchasePost, newItem: PurchasePost): Boolean {
        return oldItem.pid == newItem.pid
    }

    override fun areContentsTheSame(oldItem: PurchasePost, newItem: PurchasePost): Boolean {
        return oldItem == newItem
    }

}