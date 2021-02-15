package com.poema.theorganizerapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.models.VideosGlobal
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_show_video.*
import java.util.logging.Logger.global


class ShowVideo : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private var uid: String = ""


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_video)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }

        val url = intent.getStringExtra("url")
        val str = url!!.substringAfter("be/")
        val docId = intent.getStringExtra("docId")

        val toggleButton = findViewById<Switch>(R.id.buttonToggle)
        toggleButton.isChecked=false
        toggleButton?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                removeBtn.visibility = View.GONE
                backBtn.visibility = View.GONE

            }else{
                removeBtn.visibility = View.VISIBLE
                backBtn.visibility = View.VISIBLE
            }
        }

        println("!!!! strängen : $str")
        val youTubePlayerView: YouTubePlayerView = findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = str
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })

        removeBtn.setOnClickListener(){
            removeVideo(docId)
        }
        backBtn.setOnClickListener(){
                val intent = Intent(this, MainView::class.java)
                startActivity(intent)
        }

    }

    private fun removeVideo(docId: String?) {
        db.collection("users").document(uid).collection("videos").document(docId!!).delete()
            .addOnSuccessListener{
                val msg="Video was successfully deleted from your database"
                showToast(msg)
                VideosGlobal.videosGlobal.removeAt(0)
                val intent = Intent(this, MainView::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {

            }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}