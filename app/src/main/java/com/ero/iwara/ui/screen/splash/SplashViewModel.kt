package com.ero.iwara.ui.screen.splash

import androidx.lifecycle.ViewModel
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
): ViewModel() {
    suspend fun isLogin(): Boolean {
        if(sessionManager.session.key.isEmpty()) return false
        val response = userRepo.getCount(sessionManager.session)
        return response.isSuccess()
    }
}