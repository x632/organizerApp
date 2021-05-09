package com.poema.theorganizerapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.activities.MainView
import com.poema.theorganizerapp.activities.ShowVideo
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.utils.Utility.isInternetAvailable


class CategoryItemAdapter(private val context: Context, private val categoryItem :List<Video>) : RecyclerView.Adapter<CategoryItemAdapter.CategoryItemViewHolder>(){

    private var uid = ""

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
        private val itemDeleteImage : ImageView = itemView.findViewById(R.id.deleteImageView)

        init {

            itemView.setOnClickListener {
                if(context.isInternetAvailable()) {
                    val video = categoryItem[adapterPosition]
                    val intent = Intent(context, ShowVideo::class.java)
                    intent.putExtra("title", video.title)
                    intent.putExtra("url", video.url)
                    intent.putExtra("docId", video.docId)
                    context.startActivity(intent)
                }
                else{
                    val msg="the YouTube-stream is not cached, check your internet-connection"
                    showToast(msg)
                }
            }

            itemDeleteImage.setOnClickListener{ view ->
                if(context.isInternetAvailable()) {
                    val video = categoryItem[adapterPosition]
                    val dialogBuilder = AlertDialog.Builder(context)

                    dialogBuilder.setTitle("Remove video")
                        .setMessage("Are you sure you want to remove this video?")
                        //.setCancelable(false)

                        .setIcon(0)
                        .setPositiveButton("Remove video") { _, _ ->
                            deleteVideo(video)
                            Snackbar.make(view, "Video removed", Snackbar.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                        }

                    val alert = dialogBuilder.create()

                    alert.show()
                }
                else{
                    val msg="Your online database has priority, therefore deleting can only be made with an active internetconnection"
                    showToast(msg)
                }
            }
        }
    }
    private fun deleteVideo(video: Video){

            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val docId = video.docId
            val currentUser = auth.currentUser
            if (currentUser != null) {
                uid = auth.currentUser!!.uid
            }
            db.collection("users").document(uid).collection("videos").document(docId!!).delete()
                .addOnSuccessListener{
                    val msg="Video was successfully deleted from your database"
                    showToast(msg)
                    if (auth.currentUser != null) {
                        uid = auth.currentUser!!.uid
                    }
                    val intent=Intent(context,MainView::class.java)
                    context.startActivity(intent)
                }
                .addOnFailureListener {
                }
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}