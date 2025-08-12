package com.ero.iwara.result

import android.os.Build
import androidx.annotation.RequiresApi
import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.util.query
import kotlinx.serialization.Serializable
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.HexFormat

@Serializable
data class MVideo(
    val body: String?,
    val createdAt: String,
    val customThumbnail: MFile?,
    val embedUrl: String?,
    val file: MFile?,
    val fileUrl: String? = null,
    val id: String,
    val liked: Boolean,
    val numComments: Int,
    val numLikes: Int,
    val numViews: Int,
    val private: Boolean,
    val rating: String,
    val slug: String?,
    val status: String,
    val tags: List<MTag>,
    val thumbnail: Int,
    val title: String,
    val unlisted: Boolean,
    val updatedAt: String,
    val user: MUser
){
    fun mediaView(domain: String): MediaPreview
    {
        return MediaPreview(
            id = id,
            title = title,
            author = user.name,
            previewPic = getPreviewPic(domain),
            animatePic = getAnimatePic(domain),
            likes = numLikes.toString(),
            watches = numViews.toString(),
            mediaId = id,
            type = MediaType.VIDEO
        )
    }
    fun getPreviewPic(domain: String): String
    {
        return customThumbnail?.getThumbnail(domain)
            ?: "$domain/image/thumbnail/${file?.id}/thumbnail-${thumbnail.toString().padStart(2,'0')}.jpg"
    }
    fun getAnimatePic(domain: String): String
    {
        return "$domain/image/thumbnail/${id}/preview.webp"
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun version(): String
    {
        val input = "${file?.id}_${fileUrl.query()["expires"]}_5nFp9kmbNnHdAFhaqMvt"

        // 2. 获取 SHA-1 MessageDigest 实例
        val digest = MessageDigest.getInstance("SHA-1")

        // 3. 将输入字符串转换为字节数组 (使用 UTF-8 编码，这是 Web 中常见的做法)
        val bytes = input.toByteArray(StandardCharsets.UTF_8)

        // 4. 计算哈希值
        val hash = digest.digest(bytes)

        // 5. 将字节数组转换为十六进制字符串
        return HexFormat.of().formatHex(hash)
    }
}
