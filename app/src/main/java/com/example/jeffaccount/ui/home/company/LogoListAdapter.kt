package com.example.jeffaccount.ui.home.company

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.jeffaccount.R
import com.example.jeffaccount.model.Logo
import com.squareup.picasso.Picasso
import timber.log.Timber

class LogoListAdapter(val logoList: List<Logo>, val clickListener: OnLogoItemClickListener) :
    RecyclerView.Adapter<LogoListAdapter.LogoListViewHolder>() {

    class LogoListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val logoIv: ImageView = itemView.findViewById(R.id.logo_imageView)
        fun bind(logo: Logo,clickListener: OnLogoItemClickListener) {
            val imageUri = "https://alphabusinessdesigns.com/wordpress/appproject/jtapp/${logo.fileName}"
            Timber.e(imageUri)
            Picasso.get().load(imageUri).into(logoIv)
            itemView.setOnClickListener {clickListener.onLogoItemClick(logo)}
        }

        companion object {
            fun from(parent: ViewGroup): LogoListViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.logo_list_item, parent, false)
                return LogoListViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogoListViewHolder {
        return LogoListViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return logoList.size
    }

    override fun onBindViewHolder(holder: LogoListViewHolder, position: Int) {
        val logo = logoList.get(position)
        holder.bind(logo,clickListener)
    }
}

interface OnLogoItemClickListener{
    fun onLogoItemClick(logo: Logo)
}