package com.ero.iwara.api.result

import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MForum (
    val approved: Boolean,
    val body: String,
    val createdAt: String,
    val id: String,
    val replyNum: Int,
    val thread: MThread,
    val threadId: String,
    val updatedAt: String,
    val user: MUser
){
    fun mediaView(domain: String): MediaPreview
    {
        return MediaPreview(
            id = id,
            title = thread.title,
            author = user.name,
            previewPic = user.getAvatar(domain),
            animatePic = "",
            likes = "0",
            watches = thread.numViews.toString(),
            mediaId = id,
            type = MediaType.FORUM
        )
    }
}