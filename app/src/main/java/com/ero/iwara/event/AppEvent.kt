package com.ero.iwara.event

class Events {
}

sealed interface AppEvent {
    data class UserLoggedInEvent(val username: String, val password: String) : AppEvent
    data class ItemFavoritedEvent(val itemId: String, val isFavorite: Boolean) : AppEvent
    data class GenericMessageEvent(val message: String) : AppEvent
    object NetworkStatusChanged : AppEvent // 无参数事件
    // 可以添加更多具体事件
}