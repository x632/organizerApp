package com.poema.theorganizerapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.type.Date
import java.util.*

@Entity
class Video(
    val dateCreated: String? = null,
    val title:String? = null,
    val url:String? = null,
    val imageUrl:String?= null,
    val docId: String?= null,
    val groupTitle : String?= null,
    @PrimaryKey (autoGenerate = true)
    val roomId : Long = 0
)

