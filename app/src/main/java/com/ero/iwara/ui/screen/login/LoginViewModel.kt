package com.ero.iwara.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ero.iwara.DatabaseManager
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.postFlowEvent
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.repo.UserRepo
import com.ero.iwara.sharedPreferencesOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager,
    private val databaseManager: DatabaseManager
): ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var token by mutableStateOf("")
    var accessToken by mutableStateOf("")
    var isLoginState by mutableStateOf(false)
    var errorContent by mutableStateOf("")

    init {
        val sharedPreferences = sharedPreferencesOf("session")
        email = sharedPreferences.getString("email","")!!
        password = sharedPreferences.getString("password","")!!
    }

    fun login(result: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            isLoginState = true
            // save
            val sharedPreferences = sharedPreferencesOf("session")
            sharedPreferences.edit {
                putString("email", email)
                putString("password", password)
            }

            val tokenResponse = userRepo.login(email, password)


            // call event
            if(tokenResponse.isSuccess()){
                token = tokenResponse.read()
                val accessResponse = userRepo.getToken(token)
                if(accessResponse.isSuccess())
                {
                    accessToken = accessResponse.read()
                    sessionManager.update(token, accessToken)
                    databaseManager.saveUser(email, password, token, accessToken)
                    postFlowEvent(AppEvent.UserLoggedInEvent(email, password))
                    result(true)
                }
                else{
                    errorContent = accessResponse.errorMessage()
                    result(false)
                }
            }else {
                errorContent = tokenResponse.errorMessage()
                result(false)
            }
            // call back
            isLoginState = false
        }
    }
}