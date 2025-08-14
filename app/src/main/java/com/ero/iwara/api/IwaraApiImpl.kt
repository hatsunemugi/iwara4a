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
import com.ero.iwara.model.session.Session
import com.ero.iwara.model.user.Self
import com.ero.iwara.model.user.UserData
import com.ero.iwara.param.UserLogin
import com.ero.iwara.result.MCount
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

    override suspend fun getSelf(session: Session): Response<Self> =
        autoRetry { iwaraParser.getSelf(session) }

    override suspend fun getTag(filter: String, page: Int): Response<TagList> =
        autoRetry { iwaraParser.getTag(filter, page) }

    override suspend fun getSubscriptionList(
        session: Session,
        type: MediaType,
        page: Int
    ): Response<SubscriptionList> = autoRetry { iwaraParser.getSubscriptionList(session, type, page) }

    override suspend fun getImagePageDetail(
        session: Session,
        imageId: String
    ): Response<ImageDetail> = autoRetry { iwaraParser.getImagePageDetail(session, imageId) }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun getVideoPageDetail(
        session: Session,
        videoId: String
    ): Response<VideoDetail> = iwaraParser.getVideoPageDetail(session, videoId)

    override suspend fun like(
        session: Session,
        like: Boolean,
        likeLink: String
    ): Response<LikeResponse> = iwaraParser.like(session, like, likeLink)

    override suspend fun follow(
        session: Session,
        follow: Boolean,
        followLink: String
    ): Response<FollowResponse> = iwaraParser.follow(session, follow, followLink)

    override suspend fun getCommentList(
        session: Session,
        mediaType: MediaType,
        authorId: String,
        mediaId: String,
        page: Int
    ): Response<CommentList> = autoRetry {
        iwaraParser.getCommentList(
            session,
            mediaType,
            authorId,
            mediaId,
            page
        )
    }

    override suspend fun getMediaList(
        session: Session,
        mediaType: MediaType,
        page: Int,
        sort: SortType,
        tags: List<String>
    ): Response<MediaList> = autoRetry {
        iwaraParser.getMediaList(
            session,
            mediaType,
            page,
            sort,
            tags
        )
    }

    override suspend fun getUser(session: Session, username: String): Response<UserData> = autoRetry {
        iwaraParser.getUser(
            session,
            username
        )
    }

    override suspend fun getCount(session: Session): Response<MCount> = iwaraParser.getCount(session)

    override suspend fun search(
        session: Session,
        query: String,
        page: Int,
        type: MediaType
    ): Response<MediaList> = autoRetry {
        iwaraParser.search(
            session,
            query,
            page,
            type
        )
    }
}