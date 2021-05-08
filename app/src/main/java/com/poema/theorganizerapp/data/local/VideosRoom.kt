package com.poema.theorganizerapp.data.local

import android.content.Context
import androidx.room.Room

object VideosRoom {
    private lateinit var INSTANCE: AppDatabase

    fun getInstance(context: Context): AppDatabase {
        if (!::INSTANCE.isInitialized) {
            synchronized(AppDatabase::class) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java,
                        "VideosRoom").fallbackToDestructiveMigration().build()
                }
            }
        }
        return INSTANCE
    }
}