package com.ero.iwara.result

import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class MFile(
    val animatedPreview: Boolean,
    val duration: Int?,
    val height: Int?,
    val width: Int?,
    val id: String,
    val mime: String,
    val name: String,
    val numThumbnails: Int?,
    val path: String,
    val size: Int,
    val type: String,
    val updatedAt: String,
    val createdAt: String,
)
{
    fun getThumbnail(domain: String): String{
        return "$domain/image/thumbnail/${id}/$name"
    }
    fun getLargeImage(domain: String): String
    {
        return "$domain/image/large/${id}/${name}"
    }
    fun getAvatar(domain: String): String
    {
        if(mime == "image/gif") return getOrigin(domain)
        if(mime == "image/jpeg" && name.endsWith("gif")) name.replace("gif", "jpg")
        return "$domain/image/avatar/${id}/${name}"
    }
    fun getOrigin(domain: String): String
    {
        return "$domain/image/original/${id}/${name}"
    }
}