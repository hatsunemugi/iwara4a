package com.ero.iwara.ui.screen.log

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ero.iwara.param.BaseParam
import com.ero.iwara.stroage.dao.LogDao
import com.ero.iwara.stroage.entity.LogBase
import kotlinx.coroutines.flow.MutableStateFlow

class LogSource(
    private val load: MutableStateFlow<Int>,
    private val page: MutableStateFlow<Int>,
    private val count: MutableStateFlow<Int>,
    private val param: BaseParam,
    private val dao: LogDao,
): PagingSource<Int, LogBase>() {
    override fun getRefreshKey(state: PagingState<Int, LogBase>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LogBase> {
        val key = params.key ?: 0
        try{
            page.value = key + 1
            param.page = key + 1
            load.value = page.value * params.loadSize
            val query = param.sql()
            val list = dao.query(query)
            count.value = dao.count()
            val hasNext = param.hasNext(count.value)
            return LoadResult.Page(
                data = list,
                prevKey = if(key <= 0) null else key - 1,
                nextKey = if(hasNext) key + 1 else null
            )
        }catch (ex: Exception){
            return LoadResult.Error(ex)
        }
    }
}