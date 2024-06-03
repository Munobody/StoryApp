package com.example.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.local.repository.StoryEntityRepository
import com.example.storyapp.data.response.LoginResponse

class LoginViewModel(private val storyEntityRepository: StoryEntityRepository) : ViewModel() {

    val loginStatus: LiveData<String> = storyEntityRepository.loginStatus

    val isLoadingLogin: LiveData<Boolean> = storyEntityRepository.isLoadingLogin

    var isErrorLogin: Boolean = false

    init {
        loginStatus.observeForever { status ->
            isErrorLogin = status != "Welcome ${login.value?.loginResult?.name}, To Story Apps"
        }
    }

    val login: LiveData<LoginResponse> = storyEntityRepository.loginUser

    fun login(email: String, password: String) {
        storyEntityRepository.login(email, password)
    }
}