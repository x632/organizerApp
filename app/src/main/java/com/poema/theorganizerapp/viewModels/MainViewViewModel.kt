package com.poema.theorganizerapp.viewModels


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.poema.theorganizerapp.data.local.AppDatabase
import com.poema.theorganizerapp.models.EntireCategory
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.database.VideosRoom
import com.poema.theorganizerapp.models.VideosGlobal.videosGlobal
import com.poema.theorganizerapp.utils.Utility.isInternetAvailable


import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class MainViewViewModel(context:Context) : ViewModel() {

    private var job: CompletableJob? = null
    private var roomDb: AppDatabase = VideosRoom.getInstance(context)
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore= FirebaseFirestore.getInstance()
    private var videos = mutableListOf<Video>()
    private var uid: String = ""
    var allGroups = MutableLiveData<MutableList<EntireCategory>>()
    var allGroups1 = mutableListOf<EntireCategory>()
    private var context : Context = context

    fun getVideos() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
        if (context.isInternetAvailable()) {

          db.collection("users").document(uid).collection("videos")
              .get()
              .addOnSuccessListener { documents ->
                  videos = mutableListOf()
                  videosGlobal = mutableListOf()
                  for (document in documents) {
                      val temp = document!!.toObject<Video>() //(Video::class.java)
                      videos.add(temp)
                      videosGlobal.add(temp)
                  }
                  createCache()
                  doSorting(videos)
                  allGroups.value = allGroups1 //ge grupperna till livedatat

              }
              .addOnFailureListener { exception ->
                  println("!!! Error getting users: $exception.message")
              }
        }
        else {
            getFromCache()
        }
    }

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
       //allGroups.value = allGroups1 //ge grupperna till livedatat
        return allGroups1
    }

    private fun createCache(){
        job = Job()
        println("!!! the Job :$job")
        CoroutineScope(Dispatchers.IO + job!!).launch {
            roomDb.clearAllTables()
            for (i in 0 until videos.size) {
                val uid = "${videos[i].docId}"
                // Dubbelkollar nedan ifall den asynkrona tömningen inte skulle ha hunnit ske, vilket den ska ha gjort i och med coroutine!
                // hängslen och livrem.
                val numb = roomDb.videoDao().findVideoByUid(uid)
                if (numb == null) { //numb blir i högsta grad null även om typen inte är nullbar!! IDE:t har fel här.
                    println("!!! numb är visst : $numb!")
                    roomDb.videoDao().insert(videos[i])
                }
            }
           var roomVideos: MutableList<Video> = mutableListOf()
            roomVideos = roomDb.videoDao().getAllVideos() as MutableList<Video>
            var i = 0
            for (video in roomVideos){
                i ++
                println("!!!Video in cache: ${video.title} nummer $i Grouptitle: ${video.groupTitle}")
            }
            job!!.cancel() //canclar för säkerhets skull jobbet p g a risk för minnesläckor i viewmodel
            println("!!! the Job :$job")
        }
    }

   private fun getFromCache(){
       job = Job()
       println("!!! the Job :$job")
       videos = mutableListOf()
       videosGlobal = mutableListOf()
       CoroutineScope(Dispatchers.IO + job!!).launch {



           videos = roomDb.videoDao().getAllVideos() as MutableList<Video>
           for (video in videos){
               println("!!! Hämtat från cache : ${video.title} från cache")
           }

           println("!!! the Job :$job")
           withContext(Main){
               doSorting(videos)
               job!!.cancel()
           }
       }

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




