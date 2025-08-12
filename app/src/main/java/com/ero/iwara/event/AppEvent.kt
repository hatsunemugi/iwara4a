package com.ero.iwara.event

import com.ero.iwara.model.user.Self

sealed interface AppEvent {
    data class UserLoggedInEvent(val username: String, val password: String) : AppEvent
    data class UserInfoEvent(val model: Self) : AppEvent
    data class ItemFavoritedEvent(val itemId: String, val isFavorite: Boolean) : AppEvent
    data class GenericMessageEvent(val message: String) : AppEvent
    object NetworkStatusChanged : AppEvent // 无参数事件
    // 可以添加更多具体事件
}