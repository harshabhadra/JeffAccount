package com.example.jeffaccount.ui.home.quotation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.ItemListBinding
import com.example.jeffaccount.network.Item
import timber.log.Timber

class ItemAdapter(val clickListener: OnAddedItemClickListener) : ListAdapter<Item, ItemAdapter.ItemViewHolder>(ItemDiffUtilCallBack()) {

    class ItemViewHolder private constructor(val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item,clickListener: OnAddedItemClickListener) {
            binding.item = item
            binding.clicklistner = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val binding =
                    ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        Timber.e("item is ${item.itemDes}")
        holder.bind(item,clickListener)
    }
}

class OnAddedItemClickListener(val clickListener:(item:Item)->Unit){
    fun itemClick(item: Item) = clickListener(item)
}
class ItemDiffUtilCallBack : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.noOfItem == newItem.noOfItem
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}