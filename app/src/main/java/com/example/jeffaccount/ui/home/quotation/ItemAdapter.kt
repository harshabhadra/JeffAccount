package com.example.jeffaccount.ui.home.quotation

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.DragStartHelper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.ItemListBinding
import com.example.jeffaccount.network.Item
import com.example.jeffaccount.utils.ItemMoveCallbackListener
import kotlinx.android.synthetic.main.item_list.view.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

 class ItemAdapter(val clickListener: OnAddedItemClickListener, private val startDragListener:OnStartDragListener) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(),ItemMoveCallbackListener.Listener {

    var items:List<Item> = ArrayList()
    class ItemViewHolder private constructor(val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item,clickListener: OnAddedItemClickListener) {
            binding.item = item
            binding.root.setOnClickListener {
                clickListener.itemClick(item,adapterPosition)
            }
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
        val item = items.get(position)
        Timber.e("item is ${item.itemDes}")
        holder.bind(item,clickListener)
        holder.binding.root.quotation_item_del_iv.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                this.startDragListener.onStartDrag(holder)
            }
            return@setOnTouchListener true
        }
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(itemViewHolder: ItemViewHolder) {
    }

    override fun onRowClear(itemViewHolder: ItemViewHolder) {
    }

    fun submitList(items:List<Item>){
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        items?.let {
            return items.size
        }
    }
}

interface OnAddedItemClickListener{
    fun itemClick(item: Item, position: Int)
}
interface OnStartDragListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}
class ItemDiffUtilCallBack : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.itemDes == newItem.itemDes
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}