package com.poema.theorganizerapp.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.models.Video
import kotlinx.coroutines.tasks.await

interface Repository {




    fun getFromCache(): MutableList<Video>

    suspend fun getGroupsFromFirestore(uid:String): List<DocumentSnapshot>

    fun createCache(videos: MutableList<Video>)

    fun saveToFirestore(video: Video, id:String)

    fun updateId(docId: String, id: String)

    fun getYouTubeVideo(url:String): MutableLiveData<String>

    fun removeVideo(uid: String, docId: String?)

    fun login(email:String, password: String)

    fun firestoreListener(uid:String)

    fun getLiveVid():MutableLiveData<List<Video>>


}