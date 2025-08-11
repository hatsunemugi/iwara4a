package com.ero.iwara.ui.screen.index

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ero.iwara.DatabaseManager
import com.ero.iwara.api.paging.MediaSource
import com.ero.iwara.api.paging.SubscriptionsSource
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.subscribe
import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaQueryParam
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.model.user.Self
import com.ero.iwara.repo.MediaRepo
import com.ero.iwara.repo.UserRepo
import com.ero.iwara.sharedPreferencesOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val mediaRepo: MediaRepo,
    private val databaseManager: DatabaseManager,
    private val sessionManager: SessionManager
) : ViewModel() {
    var self by mutableStateOf(Self.GUEST)
    var email by mutableStateOf("")
    var loadingSelf by mutableStateOf(false)

    // Pager: 视频查询参数
    private val _videoQueryParamStateFlow = MutableStateFlow(MediaQueryParam(SortType.TREND, emptyList())) // 初始值
    val videoQueryParamState: StateFlow<MediaQueryParam> = _videoQueryParamStateFlow.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    val videoPager: Flow<PagingData<MediaPreview>> = _videoQueryParamStateFlow
        .flatMapLatest { // 当 currentParams 变化时，会取消旧的 Pager 并创建一个新的
            Pager(
                config = PagingConfig(pageSize = 32, initialLoadSize = 32),
                pagingSourceFactory = {
                    // 每次都创建一个新的 MediaSource 实例，并传入当前最新的参数
                    MediaSource(
                        MediaType.VIDEO,
                        mediaRepo,
                        sessionManager,
                        it // 使用从 StateFlow 来的最新参数
                    )
                }
            ).flow
        }.cachedIn(viewModelScope) // cachedIn 是重要的
    fun updateVideoSort(sort: SortType) {
        _videoQueryParamStateFlow.update {
            it.copy(sort = sort)
        }
    }

    fun updateVideoTags(tags: List<String>) {
        _videoQueryParamStateFlow.update {
            it.copy(tags = tags)
        }
    }

    // Pager: 订阅列表
    val subscriptionPager by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 32,
                initialLoadSize = 32,
                prefetchDistance = 8
            )
        ) {
            SubscriptionsSource(
                sessionManager,
                mediaRepo
            )
        }.flow.cachedIn(viewModelScope)
    }

    // 图片列表
    private val _imageQueryParamStateFlow = MutableStateFlow(MediaQueryParam(SortType.TREND, emptyList())) // 初始值
    val imageQueryParamState: StateFlow<MediaQueryParam> = _imageQueryParamStateFlow.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    val imagePager: Flow<PagingData<MediaPreview>> = _imageQueryParamStateFlow
        .flatMapLatest { //当 currentParams 变化时，会取消旧的 Pager 并创建一个新的
            Pager(
                config = PagingConfig(pageSize = 32, initialLoadSize = 32),
                pagingSourceFactory = {
                    // 每次都创建一个新的 MediaSource 实例，并传入当前最新的参数
                    MediaSource(
                        MediaType.IMAGE,
                        mediaRepo,
                        sessionManager,
                        it // 使用从 StateFlow 来的最新参数
                    )
                }
            ).flow
        }.cachedIn(viewModelScope) // cachedIn 是重要的
    fun updateImageSort(sort: SortType) {
        _imageQueryParamStateFlow.update {
            it.copy(sort = sort)
        }
    }

    fun updateImageTags(tags: List<String>) {
        _imageQueryParamStateFlow.update {
            it.copy(tags = tags)
        }
    }

    init {
        this.viewModelScope.subscribe<AppEvent.UserLoggedInEvent> {
            refreshSelf()
        }
        refreshSelf()
    }

    override fun onCleared() { }

    fun refreshSelf() = viewModelScope.launch {
        loadingSelf = true
        email = sharedPreferencesOf("session").getString("username","请先登录你的账号吧")!!
        val response = userRepo.getSelf(sessionManager.session)
        if (response.isSuccess()) {
            self = response.read()
            val sharedPreferences = sharedPreferencesOf("session")
            sharedPreferences.edit {
                putString("id", self.id)
            }
        }
        loadingSelf = false
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onLogin(loginEvent: LoginEvent) {
//        refreshSelf()
//    }
}