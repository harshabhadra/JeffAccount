package com.example.jeffaccount.ui.home.timeSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.databinding.WorkerListItemBinding
import com.example.jeffaccount.network.WorkerList

class WorkerListAdapter(val clickListener: WorkerItemClickListener):ListAdapter<WorkerList,WorkerListAdapter.WokerListViewHolder>(WorkerDiffUtilCallBack()) {

    class WokerListViewHolder(val binding: WorkerListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(worker: WorkerList, clickListener: WorkerItemClickListener) {
            binding.worker = worker
            binding.clicklistener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): WokerListViewHolder {
                val binding = WorkerListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return WokerListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WokerListViewHolder {
        return WokerListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: WokerListViewHolder, position: Int) {
        val worker = getItem(position)
        holder.bind(worker,clickListener)
    }
}

class WorkerItemClickListener(val clickListener:(worker:WorkerList)->Unit){
    fun workerItemClick(worker: WorkerList) = clickListener(worker)
}
class WorkerDiffUtilCallBack:DiffUtil.ItemCallback<WorkerList>(){
    override fun areItemsTheSame(oldItem: WorkerList, newItem: WorkerList): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: WorkerList, newItem: WorkerList): Boolean {
        return  oldItem.name == newItem.name
    }

}