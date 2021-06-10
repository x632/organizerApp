package com.poema.theorganizerapp.di

import android.content.Context
import androidx.room.Room
import com.poema.theorganizerapp.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context,
            AppDatabase::class.java,
            "VideosRoom")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideVideoDao(dbR: AppDatabase) = dbR.videoDao()

}
