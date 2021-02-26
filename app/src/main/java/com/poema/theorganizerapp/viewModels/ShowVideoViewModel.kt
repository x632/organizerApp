package com.poema.theorganizerapp.viewModels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ShowVideoViewModel : ViewModel() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val currentUser = auth.currentUser
    private var uid:String =""
    private var isRemoved = MutableLiveData(false)

    fun removeVideo(docId: String?) {
        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
        db.collection("users").document(uid).collection("videos").document(docId!!).delete()
            .addOnSuccessListener{
                isRemoved.value=true
            }
            .addOnFailureListener {

            }
    }
    fun getIsRemoved():MutableLiveData<Boolean>{
        return isRemoved
    }

}