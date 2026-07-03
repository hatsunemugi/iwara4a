package com.ero.iwara.api.result

import kotlinx.serialization.Serializable

@Serializable
data class MToken(
    val token: String
)