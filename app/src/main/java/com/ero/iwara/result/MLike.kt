package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MLike(
    val id: Int,
    val user: MUser,
    val createAt: String
)