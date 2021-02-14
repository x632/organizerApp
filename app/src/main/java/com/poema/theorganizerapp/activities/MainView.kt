package com.poema.theorganizerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import android.widget.ToggleButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.adapters.VideoAdapter
import com.poema.theorganizerapp.models.EntireCategory
import com.poema.theorganizerapp.models.VideosGlobal.videosGlobal
import com.poema.theorganizerapp.viewModels.MainViewViewModel
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

        var viewModel = ViewModelProvider(this@MainView).get(MainViewViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
        getVideos()
        floatingActionButton.setOnClickListener(){
            goToAddVideo()
        }
    }



    private fun goToAddVideo() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getVideos() {
        val spinner = findViewById<ProgressBar>(R.id.progressBar2)
        spinner.visibility = View.VISIBLE
        db.collection("users").document(uid).collection("videos")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val temp = document!!.toObject(Video::class.java)
                    videosGlobal.clear()
                    videos.add(temp)
                    videosGlobal.add(temp)
                }
                // val immutableVideos = Collections.unmodifiableList(videos)
                val existingTitles = mutableListOf<String>()

                //sortera fram vilka grupptitlar som finns
                for (i in 0 until videos.size) {
                    for (j in 0 until videos.size) {
                        if (!existingTitles.contains(videos[j].groupTitle)) {
                           if (videos[i].groupTitle == videos[j].groupTitle){
                                existingTitles.add(videos[j].groupTitle)
                            }
                        }
                    }
                }

                val sortedExistingTitles = sortAlphabetically(existingTitles)

                //sortera in videos beroende på grupptitel
                var vids2 = mutableListOf<Video>()
                for (i in 0 until sortedExistingTitles.size) {
                    for (j in 0 until videos.size) {
                        if (videos[j].groupTitle == sortedExistingTitles[i]) {
                            vids2.add(videos[j])
                        }
                    }
                    allGroups.add(EntireCategory(sortedExistingTitles[i], vids2))
                    vids2 = mutableListOf() //obs! - går inte att tömma med clear!!
                }

                initRecyclerView()
                videoAdapter.notifyDataSetChanged()
                spinner.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                println("!!! Error getting users: $exception.message")
            }
    }

    fun sortAlphabetically(titles: MutableList<String>):MutableList<String>{
        var swap = true
        while(swap){
            swap = false
            for(i in 0 until titles.size-1){
                if(titles[i] > titles[i+1]){
                    val temp = titles[i]
                    titles[i] = titles[i+1]
                    titles[i + 1] = temp

                    swap = true
                }
            }
        }
        return titles
    }


    private fun initRecyclerView(){
        main_recycler.apply{
            layoutManager = LinearLayoutManager(this@MainView)
            videoAdapter = VideoAdapter(this@MainView,allGroups)
            adapter = videoAdapter
        }
    }


}