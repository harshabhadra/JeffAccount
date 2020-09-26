package com.example.jeffaccount.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.jeffaccount.R
import com.example.jeffaccount.ui.home.quotation.ItemAdapter
import kotlinx.android.synthetic.main.spinner_list_item.view.*

class SearchAdapter(val context: Context, var nameList: MutableList<String>) : BaseAdapter(),Filterable {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view:View
        val vh:ItemHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.spinner_list_item, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }

        vh.name.text = nameList[position]

        return view
    }

    override fun getItem(position: Int): Any {
        return nameList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return nameList.size
    }

    class ItemHolder(itemView:View?){
        val name:TextView = itemView!!.findViewById(R.id.search_item_tv)
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }
}