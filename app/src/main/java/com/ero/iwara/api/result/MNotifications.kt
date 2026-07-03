package com.ero.iwara.api.result

import kotlinx.serialization.Serializable

@Serializable
data class MNotifications(
    val mention: Boolean,
    val reply: Boolean,
    val comment: Boolean
)