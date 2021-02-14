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

    //private lateinit var job1: CompletableJob
    private var words: String = ""
    private lateinit var spinner : ProgressBar
    private var url : String = ""
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
        viewModel.getWords().observe(this@MainActivity,
            { t ->
                words=t
                spinner.visibility = View.GONE
                extractUrl()
            })
    }


   /* fun webScratch() {
        spinner = findViewById<ProgressBar>(R.id.progressBar)
        spinner.visibility = View.VISIBLE
        job1 = Job()
        job1.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "Unknown cancellation error."
                }
                println("!!! $job1 was cancelled. Reason: ${msg}")
            }
        }

        CoroutineScope(Dispatchers.IO + job1).launch {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                words = response.body!!.string()
            }
            completionHandler()
        }
            }

    private fun completionHandler() {
            CoroutineScope(Dispatchers.Main)
            .launch {
                println("!!! Coroutine is finished")
                job1.cancel(CancellationException("Reset job when done"))
                println("!!!Job1 is cancelled")
                spinner.visibility = View.GONE
                    extractUrl()
            }
    }*/

    private fun matchDetails(inputString: String, whatToFind: String, startIndex: Int): Int {
        return inputString.indexOf(whatToFind, startIndex)
    }

    private fun extractStuff(str:String, index2:Int):String{
        val index = matchDetails(words,str,0)
        val stri = words.slice(index+index2..(index+200))
        val delimiter = "\""
        val parts = stri.split(delimiter)
        return parts[0]
    }

    private fun extractUrl(){
        val imageUrl = extractStuff("og:image",19)

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
            extractTitle(imageUrl)
        }
        else showToast(msg)
    }

    private fun extractTitle(imageUrl:String){
        val title = extractStuff("<meta name=\"title\" content=\"",28)
        println("!!! Detta är titeln : $title")
        nextBtn.visibility = View.VISIBLE
        nextScreen(imageUrl, title)
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