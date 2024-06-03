package com.example.storyapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapp.data.local.RemoteKeysDao
import com.example.storyapp.data.local.StoryEntity
import com.example.storyapp.data.local.StoryEntityDao

@Database(entities = [StoryEntity::class,RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoryEntityDatabase : RoomDatabase() {
    abstract fun storyentityDao(): StoryEntityDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryEntityDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryEntityDatabase {
            if (INSTANCE == null) {
                synchronized(StoryEntityDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        StoryEntityDatabase::class.java,
                        "storyy.db"
                    ).build()
                }
            }
            return INSTANCE as StoryEntityDatabase
        }
    }
}