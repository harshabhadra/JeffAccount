package com.example.jeffaccount.ui.home.quotation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R

class SearchNameAdapter(private val nameList:ArrayList<String>) : RecyclerView.Adapter<SearchNameAdapter.ViewHolder>() {


    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nametv: TextView = itemView.findViewById(R.id.name_tv)
        fun bind(item: String) {
            nametv.text = item
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.name_layout, parent, false)
                return ViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
       return nameList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = nameList.get(position)
        holder.bind(item)
    }
}