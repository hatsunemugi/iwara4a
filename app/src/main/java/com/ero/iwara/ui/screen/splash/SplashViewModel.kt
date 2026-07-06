package com.ero.iwara.ui.screen.splash

import androidx.lifecycle.ViewModel
import com.ero.iwara.stroage.Config
import com.ero.iwara.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepo: UserRepo,
    config: Config
): ViewModel() {
    val token by config.token
    suspend fun isLogin(): Boolean {
        if(token.isEmpty()) return false
        val response = userRepo.getCount()
        return response.isSuccess()
    }
}