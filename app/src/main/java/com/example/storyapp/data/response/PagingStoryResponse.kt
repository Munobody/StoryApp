package com.example.storyapp.data.response

import com.example.storyapp.data.local.StoryEntity
import com.google.gson.annotations.SerializedName

data class PagingStoryResponse(
    @field:SerializedName("error")
    var error: String,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("listStory")
    var listStory: List<StoryEntity>
)