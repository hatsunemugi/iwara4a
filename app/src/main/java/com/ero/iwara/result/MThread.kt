package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MThread(
    val approved: Boolean,
    val createdAt: String,
    val id: String,
    val lastPost: MPostInfo?,
    val locked: Boolean,
    val numPosts: Int,
    val numViews: Int,
    val section: String,
    val slug: String,
    val sticky: Boolean,
    val title: String,
    val updatedAt: String,
    val user: MUser
)