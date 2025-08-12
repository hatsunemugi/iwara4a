package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MHeader(
    val id: String,
    val type: String,
    val path: String,
    val name: String,
    val mime: String,
    val size: Int,
    val width: Int?,
    val height: Int?,
    val duration: Int?,
    val numThumbnails: Int?,
    val animatedPreview: Boolean,
    val createdAt: String,
    val updatedAt: String
)