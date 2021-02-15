package com.poema.theorganizerapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.models.VideosGlobal
import com.poema.theorganizerapp.viewModels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_view.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var spinner : ProgressBar
    private var url : String = ""
    private var data: String=""
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this@MainActivity).get(MainActivityViewModel::class.java)

        searchBtn.setOnClickListener(){
            val searchTerm = searchInput.text.toString()
            val intent = Intent(Intent.ACTION_SEARCH)
            intent.setPackage("com.google.android.youtube")
            intent.putExtra("query", searchTerm)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        addUrl.setOnClickListener(){
            url = pastedUrl.text.toString();
            viewModel.setUrl(url)
            spinner = findViewById(R.id.progressBar)
            spinner.visibility = View.VISIBLE
            setStringObserver()
        }
    }

    private fun setStringObserver() {
        viewModel.getYouTubeString().observe(this@MainActivity,
            { t ->
                data=t
                spinner.visibility = View.GONE
                extractUrl()
            })
    }

    private fun extractUrl(){
        val imageUrl = viewModel.extractStuff("og:image",19, data)
        var msg = ""
        VideosGlobal.videosGlobal.forEach{
            if (imageUrl==it.imageUrl){
                msg = "You already have this video!"
            }
        }
        if (msg ==""){
            Glide.with(this)
                .load(imageUrl)//.apply(RequestOptions.circleCropTransform())
                .into(imageView)
                val title = viewModel.extractTitle(data)
                nextBtn.visibility = View.VISIBLE
                nextScreen(imageUrl, title)
        }
        else showToast(msg)
    }

    private fun nextScreen(imageUrl:String, title:String) {

        nextBtn.setOnClickListener(){
            val intent = Intent(this, AddVideoProps::class.java)
            intent.putExtra("title", title)
            intent.putExtra("url", url)
            intent.putExtra("imageUrl", imageUrl)
            startActivity(intent)
        }
    }
    private fun showToast(message: String){
        Toast.makeText(
            this, message,
            Toast.LENGTH_LONG).show()
    }

}