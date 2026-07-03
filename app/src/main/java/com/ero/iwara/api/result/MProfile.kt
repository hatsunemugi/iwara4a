package com.ero.iwara.api.result

import kotlinx.serialization.Serializable

@Serializable
data class MProfile(
    val body: String?,
    val createdAt: String,
    val header: MHeader?,
    val updatedAt: String,
    val user: MUser?
)