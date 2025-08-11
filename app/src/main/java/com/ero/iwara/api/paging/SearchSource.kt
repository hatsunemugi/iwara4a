package com.ero.iwara.api.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaQueryParam
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.repo.MediaRepo

private const val TAG = "SearchSource"

class SearchSource(
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager,
    private val query: String,
    private val type: MediaType,
    private val mediaQueryParam: MediaQueryParam
) : PagingSource<Int, MediaPreview>() {
    override fun getRefreshKey(state: PagingState<Int, MediaPreview>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaPreview> {
        val page = params.key ?: 0

        if(query.isBlank()){
            return LoadResult.Page(
                data = emptyList(),
                prevKey = if (page <= 0) null else page - 1,
                nextKey = null
            )
        }

        Log.i(TAG, "load: Trying search: $page")

        val response = mediaRepo.search(
            sessionManager.session,
            query,
            page,
            type,
            mediaQueryParam.sort
        )
        return if (response.isSuccess()) {
            val data = response.read()
            Log.i(
                TAG,
                "load: Success load search list (datasize=${data.mediaList.size}, hasNext=${data.hasNext})"
            )
            LoadResult.Page(
                data = data.mediaList,
                prevKey = if (page <= 0) null else page - 1,
                nextKey = if (data.hasNext) page + 1 else null
            )
        } else {
            LoadResult.Error(Exception(response.errorMessage()))
        }
    }
}