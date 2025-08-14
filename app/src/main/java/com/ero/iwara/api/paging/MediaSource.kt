package com.ero.iwara.api.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.repo.MediaRepo

private const val TAG = "MediaSource"

class MediaSource(
    private val mediaSort: SortType,
    private val mediaType: MediaType,
    private val mediaTags: List<String>,
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager
): PagingSource<Int, MediaPreview>() {
    override fun getRefreshKey(state: PagingState<Int, MediaPreview>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaPreview> {
        val page = params.key ?: 0

        Log.i(TAG, "load: Trying to load media list: $page")

        val response = mediaRepo.getMediaList(sessionManager.session, mediaType, page, mediaSort, mediaTags)
        return if(response.isSuccess()){
            val data = response.read()
            Log.i(TAG, "load: Success load media list (data size=${data.mediaList.size}, hasNext=${data.hasNext})")
            LoadResult.Page(
                data = data.mediaList,
                prevKey = if(page <= 0) null else page - 1,
                nextKey = if(data.hasNext) page + 1 else null
            )
        } else {
            LoadResult.Error(Exception(response.errorMessage()))
        }
    }
}