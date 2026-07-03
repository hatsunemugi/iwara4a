package com.ero.iwara.api.result

import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
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
    val slug: String?,
    val sticky: Boolean,
    val title: String,
    val updatedAt: String,
    val user: MUser?
){
    fun mediaView(domain: String): MediaPreview
    {
        return MediaPreview(
            id = id,
            title = title,
            author = user?.name ?: "未知用户",
            previewPic = user?.getAvatar(domain) ?: "https://www.iwara.tv/images/default-avatar.jpg",
            animatePic = "",
            likes = "0",
            watches = numViews.toString(),
            mediaId = id,
            type = MediaType.FORUM
        )
    }
}