package com.ero.iwara.model.user

data class Self(
    val id: String,
    var email: String,
    val avatar: String,
    val username: String,
    val nickname: String

){
    companion object {
        val GUEST = Self("", "", "https://www.iwara.tv/images/default-avatar.jpg","","访客")
    }
}