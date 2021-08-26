package com.poema.theorganizerapp.di

import android.content.Context
import androidx.room.Room
import com.poema.theorganizerapp.database.AppDatabase
import com.poema.theorganizerapp.repositories.MainRepository
import com.poema.theorganizerapp.repositories.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
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
    @Singleton
    fun provideVideoDao(dbR: AppDatabase) = dbR.videoDao()

   @Singleton
    @Provides
    fun provideMainRepository(
        dbRoom: Provider<AppDatabase>
    ) = MainRepository(dbRoom) as Repository

}
