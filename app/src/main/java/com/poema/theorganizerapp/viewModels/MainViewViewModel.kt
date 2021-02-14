package com.poema.theorganizerapp.viewModels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.models.EntireCategory
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.models.VideosGlobal

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
                for (document in documents) {
                    val temp = document!!.toObject(Video::class.java)
                    VideosGlobal.videosGlobal.clear()
                    videos.add(temp)
                    VideosGlobal.videosGlobal.add(temp)
                }
                // val immutableVideos = Collections.unmodifiableList(videos)
                val existingTitles = mutableListOf<String>()

                //sortera fram vilka grupptitlar som finns
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
                for (i in 0 until sortedExistingTitles.size) {
                    for (j in 0 until videos.size) {
                        if (videos[j].groupTitle == sortedExistingTitles[i]) {
                            vids2.add(videos[j])
                        }
                    }

                    allGroups1.add(EntireCategory(sortedExistingTitles[i], vids2))
                    vids2 = mutableListOf() //obs! - går inte att tömma med clear!!
                }
                allGroups.value = allGroups1
            }
            .addOnFailureListener { exception ->
                println("!!! Error getting users: $exception.message")
            }
    }

    private fun sortAlphabetically(titles: MutableList<String>):MutableList<String>{
        var lastWasSwapped = true
        while(lastWasSwapped){
            lastWasSwapped = false
            for(i in 0 until titles.size-1){
                if(titles[i] > titles[i+1]){
                    val temp = titles[i]
                    titles[i] = titles[i+1]
                    titles[i + 1] = temp
                    lastWasSwapped = true
                }
            }
        }
        return titles
    }

    fun getList(): MutableLiveData<MutableList<EntireCategory>> {
        return allGroups
    }
}




