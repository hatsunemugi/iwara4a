package com.ero.iwara.model.detail.video

import com.ero.iwara.result.MLinkInfo

data class VideoDetail(
    // 视频信息
    val id: String,
    val title: String,
    var links: List<MLinkInfo>,
    val likes: String,
    val watches: String,
    val postDate: String,
    val description: String,

    // 视频作者信息
    val authorPic: String,
    val authorName: String,
    val authorNickname: String,
    val authorId: String,

    // 作者的更多视频
    val moreVideo: List<MoreVideo>,

    // 是否关注
    val follow: Boolean,
    // 关注链接
    val followLink: String,

    // 是否喜欢
    var isLike: Boolean,
    val likeLink: String
){
    companion object {
        val LOADING = VideoDetail(
            "",
            "",
            listOf(),
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            emptyList(),
            true,
            "",
            true,
            ""
        )
    }
}

data class MoreVideo(
    val id: String,
    val title: String,
    val pic: String,
    val watches: String,
    val likes: String
)