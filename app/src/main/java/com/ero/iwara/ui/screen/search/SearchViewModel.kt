package com.ero.iwara.ui.screen.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ero.iwara.api.paging.SearchSource
import com.ero.iwara.model.index.MediaQueryParam
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.session.SessionManager
import com.ero.iwara.repo.MediaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo
) :ViewModel() {
    var query by mutableStateOf("")
    var type by mutableStateOf(MediaType.VIDEO)
    var searchParam by mutableStateOf(MediaQueryParam(SortType.DATE, emptyList()))

    val pager by lazy {
        Pager(
            PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5
            )
        ){
            SearchSource(
                mediaRepo,
                sessionManager,
                query,
                type,
                searchParam
            )
        }.flow.cachedIn(viewModelScope)
    }
}