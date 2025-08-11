package com.ero.iwara.model.user

data class Self(
    val id: String,
    val nickname: String,
    val profilePic: String
){
    companion object {
        val GUEST = Self("", "访客", "https://ecchi.iwara.tv/sites/all/themes/main/img/logo.png")
    }
}