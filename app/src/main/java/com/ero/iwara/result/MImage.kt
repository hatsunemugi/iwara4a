package com.ero.iwara.result

import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MImage(
    val body: String?,
    val createdAt: String,
    val files: List<MFile>,
    val id: String,
    val liked: Boolean,
    val numComments: Int,
    val numImages: Int,
    val numLikes: Int,
    val numViews: Int,
    val rating: String,
    val slug: String?,
    val status: String,
    val tags: List<MTag>,
    val thumbnail: MFile,
    val title: String,
    val updatedAt: String,
    val user: MUser
)
{
    fun mediaView(domain: String): MediaPreview
    {
        return MediaPreview(
            id = id,
            title = title,
            author = user.name,
            previewPic = getPreviewPic(domain),
            animatePic = "",
            likes = numLikes.toString(),
            watches = numViews.toString(),
            mediaId = id,
            type = MediaType.IMAGE
        )
    }
    fun getPreviewPic(domain: String): String
    {
        return "$domain/image/thumbnail/${thumbnail.id}/${thumbnail.name}"
    }
}