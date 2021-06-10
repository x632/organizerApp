package com.poema.theorganizerapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.poema.theorganizerapp.models.Video

@Dao
interface VideoDao {
    @Query("SELECT * FROM Video")
    fun getAllVideos(): List<Video>

    @Insert
    fun insert(video: Video):Long

    @Delete
    fun delete(video: Video)

    @Query("DELETE FROM Video")
    fun deleteAll()

    @Query("SELECT * FROM Video WHERE docId = :uid" )
    fun findVideoByUid(uid: String) : Video

}
