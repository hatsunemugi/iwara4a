package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MLinkInfo(
    val id: String,
    val name: String,
    var src: MLink,
    val type: String,
    val createdAt: String,
    val updatedAt: String
)