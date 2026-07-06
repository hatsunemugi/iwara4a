package com.ero.iwara.repo

import androidx.annotation.IntRange
import com.ero.iwara.api.IwaraApi
import com.ero.iwara.api.Response
import com.ero.iwara.model.index.MediaList
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.index.SubscriptionList
import com.ero.iwara.model.index.TagList
import com.ero.iwara.model.session.Session
import javax.inject.Inject

class MediaRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {
    suspend fun getTag(filter: String, page: Int): Response<TagList> =
        iwaraApi.getTag(filter, page)

    suspend fun getSubscriptionList(
        type: MediaType,
        @IntRange(from = 0) page: Int
    ): Response<SubscriptionList> = iwaraApi.getSubscriptionList(type, page)

    suspend fun getMediaList(
        type: MediaType,
        page: Int,
        sort: SortType,
        tags: List<String>
    ) = iwaraApi.getMediaList(type, page, sort, tags)

    suspend fun getImageDetail(imageId: String) =
        iwaraApi.getImagePageDetail(imageId)

    suspend fun getVideoDetail(videoId: String) =
        iwaraApi.getVideoPageDetail(videoId)

    suspend fun like(like: Boolean, link: String) =
        iwaraApi.like(like, link)

    suspend fun follow(follow: Boolean, link: String) =
        iwaraApi.follow(follow, link)

    suspend fun loadComment(mediaType: MediaType, authorId: String, mediaId: String, page: Int) =
        iwaraApi.getCommentList(mediaType, authorId, mediaId, page)

    suspend fun search(query: String, page: Int, type: MediaType): Response<MediaList> =
        iwaraApi.search(query, page, type)/**/
}