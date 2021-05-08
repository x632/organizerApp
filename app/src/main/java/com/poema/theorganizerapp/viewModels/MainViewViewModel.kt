package com.poema.theorganizerapp.viewModels


import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.poema.theorganizerapp.data.local.AppDatabase
import com.poema.theorganizerapp.data.local.EntireCategory
import com.poema.theorganizerapp.data.local.Video
import com.poema.theorganizerapp.data.local.VideosRoom
import com.poema.theorganizerapp.models.VideosGlobal.videosGlobal
import kotlinx.coroutines.*

class MainViewViewModel(context:Context) : ViewModel() {

    private var job: CompletableJob? = null
    private var roomDb: AppDatabase = VideosRoom.getInstance(context)
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore= FirebaseFirestore.getInstance()
    private var videos = mutableListOf<Video>()
    private var uid: String = ""
    private var allGroups = MutableLiveData<MutableList<EntireCategory>>()
    private var allGroups1 = mutableListOf<EntireCategory>()


    fun getVideos() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
        db.collection("users").document(uid).collection("videos")
            .get()
            .addOnSuccessListener { documents ->
                videos = mutableListOf()
                videosGlobal = mutableListOf()
                for (document in documents) {
                    val temp = document!!.toObject<Video>()         //(Video::class.java)
                    videos.add(temp)
                    videosGlobal.add(temp)
                }
                val existingTitles = mutableListOf<String>()
                createCache()
                //sortera fram en av varje grupptitel.
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
                allGroups.value = allGroups1 //ge grupperna till livedatat
            }
            .addOnFailureListener { exception ->
                println("!!! Error getting users: $exception.message")
            }
    }

    private fun createCache(){
        job = Job()
        println("!!! the Job :$job")
        CoroutineScope(Dispatchers.IO + job!!).launch {
            roomDb.clearAllTables()
            //roomDb.videoDao().deleteAll()
            for (i in 0 until videos.size) {
                val uid = "${videos[i].docId}"
                val numb = roomDb.videoDao().findVideoByUid(uid)
                if (numb!= null){
                println("!!!Denna video kommmer från room: ${numb.title}")
                }
                if (numb == null) {
                    val savedVideoNum = roomDb.videoDao().insert(videos[i])
                    println("!!! Video: ${videos[i].title} with number $savedVideoNum has been saved in cache.")
                    //    println("!!! Arraysize is :${videos.size}")
                } else {
                    println("!!! Video: ${videos[i].docId} is already in cache number is $i")
                   // println("!!! Arraysize is :${videos.size}")
                }
            }
           var roomVideos: MutableList<Video> = mutableListOf()
            roomVideos = roomDb.videoDao().getAllVideos() as MutableList<Video>
            for (video in roomVideos){
                println("!!!Videos total : ${video.docId}")
            }
            job!!.cancel()
            println("!!! the Job :$job")
        }
    }

   private fun getFromCache(){


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




