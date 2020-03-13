package com.example.jeffaccount.ui.home.timeSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.TimeSheetListItemBinding
import com.example.jeffaccount.model.TimeSheetPost

class TimeSheetListAdapter(val clickListener: TimeSheetClickListener) :
    ListAdapter<TimeSheetPost, TimeSheetListAdapter.TimeSheetViewHolder>(TimeSheetDiffUtilCallBack()) {

    class TimeSheetViewHolder private constructor(val binding: TimeSheetListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: TimeSheetClickListener,item: TimeSheetPost) {
            binding.timeSheet = item
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): TimeSheetViewHolder {
                val binding = TimeSheetListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return TimeSheetViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSheetViewHolder {
        return TimeSheetViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TimeSheetViewHolder, position: Int) {

        val item = getItem(position)
        holder.bind(clickListener,item)
    }
}

class TimeSheetClickListener(val clickListener: (timeSheet: TimeSheetPost) -> Unit) {
    fun timeSheetItemClick(timeSheet: TimeSheetPost) = clickListener(timeSheet)
}

class TimeSheetDiffUtilCallBack : DiffUtil.ItemCallback<TimeSheetPost>() {
    override fun areItemsTheSame(oldItem: TimeSheetPost, newItem: TimeSheetPost): Boolean {
        return oldItem.tid == newItem.tid
    }

    override fun areContentsTheSame(oldItem: TimeSheetPost, newItem: TimeSheetPost): Boolean {
        return oldItem == newItem
    }

}