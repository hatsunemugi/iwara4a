package com.ero.iwara.ui.screen.index

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.postFlowEvent
import com.ero.iwara.event.subscribe
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.model.user.Self
import com.ero.iwara.repo.UserRepo
import com.ero.iwara.sharedPreferencesOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
) : ViewModel() {
    var self by mutableStateOf(Self.GUEST)
    var loadingSelf by mutableStateOf(false)
    var tag by mutableStateOf("")
    var sort by mutableStateOf(SortType.TREND)
    var type by mutableStateOf(MediaType.VIDEO)
    var tags = mutableStateListOf<String>()
    var page: ()->Int = { 1 }
    var video: ()->Unit = {}
    var image: ()->Unit = {}
    var sub: ()->Unit = {}

    init {
        viewModelScope.subscribe<AppEvent.UserLoggedInEvent> {
            refreshSelf()
        }
        refreshSelf()
    }

    override fun onCleared() { }
    fun search()
    {
        when(page()){
            0 -> video()
            1 -> sub()
            2 -> image()
        }
    }

    fun refreshSelf() = viewModelScope.launch {
        loadingSelf = true
        self.email = sharedPreferencesOf("session").getString("email","请先登录你的账号吧")!!
        val response = userRepo.getSelf(sessionManager.session)
        if (response.isSuccess()) {
            val user = response.read()
            self = user
            val sharedPreferences = sharedPreferencesOf("session")
            sharedPreferences.edit {
                putString("id", user.id)
                putString("avatar", user.avatar)
                putString("nickname", user.nickname)
                putString("username", user.username)
            }
            postFlowEvent(AppEvent.UserInfoEvent(user))
        }
        loadingSelf = false
    }
}