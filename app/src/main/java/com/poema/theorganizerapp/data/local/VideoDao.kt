package com.poema.theorganizerapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

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

    @Query("SELECT * FROM Video WHERE docId = :uid LIMIT 1" )
    fun findVideoByUid(uid: String) : Video

}