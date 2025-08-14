package com.ero.iwara.ui.screen.index.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ero.iwara.DatabaseManager
import com.ero.iwara.api.paging.TagSource
import com.ero.iwara.repo.MediaRepo
import com.ero.iwara.result.MTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val repo: MediaRepo,
    private val manager: DatabaseManager,
) : ViewModel() {
    private val value: MutableStateFlow<String> = MutableStateFlow("")

    // Pager: 视频查询参数
    val flow: StateFlow<String> = value.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    val pager: Flow<PagingData<MTag>> = value.flatMapLatest { // 当 currentParams 变化时，会取消旧的 Pager 并创建一个新的
        Pager(
            config = PagingConfig(pageSize = 32, initialLoadSize = 32, prefetchDistance = 8),
            pagingSourceFactory = {
                // 每次都创建一个新的 MediaSource 实例，并传入当前最新的参数
                TagSource(it, repo, manager)
            }
        ).flow
    }.cachedIn(viewModelScope) // cachedIn 是重要的

    fun update(param: String){
        value.value = param
    }
}