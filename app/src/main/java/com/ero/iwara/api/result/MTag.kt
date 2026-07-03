package com.ero.iwara.api.result

import kotlinx.serialization.Serializable

@Serializable
data class MTag(
    val id: String,
    val type: String,
    val sensitive: Boolean
)