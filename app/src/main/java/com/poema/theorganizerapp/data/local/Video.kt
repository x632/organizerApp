package com.poema.theorganizerapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "videos")
data class Video(
    val title:String = "",
    val url:String = "",
    val imageUrl:String = "",
    val docId: String = "",
    val groupTitle : String = "",
    @PrimaryKey(autoGenerate = true)
    val roomId : Int = 0
)
