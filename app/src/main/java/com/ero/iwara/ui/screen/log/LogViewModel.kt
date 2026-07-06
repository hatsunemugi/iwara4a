package com.ero.iwara.ui.screen.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ero.iwara.param.BaseParam
import com.ero.iwara.stroage.dao.LogDao
import com.ero.iwara.stroage.entity.LogBase
import com.ero.iwara.ui.screen.base.IViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LogViewModel @Inject constructor(
    private val dao: LogDao,
) : ViewModel(), IViewModel<LogBase> {
    val load = MutableStateFlow(0)
    val page = MutableStateFlow(0)
    val count = MutableStateFlow(0)
    val param: MutableStateFlow<BaseParam> = MutableStateFlow(BaseParam(1,20,"logs", emptyList(), emptyList()))
    var scroll: (Int)->Unit = {}
    val pages: StateFlow<Int> = combine(count.asStateFlow(), param.asStateFlow()) { count, param ->
        (count + param.size - 1) / param.size
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        1
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    override val pager: Flow<PagingData<LogBase>> = param.flatMapLatest { // 当 currentParams 变化时，会取消旧的 Pager 并创建一个新的
        Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 10),
            pagingSourceFactory = {
                // 每次都创建一个新的 MediaSource 实例，并传入当前最新的参数
                LogSource(
                    load,
                    page,
                    count,
                    it,
                    dao
                )
            }
        ).flow
    }.cachedIn(viewModelScope) // cachedIn 是重要的

    fun setPage(value: Int)
    {
        val index = param.value.size * (value - 1)
        scroll(index)
        if(index < load.value) page.value = value
    }
    fun setSize(value: Int)
    {
        param.update { it.copy(size = value) }
    }
    fun remove()
    {
        viewModelScope.launch {
            dao.clear()
        }
    }
}