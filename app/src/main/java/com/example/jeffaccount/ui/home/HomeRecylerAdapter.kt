package com.example.jeffaccount.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.HomeItemBinding
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter:
    ListAdapter<Home, HomeRecyclerAdapter.HomeViewHolder>(HomeDiffUtilCallback()){

    var onItemClick:((pos:Int, view:View)->Unit)? = null
    inner class HomeViewHolder constructor(itemView:View):
        RecyclerView.ViewHolder(itemView),View.OnClickListener {

        val name:TextView =itemView.findViewById(R.id.home_item_tv)
        val image:ImageView = itemView.findViewById(R.id.home_item_iv)

        override fun onClick(v: View?) {

            val position = adapterPosition
            if (v!= null){
                onItemClick?.invoke(position,v)
            }
        }
        init {
            itemView.setOnClickListener(this)
        }
    }

    fun getItemName(position: Int):Home{

        return currentList[position]
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.home_item,parent,false
        ))
    }


    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val item = getItem(position)
        holder.name.text = item.title
        Picasso.get().load(item.image)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.image)
    }
}

class HomeDiffUtilCallback: DiffUtil.ItemCallback<Home>(){
    override fun areItemsTheSame(oldItem: Home, newItem: Home): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Home, newItem: Home): Boolean {
        return oldItem == newItem
    }

}