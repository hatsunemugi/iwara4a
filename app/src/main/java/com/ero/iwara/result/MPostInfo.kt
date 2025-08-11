package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MPostInfo(
    val approved: Boolean,
    val body: String,
    val createdAt: String,
    val id: String,
    val replyNum: Int,
    val thread: String?,
    val threadId: String,
    val updatedAt: String,
    val user: MUser
)