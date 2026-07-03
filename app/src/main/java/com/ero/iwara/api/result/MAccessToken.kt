package com.ero.iwara.api.result

import kotlinx.serialization.Serializable

@Serializable
data class MAccessToken(
    val accessToken: String
)