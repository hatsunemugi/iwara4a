package com.ero.iwara.param

import kotlinx.serialization.Serializable

@Serializable
data class UserLogin(
    val email: String,
    val password: String
)