package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MAccessToken(
    val accessToken: String
)