package com.example.storyapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.local.StoryEntity
import com.example.storyapp.data.local.repository.StoryEntityRepository

class StoryViewModel(private val storyEntityRepository: StoryEntityRepository) : ViewModel() {

    @ExperimentalPagingApi
    fun getPagingStory(token: String): LiveData<PagingData<StoryEntity>> {
        return storyEntityRepository.getPagingStory(token).cachedIn(viewModelScope)
    }
}