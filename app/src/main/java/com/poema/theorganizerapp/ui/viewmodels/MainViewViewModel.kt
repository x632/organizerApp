package com.poema.theorganizerapp.ui.viewmodels

import android.service.autofill.Transformation
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap

import com.google.firebase.auth.FirebaseAuth
import com.poema.theorganizerapp.models.EntireCategory
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.models.VideosGlobal.videosGlobal
import com.poema.theorganizerapp.repositories.MainRepository
import com.poema.theorganizerapp.repositories.MainRepository.Companion.listenerActivated
import com.poema.theorganizerapp.repositories.Repository
import dagger.hilt.android.lifecycle.HiltViewModel



import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

@HiltViewModel
class MainViewViewModel @Inject constructor(
    private val repo: MainRepository,

    ) : ViewModel() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var videos = mutableListOf<Video>()
    private var uid: String = ""
    var allGroups = MutableLiveData<MutableList<EntireCategory>>()
    var allGroups1 = mutableListOf<EntireCategory>()
    var sortingAlphabetically :Boolean = false
    var vidsFromListener = repo.getLiveVid()
    var fromListener : LiveData<MutableList<EntireCategory>> = Transformations.switchMap(vidsFromListener){
    sortVideos(it)}


    init{
            println("!!!   STARTED LISTENER")
            val currentUser = auth.currentUser
            currentUser?.let {
                uid = auth.currentUser!!.uid
            }
            repo.firestoreListener(uid)
    }

   fun sortVideos(vids: List<Video>): MutableLiveData<MutableList<EntireCategory>> {
        val list : MutableList<Video> = mutableListOf()
        for(video in vids) {
            list.add(video)
        }
        val list2 = doSorting(list)
        allGroups.value = list2
        return allGroups
    }

