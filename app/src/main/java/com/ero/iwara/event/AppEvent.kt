package com.ero.iwara.event

import androidx.lifecycle.ViewModel
import com.ero.iwara.model.user.Self


sealed interface AppEvent {
    data class UserLoggedInEvent(val username: String, val password: String) : AppEvent
    data class UserInfoEvent(val model: Self) : AppEvent
    data class BaseEvent<T,E>(val target: T? = null, val value: E):  AppEvent
    data class TagEvent<T: ViewModel>(val value: String, val target: T? = null):  AppEvent
    data class GenericMessageEvent(val message: String, val copy: Boolean) : AppEvent
    object NetworkStatusChanged : AppEvent // 无参数事件
    // 可以添加更多具体事件
}