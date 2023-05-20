package com.example.firebase_project.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_project.R
import com.example.firebase_project.dataclass.ParentItem


class FoodListAdapter(private var parentList :List<ParentItem>):RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>(){


    inner class FoodViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        val imgdish  : ImageView
        val tvdishname :TextView
         val childrecycleview : RecyclerView

        init {
            imgdish = itemView.findViewById(R.id.imgdish)
            tvdishname = itemView.findViewById(R.id.tvdishname)
            childrecycleview = itemView.findViewById(R.id.rv_sub_category)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_food,parent,false)
          return FoodViewHolder(view)



    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
           val parentItem = parentList[position]
          holder.imgdish.setImageResource(parentItem.logo)
          holder.tvdishname.text = parentItem.title


        holder.childrecycleview.setHasFixedSize(true)
        holder.childrecycleview.layoutManager = LinearLayoutManager(holder.itemView.context)
        val adapter = ChildAdapter(parentItem.mList)
        holder.childrecycleview.adapter = adapter
    }
    fun updateData(parentList: List<ParentItem>) {
        this.parentList = parentList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return parentList.size
    }
}
