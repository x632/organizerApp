package com.poema.theorganizerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.R.layout.activity_add_video_props
import com.poema.theorganizerapp.models.Video
import kotlinx.android.synthetic.main.activity_add_video_props.*

class AddVideoProps : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var uid:String = ""
    private var url : String? = null
    private val videos = mutableListOf<Video>()
    private val groupTitles = mutableListOf<String>()
    private var groupTitle = ""

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

        getGroupsFromFirestore()



        if (title != null){
        editTextTitle.hint = title
        }

        saveBtn.setOnClickListener(){
            if (editTextNewGroup.text.toString() != ""){
            groupTitle = editTextNewGroup.text.toString()
            }
            if (editTextTitle.text.toString()!=""){
                title = editTextTitle.text.toString()
            }
            val searchTerm1 = editTextTerm1.text.toString()
            val searchTerm2 = editTextTerm2.text.toString()
            val tempObject = Video(title!!,url!!, imageUrl!!,searchTerm1,searchTerm2,"tempId",groupTitle)
            saveToFirestore(tempObject)
        }
    }

/*    fun limitDropDownHeight(mySpinner: Spinner) {
        val popup = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow = popup.get(mySpinner) as ListPopupWindow
        popupWindow.height = (150 * resources.displayMetrics.density).toInt()
    }*/

    private fun getGroupsFromFirestore() {
        db.collection("users").document(uid).collection("videos")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val temp = document!!.toObject(Video::class.java)
                    videos.add(temp)
                }
                for (i in 0 until videos.size) {
                    for (j in 0 until videos.size) {
                        if (groupTitles.contains(videos[j].groupTitle)) {//do nothing
                        } else {
                            if (videos[i].groupTitle == videos[j].groupTitle) {
                                groupTitles.add(videos[j].groupTitle)
                            }
                        }
                    }
                }
                println("!!!!Detta är titlarna: $groupTitles")
                val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,groupTitles)
                mySpinner.adapter = adapter
                mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                       groupTitle = adapterView?.getItemAtPosition(position).toString()

                        //Toast.makeText(this@AddVideoProps,"$groupTitle selected", Toast.LENGTH_SHORT).show()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            }
            .addOnFailureListener { exception ->
                println("!!! Error getting users: ${exception.message}")
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