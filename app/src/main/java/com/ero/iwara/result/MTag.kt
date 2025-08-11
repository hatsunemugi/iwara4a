package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MTag(
    val id: String,
    val type: String,
    val sensitive: Boolean
)