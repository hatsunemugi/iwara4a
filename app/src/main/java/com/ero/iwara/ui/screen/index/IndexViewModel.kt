package com.ero.iwara.ui.screen.index

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.stroage.Config
import com.ero.iwara.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val userRepo: UserRepo,
    config: Config
) : ViewModel() {
    var id by config.id
    var email by config.email
    var avatar by config.avatar
    var nickname by config.nickname
    var username by config.username
    var debug by config.debug
    var self by config.user
    var ready by config.ready
    var tag by mutableStateOf("")
    var sort by mutableStateOf(SortType.TREND)
    var type by mutableStateOf(MediaType.VIDEO)
    var tags = mutableStateListOf<String>()
    var page: ()->Int = { 1 }
    var video: ()->Unit = {}
    var image: ()->Unit = {}
    var sub: ()->Unit = {}

    fun search()
    {
        when(page()){
            0 -> video()
            1 -> sub()
            2 -> image()
        }
    }

    fun refreshSelf() = viewModelScope.launch {
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