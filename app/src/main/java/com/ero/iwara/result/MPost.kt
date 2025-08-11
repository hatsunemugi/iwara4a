package com.ero.iwara.result

import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MPost(
    val body: String?,
    val createdAt: String,
    val id: String,
    val numViews: Int,
    val title: String,
    val updatedAt: String,
    val user: MUser
){
    fun mediaView(domain: String): MediaPreview
    {
        return MediaPreview(
            id = id,
            title = title,
            author = user.name,
            previewPic = user.getAvatar(domain),
            animatePic = "",
            likes = "0",
            watches = numViews.toString(),
            mediaId = id,
            type = MediaType.POST
        )
    }
}