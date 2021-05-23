package com.poema.theorganizerapp.viewModels

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.poema.theorganizerapp.activities.AddVideoProps
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.Request
import java.io.IOException
import okhttp3.OkHttpClient

class MainActivityViewModel : ViewModel(){

    private val client = OkHttpClient()
    private var _url : MutableLiveData<String> = MutableLiveData()
    private var youTubeHtml : MutableLiveData<String> = Transformations.switchMap(_url) {
        getVideo(it)} as MutableLiveData<String>


    private fun getVideo(url:String):MutableLiveData<String> {
        val liveData = MutableLiveData<String>()

        CoroutineScope(Dispatchers.IO ).launch {

            val request = Request.Builder()
                .url(url)
                .build()
             //avsedd blocking call!!
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val str = response.body!!.string()

                withContext(Main) { liveData.value = str }
            }
        }
        return liveData
    }

    fun setUrl(url:String){
        if (_url.value != url){
            _url.value = url
        }
    }

    fun getYouTubeString():MutableLiveData<String>{
        return youTubeHtml
    }

    //Nedan grävs titeln och thumbnailbild-Urlen fram ur HTML-strängen
    fun matchDetails(inputString: String, whatToFind: String, startIndex: Int): Int {
        return inputString.indexOf(whatToFind, startIndex)
    }

    fun extractStuff(str:String, index2:Int,data:String):String{
        val index = matchDetails(data,str,0)
        val stri = data.slice(index+index2..(index+200))
        val delimiter = "\""
        val parts = stri.split(delimiter)
        return parts[0]
    }
    fun extractTitle(data:String):String{
        val title = extractStuff("<meta name=\"title\" content=\"",28,data)
        return title
    }

}