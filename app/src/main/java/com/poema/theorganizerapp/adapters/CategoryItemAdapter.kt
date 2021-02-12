package com.poema.theorganizerapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.activities.AddVideoProps
import com.poema.theorganizerapp.activities.ShowVideo
import com.poema.theorganizerapp.models.Video

class CategoryItemAdapter(private val context: Context, private val categoryItem :List<Video>) : RecyclerView.Adapter<CategoryItemAdapter.CategoryItemViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder {
        return CategoryItemViewHolder(LayoutInflater.from(context).inflate(R.layout.cat_row_items,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryItemViewHolder, position: Int) {
        val video = categoryItem[position]
        Glide.with(holder.itemImage.context)
            .load(video.imageUrl)//.apply(RequestOptions.circleCropTransform())
            .into(holder.itemImage)
        holder.itemTitle.text = video.title
    }

    override fun getItemCount(): Int {
        return categoryItem.size
    }


    inner class CategoryItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val itemImage : ImageView = itemView.findViewById(R.id.item_image)
        var itemTitle : TextView = itemView.findViewById(R.id.textView5)

        init {
            itemView.setOnClickListener {
                val video = categoryItem[adapterPosition]
                val intent = Intent(context, ShowVideo::class.java)
                intent.putExtra("title", video.title)
                intent.putExtra("url", video.url)
                context.startActivity(intent)
               // Toast.makeText(context,"Video :${video.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}