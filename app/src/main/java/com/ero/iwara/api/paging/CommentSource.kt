package com.ero.iwara.api.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ero.iwara.model.comment.Comment
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.repo.MediaRepo

class CommentSource(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo,
    private val mediaType: MediaType,
    private val authorId: String,
    private val mediaId: String
): PagingSource<Int, Comment>() {
    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        val page = params.key ?: 0

        val response = mediaRepo.loadComment(sessionManager.session, mediaType, authorId, mediaId, page)

        return if(response.isSuccess()){
            LoadResult.Page(
                data = response.read().comments,
                prevKey = if(page <= 0) null else page - 1,
                nextKey = if(response.read().hasNext) page + 1 else null
            )
        }else {
            LoadResult.Error(Exception(response.errorMessage()))
        }
    }
}