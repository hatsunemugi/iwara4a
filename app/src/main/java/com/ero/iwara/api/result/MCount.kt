package com.ero.iwara.api.result

import kotlinx.serialization.Serializable

@Serializable
data class MCount(
    val friendRequests: Int,
    val messages: Int,
    val notifications: Int
)