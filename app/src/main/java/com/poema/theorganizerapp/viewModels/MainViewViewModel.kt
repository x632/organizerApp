package com.poema.theorganizerapp.viewModels


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.poema.theorganizerapp.Repository
import com.poema.theorganizerapp.database.AppDatabase
import com.poema.theorganizerapp.models.EntireCategory
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.database.VideosRoom
import com.poema.theorganizerapp.models.VideosGlobal.videosGlobal
import com.poema.theorganizerapp.utils.Utility.isInternetAvailable


import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class MainViewViewModel(val context:Context) : ViewModel() {

    private var roomDb: AppDatabase = VideosRoom.getInstance(context)
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var videos = mutableListOf<Video>()
    private var uid: String = ""
    var allGroups = MutableLiveData<MutableList<EntireCategory>>()
    var allGroups1 = mutableListOf<EntireCategory>()
    var sortingAlphabetically :Boolean = false
    private val repository = Repository()

    fun getVideos() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
        if (context.isInternetAvailable()) {
            CoroutineScope(Dispatchers.IO).launch {
                videos = mutableListOf()
                videosGlobal = mutableListOf()

                try {
                    val documents = repository.getGroupsFromFirestore(uid)

                    for (document in documents) {
                        val temp = document.toObject(Video::class.java)
                        if (temp != null) {
                            videos.add(temp)
                            videosGlobal.add(temp)
                        }
                    }
                    withContext(Main) {
                    createCache()
                    doSorting(videos)
                    if(sortingAlphabetically){
                        sortWithinGroups(allGroups1)
                    }
                    else{
                        sortWithDateAdded(allGroups1)
                    }
                        allGroups.value = allGroups1
                    }
                } catch (e: Exception) {
                    e.message?.let { println("!!! $it") }
                }
            }
        }
        else {
            getFromCache()
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

    private fun createCache(){
        val job1 : CompletableJob = Job()
        CoroutineScope(Dispatchers.IO + job1).launch {
            roomDb.clearAllTables()
            for (i in 0 until videos.size) {
                val uid = "${videos[i].docId}"
                val numb = roomDb.videoDao().findVideoByUid(uid)
                if (numb == null) { //IDE:t säger fel här. Blir visst null!
                    println("!!! numb är visst : $numb!")
                    roomDb.videoDao().insert(videos[i])
                }
            }

            job1.cancel() //canclar för säkerhets skull jobbet p g a risk för minnesläckor i viewmodel
        }
        println("!!! the Job :$job1")
    }

   private fun getFromCache(){
       val job2 : CompletableJob = Job()
       println("!!! the Job :$job2")
       videos = mutableListOf()
       videosGlobal = mutableListOf()
       CoroutineScope(Dispatchers.IO + job2).launch {
           videos = roomDb.videoDao().getAllVideos() as MutableList<Video>
           for (video in videos){
               println("!!! Hämtat från cache : ${video.title} från cache")
           }
           withContext(Main){
               doSorting(videos)
               allGroups.value = allGroups1
               job2.cancel()
               println("!!! the Job :$job2")
           }
       }
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
            tempGroup = mutableListOf<String>()
            entireGroups[i].categoryItems = sortedVideos
        }
        //reversera arrayn
        var tempGroup3= mutableListOf<Video>()
        for (i in 0 until entireGroups.size){
            for (n in (entireGroups[i].categoryItems.size-1)downTo 0) {
                tempGroup3.add(entireGroups[i].categoryItems[n])

                }

            entireGroups[i].categoryItems = tempGroup3
            tempGroup3 = mutableListOf<Video>()
        }
        //skriver ut för att dubbelkolla att sorteringen är rätt
        for (i in  0 until entireGroups.size){
            println("!!! ${entireGroups[i].categoryTitle}")
            for (j in 0 until entireGroups[i].categoryItems.size) {
                println("!!! ITEM: ${entireGroups[i].categoryItems[j].title} DATE : ${entireGroups[i].categoryItems[j].dateCreated}")
            }
        }
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




