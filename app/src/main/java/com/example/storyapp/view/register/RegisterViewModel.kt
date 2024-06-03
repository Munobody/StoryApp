package com.example.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.local.repository.StoryEntityRepository

class RegisterViewModel(private val storyEntityRepository: StoryEntityRepository) : ViewModel() {

    val registerStatus: LiveData<String> = storyEntityRepository.registerStatus

    val isLoadingRegister: LiveData<Boolean> = storyEntityRepository.isLoadingRegister

    var isErrorRegister: Boolean = false

    init {
        registerStatus.observeForever { status ->
            isErrorRegister = status != "Account successfully created"
        }
    }

    fun register(name: String, email: String, password: String) {
        storyEntityRepository.register(name, email, password)
    }
}