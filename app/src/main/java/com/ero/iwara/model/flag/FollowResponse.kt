package com.ero.iwara.model.flag

import kotlinx.serialization.Serializable

@Serializable
data class FollowResponse(
    val status: Boolean,
)