package com.ero.iwara.result

import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MUser(
    val avatar: MFile?,
    val createdAt: String,
    val creatorProgram: Boolean,
    val email: String? = null,
    val followedBy: Boolean,
    val following: Boolean,
    val friend: Boolean,
    val hideSensitive: Boolean? = null,
    val id: String,
    val locale: String?,
    val name: String,
    val premium: Boolean,
    val premiumUntil: String? = null,
    val role: String,
    val seenAt: String?,
    val status: String,
    val updatedAt: String?,
    val username: String
){
    fun getAvatar(domain: String): String
    {
        if(avatar == null) return "https://www.iwara.tv/images/default-avatar.jpg"
        return avatar.getAvatar(domain)
    }
    fun mediaView(domain: String): MediaPreview
    {
        return MediaPreview(
            id = id,
            title = name,
            author = username,
            previewPic = getAvatar(domain),
            animatePic = "",
            likes = "0",
            watches = "0",
            mediaId = id,
            type = MediaType.USER
        )
    }
}