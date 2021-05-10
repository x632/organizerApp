package com.poema.theorganizerapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.adapters.VideoAdapter
import com.poema.theorganizerapp.utils.Utility
import com.poema.theorganizerapp.utils.Utility.isInternetAvailable
import com.poema.theorganizerapp.viewModels.ViewModelFactory
import com.poema.theorganizerapp.viewModels.MainViewViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_view.*
import kotlinx.coroutines.*
import java.util.*


class MainView : AppCompatActivity() {

    private lateinit var videoAdapter: VideoAdapter
    private lateinit var viewModel: MainViewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(this@MainView))
            .get(MainViewViewModel::class.java)
        val spinner = findViewById<ProgressBar>(R.id.progressBar2)
        viewModel.getVideos()
        setGroupListObserver(spinner)
        viewModel.getList()

        floatingActionButton.setOnClickListener(){
            val context = this
            if(context.isInternetAvailable()){
                goToAddVideo()
            }
            else {
                val msg = "You cannot search YouTube without the internet, please check your connection"
                showToast(msg)
            }
        }
    }

    private fun goToAddVideo() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun setGroupListObserver(spinner: ProgressBar) {
        viewModel.getList().observe(this@MainView, { t ->
            main_recycler.apply {
                layoutManager = LinearLayoutManager(this@MainView)
                videoAdapter = VideoAdapter(this@MainView, t)
                adapter = videoAdapter
                println("!!!!Categories: $t")
            }
            videoAdapter.notifyDataSetChanged()
            spinner.visibility = View.GONE
        })
    }

    private fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show();
    }
}