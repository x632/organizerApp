package com.poema.theorganizerapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.adapters.VideoAdapter
import com.poema.theorganizerapp.utils.Utility.isInternetAvailable
import com.poema.theorganizerapp.viewModelFactory.ViewModelFactory
import com.poema.theorganizerapp.viewModels.MainViewViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_view.*
import kotlinx.coroutines.*
import java.util.*


class MainView : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var viewModel: MainViewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view)
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btnDrawer.setOnClickListener{drawerLayout.openDrawer(Gravity.LEFT)}


        viewModel = ViewModelProviders.of(this, ViewModelFactory(this@MainView))
            .get(MainViewViewModel::class.java)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mItem1 -> { Firebase.auth.signOut()
                    val intent = Intent(this,Login::class.java)
                    startActivity(intent)}
            }
            true
        }
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

    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        return super.onOptionsItemSelected(item)
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