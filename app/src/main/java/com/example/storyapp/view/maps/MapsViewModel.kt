package com.example.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.local.StoryEntity
import com.example.storyapp.data.local.repository.StoryEntityRepository

class MapsViewModel(private val storyEntityRepository: StoryEntityRepository) : ViewModel(){

    val stories: LiveData<List<StoryEntity>> = storyEntityRepository.listStory

    val isLoadingStory: LiveData<Boolean> = storyEntityRepository.isLoadingStory

    val storyStatus: LiveData<String> = storyEntityRepository.storyStatus

    var isErrorStory: Boolean = false

    init {
        storyStatus.observeForever { status ->
            isErrorStory = status != "Stories fetched successfully"
        }
    }

    fun getListStoryLocation(token: String) {
        storyEntityRepository.getStory(token)
    }
}