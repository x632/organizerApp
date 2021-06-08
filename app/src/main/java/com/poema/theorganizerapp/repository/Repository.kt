package com.poema.theorganizerapp.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.models.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.IOException
import okhttp3.OkHttpClient


class Repository {

    private val client = OkHttpClient()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var message = MutableLiveData<String>()
    var isSignedIn = MutableLiveData<Boolean>()
    var accountCreated = MutableLiveData<Boolean>()
    var isSaved = MutableLiveData(false)
    var isRemoved = MutableLiveData(false)

    fun login(email:String, password: String) {
        if (email == ""  || password == ""){
            message.value = "Authentication failed - you need to fill in both email and password"
            //showMessage(mess)
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
            //avsedd blocking call för att demonstrera coroutines!!
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val str = response.body!!.string()

                withContext(Dispatchers.Main) {
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