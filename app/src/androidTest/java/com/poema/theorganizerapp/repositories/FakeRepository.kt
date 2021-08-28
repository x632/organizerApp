package com.poema.theorganizerapp.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.poema.theorganizerapp.models.Video

class FakeRepository():Repository {
    override fun getFromCache(): MutableList<Video> {
        return mutableListOf()
    }

    override fun createCache(videos: MutableList<Video>) {
        // do nothing
    }

    override suspend fun getGroupsFromFirestore(uid: String): List<DocumentSnapshot> {
        return listOf()
    }

    override fun saveToFirestore(video: Video, id: String) {
       //do nothing
    }

    override fun updateId(docId: String, id: String) {
        //do nothing
    }

    override fun getYouTubeVideo(url: String): MutableLiveData<String> {
        val theString = MutableLiveData<String>()
        return theString
    }

    override fun removeVideo(uid: String, docId: String?) {
        //do nothing
    }

    override fun login(email: String, password: String) {
        //do nothing
    }

    override fun firestoreListener(uid: String) {
        //do nothing
    }

    override fun getLiveVid(): MutableLiveData<List<Video>> {
        val a = MutableLiveData<List<Video>>()
       return a
    }
}