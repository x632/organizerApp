package com.poema.theorganizerapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.poema.theorganizerapp.database.VideoDao
import com.poema.theorganizerapp.models.Video

@Database(entities = [Video::class], version = 6)
abstract class AppDatabase: RoomDatabase() {
    abstract fun videoDao(): VideoDao
}