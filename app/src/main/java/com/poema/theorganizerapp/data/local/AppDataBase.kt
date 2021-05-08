package com.poema.theorganizerapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Video::class], version = 6)
abstract class AppDatabase: RoomDatabase() {
    abstract fun videoDao(): VideoDao
}