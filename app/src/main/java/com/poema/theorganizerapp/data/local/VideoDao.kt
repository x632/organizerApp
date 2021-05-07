package com.poema.theorganizerapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: Video)

    @Delete
    suspend fun deleteVideo(video:Video)

    @Query("SELECT * FROM videos")
    fun observeAllVideos(): LiveData<List<Video>>

}