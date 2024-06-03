package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.local.database.StoryEntityDatabase
import com.example.storyapp.data.local.repository.StoryEntityRepository
import com.example.storyapp.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryEntityRepository {
        val database = StoryEntityDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryEntityRepository(database, apiService)
    }
}