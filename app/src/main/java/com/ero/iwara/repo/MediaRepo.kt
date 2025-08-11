package com.ero.iwara.repo

import androidx.annotation.IntRange
import com.ero.iwara.api.IwaraApi
import com.ero.iwara.api.Response
import com.ero.iwara.model.index.MediaList
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.index.SubscriptionList
import com.ero.iwara.model.session.Session
import javax.inject.Inject

class MediaRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {
    suspend fun getSubscriptionList(
        session: Session,
        @IntRange(from = 0) page: Int
    ): Response<SubscriptionList> = iwaraApi.getSubscriptionList(session, page)

    suspend fun getMediaList(
        session: Session,
        type: MediaType,
        page: Int,
        sort: SortType,
        tags: List<String>
    ) = iwaraApi.getMediaList(session, type, page, sort, tags)

    suspend fun getImageDetail(session: Session, imageId: String) =
        iwaraApi.getImagePageDetail(session, imageId)

    suspend fun getVideoDetail(session: Session, videoId: String) =
        iwaraApi.getVideoPageDetail(session, videoId)

    suspend fun like(session: Session, like: Boolean, link: String) =
        iwaraApi.like(session, like, link)

    suspend fun follow(session: Session, follow: Boolean, link: String) =
        iwaraApi.follow(session, follow, link)

    suspend fun loadComment(session: Session, mediaType: MediaType, authorId: String, mediaId: String, page: Int) =
        iwaraApi.getCommentList(session, mediaType, authorId, mediaId, page)

    suspend fun search(session: Session, query: String, page: Int, type: MediaType, sort: SortType): Response<MediaList> = iwaraApi.search(
        session, query, page, type, sort
    )/**/
}