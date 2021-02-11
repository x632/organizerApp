package com.poema.theorganizerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.dataClasses.Video
import com.poema.theorganizerapp.adapters.VideoAdapter
import com.poema.theorganizerapp.dataClasses.EntireCategory
import com.poema.theorganizerapp.dataClasses.VideosGlobal
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_view.*
import java.util.ArrayList

class MainView : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var videos: MutableList<Video>
    private var uid: String = ""
    private lateinit var allGroups: MutableList<EntireCategory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }

        videos = mutableListOf()
        allGroups = ArrayList()
        getVideos()

        addVideoBtn.setOnClickListener(){
            goToAddVideo()
        }
    }

    private fun goToAddVideo() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getVideos() {
        db.collection("users").document(uid).collection("videos")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val temp = document!!.toObject(Video::class.java)
                    videos.add(temp)
                    VideosGlobal.videosGlobal.add(temp)
                }
                allGroups.add(EntireCategory("Group1", videos))
                initRecyclerView(allGroups)
                videoAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("!!! Error getting users: $exception")//blir ej error om den inte hittar någon träff.
            }
    }

    private fun initRecyclerView(allGroups:MutableList<EntireCategory>){
        main_recycler.apply{
            layoutManager = LinearLayoutManager(this@MainView)
            videoAdapter = VideoAdapter(this@MainView,allGroups)
            adapter = videoAdapter
        }
    }


}