package com.ero.iwara.ui.screen.index.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ero.iwara.api.paging.MediaSource
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.subscribe
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
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager
) : ViewModel() {
    val value: MutableStateFlow<MediaQueryParam> = MutableStateFlow(
        MediaQueryParam(
            SortType.TREND,
            MediaType.VIDEO, emptyList()
        ))

    // Pager: 视频查询参数
    val flow: StateFlow<MediaQueryParam> = value.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    val pager: Flow<PagingData<MediaPreview>> = value.flatMapLatest { // 当 currentParams 变化时，会取消旧的 Pager 并创建一个新的
            Pager(
                config = PagingConfig(pageSize = 32, initialLoadSize = 32, prefetchDistance = 8),
                pagingSourceFactory = {
                    // 每次都创建一个新的 MediaSource 实例，并传入当前最新的参数
                    MediaSource(
                        it.sort,
                        it.type,
                        it.tags,
                        mediaRepo,
                        sessionManager
                    )
                }
            ).flow
        }.cachedIn(viewModelScope) // cachedIn 是重要的
    fun update(sort: SortType, type: MediaType, tags: List<String>)
    {
        value.update {
            it.copy(sort = sort, type = type, tags = tags.toList())
        }
    }

}