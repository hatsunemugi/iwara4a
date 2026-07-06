package com.ero.iwara.ui.screen.base

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface IViewModel<T : Any>{
    val pager: Flow<PagingData<T>>
}