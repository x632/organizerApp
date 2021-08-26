package com.poema.theorganizerapp.ui.viewmodels



import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.poema.theorganizerapp.repositories.MainRepository
import com.poema.theorganizerapp.models.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

@HiltViewModel
class AddVideoPropsViewModel @Inject constructor(
    private val mainRepository : MainRepository) : ViewModel() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var uid:String = ""
    private val videos = mutableListOf<Video>()
    private val groupTitles = mutableListOf<String>()
    private var liveTitles = MutableLiveData<MutableList<String>>()




    fun getGroupsFromFirestore() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
        viewModelScope.launch{
            try {
                    val documents = mainRepository.getGroupsFromFirestore(uid)

                for (document in documents) {
                    val temp = document.toObject(Video::class.java)
                    if (temp != null){
                    videos.add(temp)
                    }
                }
                for (i in 0 until videos.size) {
                    for (j in 0 until videos.size) {
                        if (!groupTitles.contains(videos[j].groupTitle)) {
                            if (videos[i].groupTitle == videos[j].groupTitle) {
                                groupTitles.add(videos[j].groupTitle!!)
                            }
                        }
                    }
                }
                withContext(Main){
                liveTitles.value = groupTitles}
                } catch (e: Exception) {
                    e.message?.let { println("!!! $it") }
                }
        }
    }

    fun saveToFirestore(video: Video) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
       mainRepository.saveToFirestore(video,uid)
    }

    fun getGroupTitles():MutableLiveData<MutableList<String>>{
        return liveTitles
    }

    fun getIsSaved():MutableLiveData<Boolean>{
        return mainRepository.isSaved
    }
}