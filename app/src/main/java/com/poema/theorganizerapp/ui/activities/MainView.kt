package com.poema.theorganizerapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.adapters.VideoAdapter
import com.poema.theorganizerapp.utils.Utility.isInternetAvailable

import com.poema.theorganizerapp.ui.viewmodels.MainViewViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_view.*
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class MainView : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var viewModel: MainViewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view)
        main_recycler.apply {
        layoutManager = LinearLayoutManager(this@MainView)
        videoAdapter = VideoAdapter(this@MainView)
        adapter = videoAdapter}
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btnDrawer.setOnClickListener{drawerLayout.openDrawer(Gravity.LEFT)}

        //drawerLayout.closeDrawer(GravityCompat.START);
        viewModel = ViewModelProvider(this).get(MainViewViewModel::class.java)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mItem1 -> { Firebase.auth.signOut()
                    val intent = Intent(this,Login::class.java)
                    startActivity(intent)
                }
                R.id.mItem2 -> { viewModel.sortingAlphabetically=true
                    val internetConnection = this.isInternetAvailable()
                    //viewModel.getVideos(internetConnection)
                }
                R.id.mItem3 -> { viewModel.sortingAlphabetically=false
                    val internetConnection = this.isInternetAvailable()
                    //viewModel.getVideos(internetConnection)      //ändrar här och ovan !!!!!!!!!!!!!!!!!!!!!!!!!
                }
            }
            true
        }
        val spinner = findViewById<ProgressBar>(R.id.progressBar2)
        val internetConnection = this.isInternetAvailable()
        //viewModel.getVideos(internetConnection)
        //setGroupListObserver(spinner)
        //viewModel.getList()
        subscribeToChanges(spinner)

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

   /* private fun setGroupListObserver(spinner: ProgressBar) {
        viewModel.getList().observe(this@MainView, { entireCategoryList ->
            main_recycler.apply {
                layoutManager = LinearLayoutManager(this@MainView)
                videoAdapter = VideoAdapter(this@MainView, entireCategoryList)
                adapter = videoAdapter
            }
            videoAdapter.notifyDataSetChanged()
            spinner.visibility = View.GONE
        })
    }*/
    // testar med funktionen nedan iställer för den ovan
    private fun subscribeToChanges(spinner: ProgressBar){
        viewModel.fromListener.observe(this@MainView, {entireCategoryList ->
            var amount = 0
            for(i in 0 until entireCategoryList.size) {
                for (j in 0 until entireCategoryList[i].categoryItems.size) {
                    println("!!! Från obs.: category: ${entireCategoryList[i].categoryItems[j].title}")
                    amount++
                }
            }
            println("!!! Thare are ${amount} videos!!")
            //main_recycler.apply {
                /* layoutManager = LinearLayoutManager(this@MainView)
                videoAdapter = VideoAdapter(this@MainView, entireCategoryList)
                adapter = videoAdapter*/
                videoAdapter.submitList(entireCategoryList)
            //videoAdapter.notifyDataSetChanged() //denna behövs nu
            //}
            spinner.visibility = View.GONE

        })
    }
    // testar här
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
       showToast("You can´t go back here!")
    }
}