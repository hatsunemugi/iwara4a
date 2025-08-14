package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MComment(
    val id: String,
    val user: MUser?,
    val approved: Boolean,
    val body: String,
    val imageId: String? = null,
    val videoId: String?,
    val numReplies: Int,
    val parent: MComment?,
    val createdAt: String,
    val updatedAt: String,
)