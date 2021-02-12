package com.poema.theorganizerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.adapters.VideoAdapter
import com.poema.theorganizerapp.models.EntireCategory
import com.poema.theorganizerapp.models.VideosGlobal.videosGlobal
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_view.*
import java.util.*

class MainView : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private lateinit var videoAdapter: VideoAdapter
    private var videos = mutableListOf<Video>()
    private var uid: String = ""
    private var allGroups = mutableListOf<EntireCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }



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
                    videosGlobal.add(temp)
                }
                // val immutableVideos = Collections.unmodifiableList(videos)
                val existingTitles = mutableListOf<String>()

                //sortera vilka grupptitlar som finns
                for (i in 0 until videos.size) {
                    for (j in 0 until videos.size) {
                        if (existingTitles.contains(videos[j].groupTitle)) {//do nothing
                        }else{
                            if (videos[i].groupTitle == videos[j].groupTitle){
                                existingTitles.add(videos[j].groupTitle)
                            }
                        }
                    }
                   println("Detta är de existerande titlarna: ${existingTitles}")

                }
                //sortera in videos beroende på grupptitel
                var vids2 = mutableListOf<Video>()
                for (i in 0 until existingTitles.size) {
                    for (j in 0 until videos.size) {
                        if (videos[j].groupTitle == existingTitles[i]) {
                            vids2.add(videos[j])
                        }
                    }

                    allGroups.add(EntireCategory(existingTitles[i], vids2))
                    vids2 = mutableListOf()
                }

                initRecyclerView()
                videoAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("!!! Error getting users: $exception")//blir ej error om den inte hittar någon träff.
            }
    }


    private fun initRecyclerView(){
        main_recycler.apply{
            layoutManager = LinearLayoutManager(this@MainView)
            videoAdapter = VideoAdapter(this@MainView,allGroups)
            adapter = videoAdapter
        }
    }


}