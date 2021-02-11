package com.poema.theorganizerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.R.layout.activity_add_video_props
import com.poema.theorganizerapp.dataClasses.Video
import kotlinx.android.synthetic.main.activity_add_video_props.*

class AddVideoProps : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var uid:String = ""
    private var url : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_add_video_props)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            uid = auth.currentUser!!.uid
        }
        url = intent.getStringExtra("url")
        var title = intent.getStringExtra("title")
        val imageUrl = intent.getStringExtra("imageUrl")
        println("Titeln: $title")

        if (title != null){
        editTextTitle.hint = title
        }

        saveBtn.setOnClickListener(){
            if (editTextTitle.text.toString()!=""){
                title = editTextTitle.text.toString()
            }
            val searchTerm1 = editTextTerm1.text.toString()
            val searchTerm2 = editTextTerm2.text.toString()
            val tempObject = Video(title!!,url!!, imageUrl!!,searchTerm1,searchTerm2,"tempId")
            saveToFirestore(tempObject)
        }
    }

    private fun saveToFirestore(video: Video) {
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
                nextScreen()
            }
            .addOnFailureListener {
                println("!!! video was not saved")
            }
    }

    private fun nextScreen() {
        val intent = Intent(this, MainView::class.java)
        startActivity(intent)
    }
}