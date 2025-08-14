package com.ero.iwara.ui.screen.index.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ero.iwara.api.paging.SubscriptionsSource
import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaQueryParam
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.repo.MediaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SubViewModel @Inject constructor(
    private val repo: MediaRepo,
    private val sessionManager: SessionManager
) : ViewModel() {
    val value: MutableStateFlow<MediaType> = MutableStateFlow(MediaType.VIDEO)

    // Pager: 视频查询参数
    val flow: StateFlow<MediaType> = value.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    val pager: Flow<PagingData<MediaPreview>> = value.flatMapLatest { // 当 currentParams 变化时，会取消旧的 Pager 并创建一个新的
        Pager(
            config = PagingConfig(pageSize = 32, initialLoadSize = 32, prefetchDistance = 8),
            pagingSourceFactory = {
                // 每次都创建一个新的 MediaSource 实例，并传入当前最新的参数
                SubscriptionsSource(
                    it,
                    repo,
                    sessionManager,

                )
            }
        ).flow
    }.cachedIn(viewModelScope) // cachedIn 是重要的
    fun update(param: MediaType){
        value.value = param
    }
}