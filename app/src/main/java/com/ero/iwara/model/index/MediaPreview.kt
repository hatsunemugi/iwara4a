package com.ero.iwara.model.index

/**
 * 代表了一个封面预览
 */
data class MediaPreview(
    //主键
    val id: String,
    // 标题
    val title: String,
    // 作者
    val author: String,
    // 封面
    val previewPic: String,
    // 关键帧gif
    val animatePic: String,
    // 喜欢数
    val likes: String,
    // 播放量
    val watches: String,
    // 类型
    val type: MediaType,
    // 图片ID
    val mediaId: String
)

enum class SearchType(val value: String)
{
    VIDEO("video"),
    IMAGE("image"),
    FORUM("forum"),
    POST("post"),
    USER("user")
}

enum class MediaType(val value: String) {
    VIDEO("video"),
    IMAGE("image"),
    FORUM("forum"),
    POST("post"),
    USER("user")

}