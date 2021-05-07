package com.poema.theorganizerapp.viewModels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.data.local.Video

class AddVideoPropsViewModel : ViewModel() {

    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var uid:String = ""
    private val videos = mutableListOf<Video>()
    private val groupTitles = mutableListOf<String>()
    private var liveTitles = MutableLiveData<MutableList<String>>()
    private var isSaved = MutableLiveData(false)

    fun getGroupsFromFirestore() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
        db.collection("users").document(uid).collection("videos")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val temp = document!!.toObject(Video::class.java)
                    videos.add(temp)
                }
                for (i in 0 until videos.size) {
                    for (j in 0 until videos.size) {
                        if (!groupTitles.contains(videos[j].groupTitle)) {
                            if (videos[i].groupTitle == videos[j].groupTitle) {
                                groupTitles.add(videos[j].groupTitle)
                            }
                        }
                    }
                }
                liveTitles.value = groupTitles
            }
            .addOnFailureListener { exception ->
                println("!!! Error getting users: ${exception.message}")
            }
    }

    fun saveToFirestore(video: Video) {
        db.collection("users").document(uid)
            .collection("videos").add(video)
            .addOnSuccessListener { documentReference ->
                println("!!! video was saved")
                val docId = documentReference.id
                println("id:t :$docId")
                updateId(docId)
            }
            .addOnFailureListener {
                println("!!! video was not saved")
            }
    }

    private fun updateId(docId: String) {
        db.collection("users").document(uid)
            .collection("videos").document(docId).update("docId",docId)
            .addOnSuccessListener {
                println("!!! video id was updated")
                isSaved.value = true
            }
            .addOnFailureListener {
                println("!!! video was not saved! Exception: $it")
            }
    }

    fun getGroupTitles():MutableLiveData<MutableList<String>>{
        return liveTitles
    }

    fun getIsSaved():MutableLiveData<Boolean>{
        return isSaved
    }
}