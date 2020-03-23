package com.example.jeffaccount.ui.home.quotation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.generated.callback.OnClickListener

class SearchNameAdapter(private val nameList:ArrayList<String>,val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<SearchNameAdapter.ViewHolder>() {


    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nametv: TextView = itemView.findViewById(R.id.name_tv)
        fun bind(item: String,clickListener: OnItemClickListener) {
            nametv.text = item
            itemView.setOnClickListener{clickListener.onItemClick(item)}
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
        holder.bind(item,itemClickListener)
    }
}

interface OnItemClickListener{
    fun onItemClick(name:String)
}