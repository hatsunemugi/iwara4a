package com.ero.iwara.model.token

import kotlinx.serialization.Serializable

@Serializable
data class AccessToken(
    val accessToken: String
)