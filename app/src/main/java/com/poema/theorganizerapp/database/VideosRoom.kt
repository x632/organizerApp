package com.poema.theorganizerapp.database

import android.content.Context
import androidx.room.Room

object VideosRoom {
    private lateinit var INSTANCE: AppDatabase

    fun getInstance(context: Context): AppDatabase {
        if (!VideosRoom::INSTANCE.isInitialized) {
            synchronized(AppDatabase::class) {
                if (!VideosRoom::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java,
                        "VideosRoom").fallbackToDestructiveMigration().build()
                }
            }
        }
        return INSTANCE
    }
}