package com.ero.iwara.model.user

data class UserData(
    val userId: String,
    val username: String,
    val pic: String,
    val joinDate: String,
    val lastSeen: String,
    val about: String
){
    companion object{
        val LOADING = UserData(
            "",
            "",
            "",
            "",
            "",
            ""
        )
    }
}