package com.ero.iwara.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.ero.iwara.api.service.IwaraParser
import com.ero.iwara.model.comment.CommentList
import com.ero.iwara.model.detail.image.ImageDetail
import com.ero.iwara.model.detail.video.VideoDetail
import com.ero.iwara.model.flag.FollowResponse
import com.ero.iwara.model.flag.LikeResponse
import com.ero.iwara.model.index.MediaList
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.index.SubscriptionList
import com.ero.iwara.model.index.TagList
import com.ero.iwara.model.user.Self
import com.ero.iwara.model.user.UserData
import com.ero.iwara.param.UserLogin
import com.ero.iwara.api.result.MCount
import com.ero.iwara.util.autoRetry

/**
 * IwaraAPI接口的具体实现
 */
class IwaraApiImpl(
    private val iwaraParser: IwaraParser,
//    private val iwaraService: IwaraService
) : IwaraApi {
    override suspend fun login(username: String, password: String): Response<String> =
        iwaraParser.login(UserLogin(username, password))

    override suspend fun getToken(token: String): Response<String> =
        iwaraParser.getToken(token)

    override suspend fun getSelf(): Response<Self> =
        autoRetry { iwaraParser.getSelf() }

    override suspend fun getTag(filter: String, page: Int): Response<TagList> =
        autoRetry { iwaraParser.getTag(filter, page) }

    override suspend fun getSubscriptionList(
        type: MediaType,
        page: Int
    ): Response<SubscriptionList> = autoRetry { iwaraParser.getSubscriptionList(type, page) }

    override suspend fun getImagePageDetail(
        imageId: String
    ): Response<ImageDetail> = autoRetry { iwaraParser.getImagePageDetail(imageId) }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun getVideoPageDetail(
        videoId: String
    ): Response<VideoDetail> = iwaraParser.getVideoPageDetail(videoId)

    override suspend fun like(
        like: Boolean,
        likeLink: String
    ): Response<LikeResponse> = iwaraParser.like(like, likeLink)

    override suspend fun follow(
         follow: Boolean,
        followLink: String
    ): Response<FollowResponse> = iwaraParser.follow(follow, followLink)

    override suspend fun getCommentList(
        mediaType: MediaType,
        authorId: String,
        mediaId: String,
        page: Int
    ): Response<CommentList> = autoRetry {
        iwaraParser.getCommentList(
            mediaType,
            authorId,
            mediaId,
            page
        )
    }

    override suspend fun getMediaList(
        mediaType: MediaType,
        page: Int,
        sort: SortType,
        tags: List<String>
    ): Response<MediaList> = autoRetry {
        iwaraParser.getMediaList(
            mediaType,
            page,
            sort,
            tags
        )
    }

    override suspend fun getUser(username: String): Response<UserData> = autoRetry {
        iwaraParser.getUser(
            username
        )
    }

    override suspend fun getCount(): Response<MCount> = iwaraParser.getCount()

    override suspend fun search(
        query: String,
        page: Int,
        type: MediaType
    ): Response<MediaList> = autoRetry {
        iwaraParser.search(
            query,
            page,
            type
        )
    }
}