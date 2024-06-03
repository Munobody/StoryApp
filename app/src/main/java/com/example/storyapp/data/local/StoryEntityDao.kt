package com.example.storyapp.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(story: List<StoryEntity>)

    @Query("SELECT * FROM StoryEntity")
    fun getAllPagingStory(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM StoryEntity")
    fun getAllStory(): List<StoryEntity>

    @Query("DELETE FROM StoryEntity")
    fun delete()
}