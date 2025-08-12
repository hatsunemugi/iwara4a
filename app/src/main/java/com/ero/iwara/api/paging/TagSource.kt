package com.ero.iwara.api.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ero.iwara.DatabaseManager
import com.ero.iwara.repo.MediaRepo
import com.ero.iwara.result.MTag

class TagSource(
    private val filter: String,
    private val repo: MediaRepo,
    private val manager: DatabaseManager
): PagingSource<Int, MTag>() {
    override fun getRefreshKey(state: PagingState<Int, MTag>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MTag> {
        val page = params.key ?: 0


        val response = repo.getTag(filter, page)
        return if(response.isSuccess()){
            val data = response.read()
            data.tagList.forEach{ manager.saveTag(it.id, it.type, it.sensitive) }
            LoadResult.Page(
                data = data.tagList,
                prevKey = if(page <= 0) null else page - 1,
                nextKey = if(data.hasNext) page + 1 else null
            )
        } else {
            LoadResult.Error(Exception(response.errorMessage()))
        }
    }
}