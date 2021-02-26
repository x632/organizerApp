package com.poema.theorganizerapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.viewModels.ShowVideoViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_view.*
import kotlinx.android.synthetic.main.activity_show_video.*


class ShowVideo : AppCompatActivity() {


    private lateinit var viewModel: ShowVideoViewModel

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_video)

        val url = intent.getStringExtra("url")
        val str = url!!.substringAfter("be/")
        val docId = intent.getStringExtra("docId")

        viewModel = ViewModelProvider(this@ShowVideo).get(ShowVideoViewModel::class.java)

        val toggleButton = findViewById<Switch>(R.id.buttonToggle)
        toggleButton.isChecked=false
        toggleButton?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                removeBtn.visibility = View.GONE
                backBtn.visibility = View.GONE
                toggleButton.text=""

            }else{
                removeBtn.visibility = View.VISIBLE
                backBtn.visibility = View.VISIBLE
                toggleButton.text = "hide buttons"
            }
        }

        val youTubePlayerView: YouTubePlayerView = findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(str, 0f)
            }
        })
        setRemoveFinishedObserver()

        removeBtn.setOnClickListener{
            viewModel.removeVideo(docId)
        }
        backBtn.setOnClickListener{
                val intent = Intent(this, MainView::class.java)
                startActivity(intent)
        }
    }

    private fun setRemoveFinishedObserver() {
        viewModel.getIsRemoved().observe(this@ShowVideo, { t ->
            if(t) {
                val msg = "Video was successfully deleted from your database"
                showToast(msg)
                val intent = Intent(this, MainView::class.java)
                startActivity(intent)
            }
        })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}