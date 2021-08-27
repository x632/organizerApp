package com.poema.theorganizerapp.repositories

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

class MainRepository @Inject constructor(dbRoom: Provider<AppDatabase>): Repository{

    private val dao = dbRoom.get().videoDao()
    private val client = OkHttpClient()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    //private val thisUserId : String? = auth.currentUser?.uid
    var message = MutableLiveData<String>()
    var isSignedIn = MutableLiveData<Boolean>()
    var accountCreated = MutableLiveData<Boolean>()
    var isSaved = MutableLiveData(false)
    var isRemoved = MutableLiveData(false)
    private var liveVids = MutableLiveData<List<Video>>()

    override fun createCache(videos: MutableList<Video>){
        val job1 : CompletableJob = Job()
        CoroutineScope(Dispatchers.IO + job1).launch {

            for (i in 0 until videos.size) {
                val uid = "${videos[i].docId}"
                val numb :Video = dao.findVideoByUid(uid)
                if(numb == null) {val number = dao.insert(videos[i])

                }
            }

            job1.cancel()
        }
        println("!!! the Job :$job1")
    }

    override fun getFromCache(): MutableList<Video> {
        val job2: CompletableJob = Job()
        println("!!! the Job :$job2")
        var videos = mutableListOf<Video>()
        videosGlobal = mutableListOf()
        videos = dao.getAllVideos() as MutableList<Video>

        return videos
    }

    override fun login(email:String, password: String) {

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        message.value="Successfully logged in"
                        isSignedIn.value=true
                    } else {
                        message.value = ("signInWithEmail:failure - ${task.exception}")

                    }
                }

    }

    fun createAccount(email:String, password:String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    message.value= "created user successfully"
                    accountCreated.value = true

                } else {
                    val eMess = "${task.exception}"
                    val errMess = eMess.substringAfter(":")
                    message.value = "Authentication failed - $errMess"
                    println(errMess)
                }
            }
    }

    override suspend fun getGroupsFromFirestore(uid:String): List<DocumentSnapshot> {
        val snapshot = db.collection("users").document(uid).collection("videos").get().await()
        return snapshot.documents
    }

    override fun saveToFirestore(video: Video, id:String) {
        db.collection("users").document(id)
            .collection("videos").add(video)
            .addOnSuccessListener { documentReference ->
                val docId = documentReference.id
                println("id:t :$docId")
                updateId(docId,id)
            }
            .addOnFailureListener {
                println("!!! video was not saved")
            }
    }

    override fun firestoreListener(uid:String) {
        println("!!! Been here!!!!")
        val tempVideoList :MutableList<Video> = mutableListOf()

        db.collection("users").document(uid).collection("videos")
            .addSnapshotListener{ value, e ->
                if (e != null) {
                    println( "!!! Listen failed - $e")
                    return@addSnapshotListener
                }

                for (document in value!!) {
                    val video = document!!.toObject(Video::class.java)
                    tempVideoList.add(video)
                }
                liveVids.value = tempVideoList
                //friendReqAdapter.notifyDataSetChanged()
            }
    }

    override fun updateId(docId: String, id: String) {
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

    override fun getYouTubeVideo(url:String):MutableLiveData<String> {
        val liveData = MutableLiveData<String>()

        CoroutineScope(Dispatchers.IO ).launch {

            val request = Request.Builder()
                .url(url)
                .build()
            //är inom coroutine, kan vara blocking..
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

    override fun removeVideo(uid: String, docId: String?) {

        db.collection("users").document(uid).collection("videos").document(docId!!).delete()
            .addOnSuccessListener{
                isRemoved.value=true
            }
            .addOnFailureListener {
            }
    }


    override fun getLiveVid():MutableLiveData<List<Video>>{
        return liveVids
    }
}