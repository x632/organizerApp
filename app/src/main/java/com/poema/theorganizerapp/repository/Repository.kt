package com.poema.theorganizerapp.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.database.AppDatabase
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.models.VideosGlobal.videosGlobal
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.tasks.await
import okhttp3.Request
import java.io.IOException
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Provider

class Repository @Inject constructor(private val dbRoom: Provider<AppDatabase>){


    private val dao = dbRoom.get().videoDao()
    private val client = OkHttpClient()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var message = MutableLiveData<String>()
    var isSignedIn = MutableLiveData<Boolean>()
    var accountCreated = MutableLiveData<Boolean>()
    var isSaved = MutableLiveData(false)
    var isRemoved = MutableLiveData(false)

    fun createCache(videos: MutableList<Video>){
        val job1 : CompletableJob = Job()
        CoroutineScope(Dispatchers.IO + job1).launch {

            for (i in 0 until videos.size) {
                val uid = "${videos[i].docId}"
                val numb :Video = dao.findVideoByUid(uid)
                if(numb!= null){println("!!! finns i databasen: ${numb.docId}")}
                else{
                    val number = dao.insert(videos[i])
                    println("!!! ${videos[i].title} sparades i cache")
                }
            }

            job1.cancel()
        }
        println("!!! the Job :$job1")
    }

    fun getFromCache(): MutableList<Video> {
        val job2: CompletableJob = Job()
        println("!!! the Job :$job2")
        var videos = mutableListOf<Video>()
        videosGlobal = mutableListOf()
        videos = dao.getAllVideos() as MutableList<Video>
        for (video in videos) {
            println("!!! Hämtat från cache : ${video.title} från cache")
        }
        return videos
    }

    fun login(email:String, password: String) {
        if (email == ""  || password == ""){
            message.value = "Authentication failed - you need to fill in both email and password"
        }
        else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        message.value=""
                        isSignedIn.value=true
                    } else {
                        message.value = ("signInWithEmail:failure - ${task.exception}")

                    }
                }
        }
    }

    fun createAccount(email:String, password:String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    println("createUserWithEmail:success")
                    accountCreated.value = true

                } else {
                    val eMess = "${task.exception}"
                    val errMess = eMess.substringAfter(":")
                    message.value = "Authentication failed - $errMess"
                    println(errMess)
                }
            }
    }

    suspend fun getGroupsFromFirestore(uid:String): List<DocumentSnapshot> {
        val snapshot = db.collection("users").document(uid).collection("videos").get().await()
        return snapshot.documents
    }

    fun saveToFirestore(video: Video, id:String) {
        db.collection("users").document(id)
            .collection("videos").add(video)
            .addOnSuccessListener { documentReference ->
                println("!!! video was saved")
                val docId = documentReference.id
                println("id:t :$docId")
                updateId(docId,id)
            }
            .addOnFailureListener {
                println("!!! video was not saved")
            }
    }

    private fun updateId(docId: String, id: String) {
        db.collection("users").document(id)
            .collection("videos").document(docId).update("docId",docId)
            .addOnSuccessListener {
                println("!!! video id was updated")
                isSaved.value = true
            }
            .addOnFailureListener {
                println("!!! video was not saved! Exception: $it")
            }
    }

    fun getYouTubeVideo(url:String):MutableLiveData<String> {
        val liveData = MutableLiveData<String>()

        CoroutineScope(Dispatchers.IO ).launch {

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val str = response.body!!.string()

                withContext(Main) {
                    liveData.value = str
                }
            }
        }
        return liveData
    }

    fun removeVideo(uid: String, docId: String?) {

        db.collection("users").document(uid).collection("videos").document(docId!!).delete()
            .addOnSuccessListener{
                isRemoved.value=true
            }
            .addOnFailureListener {
            }
    }
}