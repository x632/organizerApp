package com.poema.theorganizerapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [Video::class],
    version = 1
)

abstract class VideoDatabase :RoomDatabase(){
    abstract fun videoDao() : VideoDao
}