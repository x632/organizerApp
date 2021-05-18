package com.poema.theorganizerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.R.layout.activity_add_video_props
import com.poema.theorganizerapp.models.Video
import com.poema.theorganizerapp.viewModels.AddVideoPropsViewModel
import kotlinx.android.synthetic.main.activity_add_video_props.*
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class AddVideoProps : AppCompatActivity() {

    private var url : String? = null
    private var groupTitle = ""
    private lateinit var viewModel: AddVideoPropsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_add_video_props)

        viewModel = ViewModelProvider(this@AddVideoProps).get(AddVideoPropsViewModel::class.java)

        url = intent.getStringExtra("url")
        var title = intent.getStringExtra("title")
        val imageUrl = intent.getStringExtra("imageUrl")


        viewModel.getGroupsFromFirestore()
        setGroupTitleListObserver()
        if (title != null){
        editTextTitle.hint = title
        }
        setIsSavedObserver()
        saveBtn.setOnClickListener(){
            if (editTextNewGroup.text.toString() != ""){
            groupTitle = editTextNewGroup.text.toString()
            }
            if (editTextTitle.text.toString()!=""){
                title = editTextTitle.text.toString()
            }
            val myDate = getCurrentDateTime()
            val timeStamp = myDate.toString("yyyy-MM-dd HH:mm:ss.SSSSSS")
            val tempObject = Video(timeStamp,title!!,url!!, imageUrl!!,"tempId",groupTitle,0)
            viewModel.saveToFirestore(tempObject)
        }

    }
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun setGroupTitleListObserver() {
        viewModel.getGroupTitles().observe(this@AddVideoProps, { t ->
            val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,t)
            mySpinner.adapter = adapter
            mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    groupTitle = adapterView?.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        })
    }

    private fun setIsSavedObserver(){
        viewModel.getIsSaved().observe(this@AddVideoProps,{ t ->
            if (t) nextScreen()
        })
    }


    private fun nextScreen() {
        Toast.makeText(this,"the video has been added to you database.",Toast.LENGTH_LONG).show()
        val intent = Intent(this, MainView::class.java)
        startActivity(intent)
    }
}