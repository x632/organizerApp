package com.poema.theorganizerapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.IOException
import okhttp3.OkHttpClient

class MainActivityViewModel : ViewModel(){

    private val client = OkHttpClient()
    private var _url : MutableLiveData<String> = MutableLiveData()


    private var words : MutableLiveData<String> = Transformations.switchMap(_url) {
        webScratch(it)} as MutableLiveData<String>


    fun webScratch(url:String):MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO ).launch {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val str: String = response.body!!.string()
                withContext(Main){mutableLiveData.value = str}
            }
        }
        return mutableLiveData
    }
    fun setUrl(url:String){
        _url.value = url
    }
    fun getWords():MutableLiveData<String>{
        return words
    }
}