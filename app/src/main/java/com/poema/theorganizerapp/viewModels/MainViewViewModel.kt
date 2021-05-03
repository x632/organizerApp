package com.poema.theorganizerapp.viewModels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.models.EntireCategory
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.models.VideosGlobal.videosGlobal

class MainViewViewModel: ViewModel() {

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
                    val temp = document!!.toObject(Video::class.java)
                    videos.add(temp)
                    videosGlobal.add(temp)
                }
                val existingTitles = mutableListOf<String>()

                //sortera fram en av varje grupptitel.
                for (i in 0 until videos.size) {
                    for (j in 0 until videos.size) {
                        if (!existingTitles.contains(videos[j].groupTitle)) {
                            if (videos[i].groupTitle == videos[j].groupTitle) {
                                existingTitles.add(videos[j].groupTitle)
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




