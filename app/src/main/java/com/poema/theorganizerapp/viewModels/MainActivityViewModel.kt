package com.poema.theorganizerapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.poema.theorganizerapp.repository.Repository


class MainActivityViewModel : ViewModel(){

    private var _url : MutableLiveData<String> = MutableLiveData()

    private val repository = Repository()
    private var youTubeHtml : MutableLiveData<String> = Transformations.switchMap(_url) {
        repository.getYouTubeVideo(it)} as MutableLiveData<String>

    fun setUrl(url:String){
        if (_url.value != url){
            _url.value = url
        }
    }

    fun getYouTubeString():MutableLiveData<String>{
        return youTubeHtml
    }

    //Nedan grävs titeln och thumbnailbild-Urlen fram ur HTML-strängen
    private fun matchDetails(inputString: String, whatToFind: String, startIndex: Int): Int {
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
        return if (title.contains("&amp;")){
            val cleanedTitle = removeHTMLAndChar(title)
            cleanedTitle
        } else {
            title
        }

    }
    private fun removeHTMLAndChar(title: String):String {
      val delimiter= "&amp;"
        val parts = title.split(delimiter)
        var newTitle = ""
        parts.forEach {part ->
            newTitle += "$part&"
        }
        return newTitle.dropLast(1)
    }

}