package com.poema.theorganizerapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.dataClasses.VideosGlobal
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException



class MainActivity : AppCompatActivity() {

    var uriUrl: Uri = Uri.parse("http://google.com/")
    private val client = OkHttpClient()
    lateinit var button: Button
    private lateinit var job1: CompletableJob
    private var words: String = ""
    private lateinit var spinner : ProgressBar
    private var url : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            println("!!! länken : $url")
            webScratch()
        }
    }
    fun webScratch() {
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
    }

    fun matchDetails(inputString: String, whatToFind: String, startIndex: Int = 0): Int {
        val matchIndex = inputString.indexOf(whatToFind, startIndex)
        return matchIndex
    }

    private fun extractStuff(str:String, index2:Int):String{
        val index = matchDetails(words,str,0)
        val str = words.slice(index+index2..(index+200))
        println("!!! Detta är strängen som följer:  $str")
        var delimiter = "\""
        val parts = str.split(delimiter)
        val whatWasAskedFor = parts[0]
        println("!!! Klippt vid citationstecken : ${parts[0]}")
        return whatWasAskedFor
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