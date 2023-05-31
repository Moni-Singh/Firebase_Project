package com.example.firebase_project.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_project.R
import com.example.firebase_project.add_item
import com.example.firebase_project.dataclass.ChildItem
import com.squareup.picasso.Picasso

class ChildAdapter(private var childList: List<ChildItem>) :
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
        Picasso.get().load(childItem.image).into(holder.imgsubdish)

        holder.imgsubdish.setOnClickListener {

                val intent = Intent(holder.itemView.context, add_item::class.java)
                holder.itemView.context.startActivity(intent)


        }


    }

    override fun getItemCount(): Int {
        return childList.size
    }

    fun updateList(filteredList: ArrayList<ChildItem>) {
        childList = filteredList
        notifyDataSetChanged()

    }
}


