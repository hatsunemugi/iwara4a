package com.ero.iwara.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.publish
import com.ero.iwara.stroage.Config
import com.ero.iwara.repo.UserRepo
import com.ero.iwara.stroage.dao.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UserRepo,
    config: Config,
    private val userDao: UserDao
): ViewModel() {
    var id by config.id
    var email by config.email
    var avatar by config.avatar
    var nickname by config.nickname
    var username by config.username
    var debug by config.debug
    var self by config.user
    var ready by config.ready
    var password by config.password
    var token by config.token
    var accessToken by config.accessToken
    var isLoginState by mutableStateOf(false)
    var errorContent by mutableStateOf("")

    fun login(account: String, passwd: String, result: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            isLoginState = true

            val tokenResponse = userRepo.login(account, passwd)


            // call event
            if(tokenResponse.isSuccess()){
                token = tokenResponse.read()
                email = account
                password = passwd
                val accessResponse = userRepo.getToken(token)
                if(accessResponse.isSuccess())
                {
                    accessToken = accessResponse.read()
                    userDao.upsert(email, password, token, accessToken)
                    result(true)
                    refresh()
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
    fun refresh() = CoroutineScope(Dispatchers.IO).launch {
        ready = false
        self.email = email
        val response = userRepo.getSelf()
        if (response.isSuccess()) {
            val user = response.read()
            self = user
            id = user.id
            email = user.email
            avatar = user.avatar
            nickname = user.nickname
            username = user.username
        }
        ready = true
    }
}