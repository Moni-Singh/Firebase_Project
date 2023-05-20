package com.example.firebase_project.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_project.R
import com.example.firebase_project.dataclass.ChildItem

class ChildAdapter(private val childList: List<ChildItem>) :
    RecyclerView.Adapter<ChildAdapter.SubViewHolder>() {

    inner class SubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
          val tvsubdishname :TextView
        val imgsubdish:ImageView

        init {
            imgsubdish = itemView.findViewById(R.id.imgsubdish)
            tvsubdishname = itemView.findViewById(R.id.tvsubdishname)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_sub_food, parent, false)
        return SubViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubViewHolder, position: Int) {
           val childItem = childList[position]
        holder.tvsubdishname.text = childItem.title
        holder.imgsubdish.setImageResource(childItem.logo)
    }

    override fun getItemCount(): Int {
        return childList.size
    }
}
