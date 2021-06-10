package com.poema.theorganizerapp.viewModels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.poema.theorganizerapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class ShowVideoViewModel @Inject constructor(
    private val repo : Repository): ViewModel() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private var uid:String =""


    fun removeVideo(docId: String?) {
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
        repo.removeVideo(uid,docId)
    }

    fun getIsRemoved():MutableLiveData<Boolean>{
        return repo.isRemoved
    }

}