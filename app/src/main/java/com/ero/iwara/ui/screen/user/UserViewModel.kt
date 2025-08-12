package com.ero.iwara.ui.screen.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.subscribe
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.model.user.UserData
import com.ero.iwara.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo
): ViewModel(){
    var loading by mutableStateOf(false)
    var error by mutableStateOf(false)
    var userData by mutableStateOf(UserData.LOADING)

    fun load(username: String){
        viewModelScope.launch {
            loading = true
            error = false

            val response = userRepo.getUser(sessionManager.session, username)
            if(response.isSuccess()){
                userData = response.read()
            } else {
                error = true
            }

            loading = false
        }
    }

    fun isLoaded() = userData != UserData.LOADING
}