/*
    fun getVideos(internetConnection:Boolean) {

        val currentUser = auth.currentUser
        currentUser?.let {
            uid = auth.currentUser!!.uid
        }
        repo.firestoreListener(uid)
        if (internetConnection) {
            viewModelScope.launch {
                videos = mutableListOf()
                videosGlobal = mutableListOf()

                try {
                    val documents = repo.getGroupsFromFirestore(uid)

                    for (document in documents) {
                        val temp = document.toObject(Video::class.java)
                        if (temp != null) {
                            videos.add(temp)
                            videosGlobal.add(temp)
                        }
                    }
                    repo.createCache(videos)
                    doSorting(videos)
                    if(sortingAlphabetically){
                        sortWithinGroups(allGroups1)
                    }
                    else{
                        sortWithDateAdded(allGroups1)
                    }
                        withContext(Main) {
                        allGroups.value = allGroups1
                    }
                } catch (e: Exception) {
                    e.message?.let { println("!!! $it") }
                }
            }
        }
        else {
            prepGetFromCache()
        }
    }*/

    private fun prepGetFromCache() {
        CoroutineScope(Dispatchers.IO).launch {
            videos = repo.getFromCache()
            withContext(Main){
                doSorting(videos)
                allGroups.value = allGroups1
            }
        }
    }

    //initial sortering in i grupper efter titel 
    fun doSorting(videos: MutableList<Video>): MutableList<EntireCategory>{
       val existingTitles = mutableListOf<String>()
       for (i in 0 until videos.size) {
           for (j in 0 until videos.size) {
               if (!existingTitles.contains(videos[j].groupTitle)) {
                   if (videos[i].groupTitle == videos[j].groupTitle) {
                       existingTitles.add(videos[j].groupTitle!!)
                   }
               }
           }
       }
       val sortedExistingTitles = sortAlphabetically(existingTitles)

       //sortera in videos beroende på grupptitel
       var vids2 = mutableListOf<Video>()
       allGroups1 = mutableListOf()//måste tömmas såhär
       for (i in 0 until sortedExistingTitles.size) {
           for (j in 0 until videos.size) {
               if (videos[j].groupTitle == sortedExistingTitles[i]) {
                   vids2.add(videos[j])
               }
           }
           //skapa hela kategoriobjekten med listorna av tillhörande videos
           allGroups1.add(EntireCategory(sortedExistingTitles[i], vids2))
           vids2 = mutableListOf() //obs! - går inte att tömma med clear!!
       }
        return allGroups1
    }

    private fun sortWithinGroups(entireGroups : MutableList<EntireCategory>) {

    var tempGroup = mutableListOf<String>()
        val tempGroup2 = mutableListOf<Video>()
        for (i in 0 until entireGroups.size) {
            for (item in entireGroups[i].categoryItems) {
               tempGroup.add(item.title!!)
                tempGroup2.add(item)
                }
            val sortedTitles = sortAlphabetically(tempGroup)
            val sortedVideos = sortVideosAccordingToTitles(sortedTitles,tempGroup2)
            tempGroup = mutableListOf<String>()
            entireGroups[i].categoryItems = sortedVideos
            }

        allGroups1 = entireGroups
    }

    private fun sortVideosAccordingToTitles(sortedTitleStrings : MutableList<String>,VideosInGroup:MutableList<Video>):List<Video> {
        val newListOfVids = mutableListOf<Video>()
        for ( title in sortedTitleStrings){
            for (video in VideosInGroup){
                if (video.title!! == title){
                        newListOfVids.add(video)
                }
            }
        }
        return newListOfVids
    }

    private fun sortWithDateAdded(entireGroups : MutableList<EntireCategory>) {
        
        var tempGroup = mutableListOf<String>()
        val tempGroup2 = mutableListOf<Video>()
        for (i in 0 until entireGroups.size) {
            for (item in entireGroups[i].categoryItems) {
                tempGroup.add(item.dateCreated!!)
                tempGroup2.add(item)
            }
            val sortedDates = sortAlphabetically(tempGroup)
            val sortedVideos = sortVideosAccordingToDates(sortedDates,tempGroup2)
            tempGroup = mutableListOf()
            entireGroups[i].categoryItems = sortedVideos
        }

        var tempGroup3= mutableListOf<Video>()
        for (i in 0 until entireGroups.size){
            for (n in (entireGroups[i].categoryItems.size-1)downTo 0) {
                tempGroup3.add(entireGroups[i].categoryItems[n])

                }

            entireGroups[i].categoryItems = tempGroup3
            tempGroup3 = mutableListOf()
        }
        //skriver ut för att dubbelkolla att sorteringen är rätt

        allGroups1 = entireGroups


    }

    private fun sortVideosAccordingToDates(sortedDateStrings : MutableList<String>,VideosInGroup:MutableList<Video>):List<Video> {
        val newListOfVids = mutableListOf<Video>()
        for ( date in sortedDateStrings){
            for (video in VideosInGroup){
                if (video.dateCreated!! == date){
                    newListOfVids.add(video)
                }
            }
        }
        return newListOfVids
    }

    private fun sortAlphabetically(titles: MutableList<String>):MutableList<String>{
        val lowerCaseArray  = mutableListOf<String>()
        //gör om till bara små bokstäver
        titles.forEach { title ->
            var title2  = ""
                for (t in title) {
                    title2 += when {
                        t.isUpperCase() -> t.toLowerCase()
                        else -> t
                    }
                }
            lowerCaseArray.add(title2)
            }
        // sortera arrayn med små bokstäver
        var Swapped = true
        while(Swapped){
            Swapped = false
            for(i in 0 until titles.size-1){
                if(lowerCaseArray[i] > lowerCaseArray[i+1]){
                    val temp = lowerCaseArray[i]
                    lowerCaseArray[i] = lowerCaseArray[i+1]
                    lowerCaseArray[i + 1] = temp
                    Swapped = true
                }
            }
        }
        // Skapa sorterad array som innehåller ursprungliga versaler och gemener
        // i enlighet med sorterade arrayn med små bokstäver
        val upperCaseArray  = mutableListOf<String>()
        lowerCaseArray.forEach(){ lC ->
            titles.forEach() {title ->
                    if (lC.equals(title, true)) {
                        upperCaseArray.add(title)
                    }
            }
        }
        //returnera färdigsorterat
        return upperCaseArray
    }

    fun getList(): MutableLiveData<MutableList<EntireCategory>> {
        return allGroups
    }


}




