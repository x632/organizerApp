package com.poema.theorganizerapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.models.EntireCategory
import com.poema.theorganizerapp.models.Video

class VideoAdapter (private val context: Context): RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private var groups: List<EntireCategory> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.main_recycler_row_item,parent,false))
    }

    fun submitList(list: List<EntireCategory>){
        groups = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.categoryTitle.text=groups[position].categoryTitle
        setCatItemRecycler(holder.itemRecycler, groups[position].categoryItems)
    }

    override fun getItemCount(): Int {

        return groups.size
    }

    private fun setCatItemRecycler(recyclerView: RecyclerView, videos: List<Video>){
        val itemRecyclerAdapter = CategoryItemAdapter(context)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
        recyclerView.adapter = itemRecyclerAdapter
        itemRecyclerAdapter.submitList(videos)


    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        var categoryTitle: TextView = itemView.findViewById(R.id.cat_title)
        var itemRecycler: RecyclerView = itemView.findViewById(R.id.cat_item_recycler)

        init {
            itemView.setOnClickListener {

            }
        }
    }
}



